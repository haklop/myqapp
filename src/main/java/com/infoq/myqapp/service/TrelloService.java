package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.FeedEntry;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.impl.TrelloImpl;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrelloService {

    private static final Logger LOG = LoggerFactory.getLogger(TrelloService.class);

    public static final String EDITING_PROCESS_BOARD_ID = "5182683ef43ba8a401000160";

    public void addCardToTrello(FeedEntry feedEntry, Token accessToken) {
        LOG.info("Trying to add card to Trello");
        Trello trelloApi = new TrelloImpl(TrelloAuthenticationService.APPLICATION_KEY, accessToken.getToken());
        Board board = trelloApi.getBoard(EDITING_PROCESS_BOARD_ID);

        LOG.info("URL Board : {}", board.getName());

    }

    public Member getMember(String username, Token accessToken) {
        Trello trelloApi = new TrelloImpl(TrelloAuthenticationService.APPLICATION_KEY, accessToken.getToken());
        return trelloApi.getBasicMemberInformation(username);
    }

}
