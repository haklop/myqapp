package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.TrelloLabel;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrelloService {

    private static final Logger LOG = LoggerFactory.getLogger(TrelloService.class);

    public static final String TEST_EDITING_PROCESS_BOARD_ID = "5182683ef43ba8a401000160";
    public static final String EDITING_PROCESS_BOARD_ID = "51499c4cb867d5eb5900678f";

    @Resource
    private FeedRepository feedRepository;

    public void addCardToTrello(FeedEntry feedEntry, Token accessToken) {
        FeedEntry entry = feedRepository.findOne(feedEntry.getLink());
        if (entry != null && entry.isAddedInTrello()) {
            throw new CardConflictException();
        }

        Trello trelloApi = new TrelloImpl(TrelloAuthenticationService.APPLICATION_KEY, accessToken.getToken());
        Board board = trelloApi.getBoard(TEST_EDITING_PROCESS_BOARD_ID);

        List<TList> lists = board.getLists();

        Card cardToCreate = buildCardFromFeedEntry(feedEntry);
        Card card = lists.get(0).createCard(cardToCreate);

        card.addLabels(TrelloLabel.TRADUCTION.getLabelColor(), TrelloLabel.valueOf(feedEntry.getType().toUpperCase()).getLabelColor());
        LOG.info("Card created on Trello, id is {}", card.getId());

        feedEntry.setAddedInTrello(true);
        feedEntry.setUrlTrello(card.getUrl());
        feedRepository.save(feedEntry);
    }

    public List<TList> getLists(Token accessToken) {
        Trello trelloApi = new TrelloImpl(TrelloAuthenticationService.APPLICATION_KEY, accessToken.getToken());
        Board board = trelloApi.getBoard(EDITING_PROCESS_BOARD_ID);

        return board.getLists();
    }

    private Member getMember(String username, Token accessToken) {
        Trello trelloApi = new TrelloImpl(TrelloAuthenticationService.APPLICATION_KEY, accessToken.getToken());
        return trelloApi.getBasicMemberInformation(username);
    }

    public Member getUserInfo(Token accessToken) {
        return getMember("me", accessToken);
    }

    private Card buildCardFromFeedEntry(FeedEntry feedEntry) {
        Card cardToCreate = new Card();
        cardToCreate.setName(feedEntry.getTitle());
        cardToCreate.setDesc(feedEntry.getLink());
        return cardToCreate;
    }
}
