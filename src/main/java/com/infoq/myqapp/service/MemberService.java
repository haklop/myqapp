package com.infoq.myqapp.service;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.impl.TrelloImpl;
import org.scribe.model.Token;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    public List<Member> getMembers(Token accessToken) {
        Trello trelloApi = new TrelloImpl(TrelloAuthenticationService.APPLICATION_KEY, accessToken.getToken());
        Board board = trelloApi.getBoard(TrelloService.EDITING_PROCESS_BOARD_ID);

        return board.getMembers();
    }
}
