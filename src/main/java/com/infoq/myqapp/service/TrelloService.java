package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.*;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.repository.TrelloMemberRepository;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.domain.Action.Data;
import com.julienvey.trello.impl.TrelloImpl;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private TrelloMemberRepository trelloMemberRepository;

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

    public List<TList> getLists(Token accessToken) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        Board board = trelloApi.getBoard(trelloBoardForStatsId);

        return board.fetchLists(arg("cards", "open"));
    }

    public TList getList(Token accessToken, String listId) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        return trelloApi.getList(listId, arg("cards", "open"));
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

    public Member getUserInfo(Token accessToken) {
        return getUserInfo("me", accessToken);
    }

    public List<TrelloHeartbeat> getMemberHeartbeat(Member member, Token accessToken) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());

        // get a list of all card moving actions where the member is involved
        List<CardWithActions> cards = trelloApi.getBoardMemberActivity(trelloBoardForStatsId,
                member.getId(), "updateCard:idList");
        List<TrelloHeartbeat> result = new ArrayList<>(cards.size());
        for (CardWithActions card : cards) {
            if (result.size() >= 5) {
                break;
            }

            // some false positives are to be expected... resolve them below
            // exclude mentored content
            if (StatsService.hasLabel(card, StatsService.MENTORAT)) {
                continue;
            }

            // exclude content where member is not the author (1st position in card)
            if (card.getIdMembers().size() < 1
                    || !card.getIdMembers().get(0).equals(member.getId())) {
                continue;
            }

            for (Action action : card.getActions()) {
                Data data = action.getData();
                // include only content where card was moved to A_VALIDER list
                if (data.getListAfter() != null
                        && data.getListAfter().getName().equals(StatsService.A_VALIDER)) {

                    TrelloHeartbeat hb = new TrelloHeartbeat(card, member, action.getDate());
                    result.add(hb);
                }
            }
        }

        return result;
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

            int i = 0;
            for (String memberId : card.getIdMembers()) {
                TrelloMember trelloMember = trelloMemberRepository.findOne(memberId);
                if (trelloMember != null) {
                    if (i == 0 && !content.isMentoring()) {
                        content.setAuthor(trelloMember);
                    } else if (i == 0) {
                        content.setValidator(trelloMember);
                        content.setMentor(trelloMember);
                    } else if (i == 1) {
                        content.setValidator(trelloMember);
                    }
                }
                i++;
            }

            List<String> urls = extractUrls(card.getDesc());
            for (String url : urls) {
                if (url.contains("github.com")) {
                    content.setGithubUrl(url);
                } else if (url.contains("infoq.com")) {
                    content.setInfoqUrl(url);
                }
            }

            validatedContents.add(content);
        }

        return validatedContents;
    }

    private List<String> extractUrls(String content) {
        List<String> result = new ArrayList<String>();
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
