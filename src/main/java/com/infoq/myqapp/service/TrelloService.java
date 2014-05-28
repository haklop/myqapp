package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.TrelloLabel;
import com.infoq.myqapp.domain.ValidatedContent;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.julienvey.trello.utils.ArgUtils.arg;

@Service
public class TrelloService {

    private static final Logger logger = LoggerFactory.getLogger(TrelloService.class);

    @Value("${editingprocess.add.board.id}")
    private String trelloBoardForAddingCardsId;

    @Value("${editingprocess.stats.board.id}")
    private String trelloBoardForStatsId;

    @Value("${editingprocess.list.validated.id}")
    private String trelloValidatedListId;

    @Value("${trello.oauth.key}")
    private String trelloKey;

    @Resource
    private FeedRepository feedRepository;

    public void addCardToTrello(FeedEntry feedEntry, Token accessToken) {
        FeedEntry entry = feedRepository.findOne(feedEntry.getLink());
        if (entry != null && entry.isAddedInTrello())
            throw new CardConflictException();

        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        Board board = trelloApi.getBoard(trelloBoardForAddingCardsId);

        List<TList> lists = board.fetchLists();

        Card cardToCreate = buildCardFromFeedEntry(feedEntry);
        Card card = lists.get(0).createCard(cardToCreate);

        card.addLabels(TrelloLabel.TRADUCTION.getLabelColor(), TrelloLabel.valueOf(feedEntry.getType().toUpperCase()).getLabelColor());
        logger.info("Card created on Trello, id is {}", card.getId());

        feedEntry.setAddedInTrello(true);
        feedEntry.setUrlTrello(card.getUrl());
        feedRepository.save(feedEntry);
    }

    public TList getList(Token accessToken, String listId) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        return trelloApi.getList(listId, arg("cards", "all"));
    }

    public TList getValidatedList(Token accesToken) {
        return getList(accesToken, trelloValidatedListId);
    }

    public Member getUserInfo(String username, Token accessToken) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        return trelloApi.getMemberInformation(username);
    }

    public List<Member> getMembers(Token accessToken) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        Board board = trelloApi.getBoard(trelloBoardForStatsId);

        return board.fetchMembers();
    }

    public List<ValidatedContent> enhancedValidatedContentList(TList list) {
        List<ValidatedContent> validatedContents = new ArrayList<>();
        for (Card card : list.getCards()) {
            ValidatedContent content = new ValidatedContent();
            content.setName(card.getName());
            content.setDateLastActivity(card.getDateLastActivity());
            content.setTrelloUrl(card.getUrl());

            content.setOriginal(false);
            content.setMentoring(false);
            content.setArticle(false);
            for (Label label : card.getLabels()) {
                switch (label.getName()) {
                    case "Articles":
                        content.setArticle(true);
                        break;
                    case "Original":
                        content.setOriginal(true);
                        break;
                    case "Mentorat":
                        content.setMentoring(true);
                        break;
                }
            }

            List<String> urls = extractUrls(card.getDesc());
            for (String url : urls) {
                if (url.contains("github.com")) {
                    content.setGithubUrl(url);
                } else if (url.contains("infoq.com")) {
                    content.setInfoqUrl(url);
                }
            }

            if (content.getInfoqUrl() != null) {
                String infoqNode = content.getInfoqUrl().substring(content.getInfoqUrl().lastIndexOf('/') + 1);
                if (content.isArticle()) {
                    content.setNode(infoqNode);
                } else {
                    content.setNode(new SimpleDateFormat("yyyy/MM").format(Calendar.getInstance().getTime()) + "/" + infoqNode);
                }
            }

            validatedContents.add(content);
        }

        return validatedContents;
    }

    private List<String> extractUrls(String content) {
        List<String> result = new ArrayList<>();
        String urlPattern = "((https?):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        while (m.find()) {
            result.add(content.substring(m.start(0), m.end(0)));
        }
        return result;
    }

    private Card buildCardFromFeedEntry(FeedEntry feedEntry) {
        Card cardToCreate = new Card();
        cardToCreate.setName(feedEntry.getTitle());
        cardToCreate.setDesc(feedEntry.getLink());
        return cardToCreate;
    }
}
