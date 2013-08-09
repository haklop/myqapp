package com.infoq.myqapp.service;

import static com.julienvey.trello.utils.ArgUtils.arg;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.TrelloHeartbeat;
import com.infoq.myqapp.domain.TrelloLabel;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Action.Data;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.CardWithActions;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;

@Service
public class TrelloService {

    private static final Logger logger = LoggerFactory.getLogger(TrelloService.class);

    @Value("${editingprocess.add.board.id}")
    private String trelloBoardForAddingCardsId;

    @Value("${editingprocess.stats.board.id}")
    private String trelloBoardForStatsId;

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

    public List<TList> getLists(Token accessToken) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        Board board = trelloApi.getBoard(trelloBoardForStatsId);

        return board.fetchLists(arg("cards", "open"));
    }

    private Member getUserInfo(String username, Token accessToken) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        return trelloApi.getBasicMemberInformation(username);
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

    private Card buildCardFromFeedEntry(FeedEntry feedEntry) {
        Card cardToCreate = new Card();
        cardToCreate.setName(feedEntry.getTitle());
        cardToCreate.setDesc(feedEntry.getLink());
        return cardToCreate;
    }

    public TList getList(Token accessToken, String listId) {
        Trello trelloApi = new TrelloImpl(trelloKey, accessToken.getToken());
        return trelloApi.getList(listId, arg("cards", "open"));
    }
}
