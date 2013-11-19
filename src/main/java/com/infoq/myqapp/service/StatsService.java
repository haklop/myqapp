package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.domain.trello.Author;
import com.infoq.myqapp.domain.trello.BoardList;
import com.infoq.myqapp.repository.AuthorRepository;
import com.infoq.myqapp.repository.BoardListRepository;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class StatsService {
    private static final Logger logger = LoggerFactory.getLogger(StatsService.class);

    // FIXME : use enum TrelloLabel
    public static final String ARTICLES = "Articles";
    public static final String MENTORAT = "Mentorat";
    public static final String ORIGINAL = "Original";
    public static final String TRADUCTION = "Traduction";
    public static final String NEWS = "News";

    public static final String NONE = "None";

    @Resource
    private BoardListRepository boardListRepository;

    @Resource
    private AuthorRepository authorRepository;

    @Resource
    private TrelloService trelloService;

    @Resource
    private MongoTemplate mongoTemplate;

    public List<Author> getUsersStats(boolean withActivity) {
        List<Author> authors = mongoTemplate.findAll(Author.class);

        // FIXME why filter activities ?
        if (!withActivity) {
            for (Author author : authors) {
                author.setLastActivity(null);
            }
        }

        return Collections.unmodifiableList(authors);
    }

    public List<BoardList> getListsStats() {
        return Collections.unmodifiableList(mongoTemplate.findAll(BoardList.class));
    }

    public void calculateStats() {
        logger.debug("Calculate stats");
        UserProfile adminUser = mongoTemplate.findOne(query(where("authorities").in("ROLE_ADMIN")),
                UserProfile.class);

        List<Member> members = trelloService.getMembers(adminUser.getTokenTrello());
        Map<String, Author> authorMap = mapMemberList(members);
        List<TList> lists = trelloService.getLists(adminUser.getTokenTrello());

        boardListRepository.deleteAll();

        for (TList list : lists) {
            BoardList boardList = getBoardList(list);
            Map<String, BoardList.Stats> userStatMap = new HashMap<>();

            for (Card card : list.getCards()) {
                String idAuthor = card.getIdMembers().size() > 0 ? card.getIdMembers().get(0) : NONE;
                String idValidator = card.getIdMembers().size() > 1 ? card.getIdMembers().get(1) : NONE;

                populateUserStatMap(idAuthor, userStatMap, authorMap);
                populateUserStatMap(idValidator, userStatMap, authorMap);

                BoardList.Stats author = userStatMap.get(idAuthor);
                BoardList.Stats validator = userStatMap.get(idValidator);

                Author globalAuthor = idAuthor.equals(NONE) ? new Author() : authorMap.get(idAuthor);
                Author globalValidator = idValidator.equals(NONE) ? new Author() : authorMap.get(idValidator);

                if (hasLabel(card, ARTICLES)) {
                    if (hasLabel(card, MENTORAT)) {
                        author.mentored++;
                        globalAuthor.mentoredArticles++;
                        boardList.mentorArticles++;
                    } else {
                        validator.validated++;
                        globalValidator.validatedArticles++;
                        author.articles++;
                    }

                    if (hasLabel(card, ORIGINAL)) {
                        boardList.originalArticles++;
                        globalAuthor.originalArticles++;
                    } else {
                        boardList.translatedArticles++;
                        globalAuthor.translatedArticles++;
                    }
                } else if (hasLabel(card, NEWS)) {
                    if (hasLabel(card, MENTORAT)) {
                        author.mentored++;
                        globalAuthor.mentoredNews++;
                        boardList.mentorNews++;
                    } else {
                        validator.validated++;
                        globalValidator.validatedNews++;
                        author.news++;
                    }

                    if (hasLabel(card, ORIGINAL)) {
                        boardList.originalNews++;
                        globalAuthor.originalNews++;
                    } else {
                        boardList.translatedNews++;
                        globalAuthor.translatedNews++;
                    }
                }
            }

            boardList.setStatsByAuthor(userStatMap.values());
            boardListRepository.save(boardList);
        }

        authorRepository.save(authorMap.values());
        // TODO sync author and list

        // get the heartbeat for each members
        /*
        logger.debug("Get the heartbeat for each members");
        List<TrelloActivity> activities = new ArrayList<>(memberMap.size());
        for (Entry<String, Member> entry : memberMap.entrySet()) {
            String id = entry.getKey();
            Member member = entry.getValue();
            if (NONE.equals(member.getId()) || AL_AMINE_USER_ID.equals(id))
                continue;
            List<TrelloHeartbeat> hb = trelloService.getMemberHeartbeat(member,
                    adminUser.getTokenTrello());
            activities.add(new TrelloActivity(id, member.getFullName(), "Done", hb));
        }

        userActivityRepository.save(activities);*/
    }

    public static boolean hasLabel(Card card, String label) {
        for (Label l : card.getLabels()) {
            if (l.getName().equals(label)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Author> mapMemberList(List<Member> members) {
        Map<String, Author> memberMap = new HashMap<>();
        for (Member member : members) {
            memberMap.put(member.getId(), getAuthor(member));
        }
        Author noneMember = new Author();
        noneMember.setUsername(NONE);
        memberMap.put(NONE, noneMember);
        return memberMap;
    }

    private Author getAuthor(Member member) {
        Author author = authorRepository.findOne(member.getId());
        if (author == null) {
            author = new Author();
            author.setUsername(member.getUsername());
            author.setFullName(member.getFullName());
            author.setTrelloId(member.getId());

            if (member.getAvatarHash() != null && !member.getAvatarHash().isEmpty()) {
                author.setAvatarUrl("https://trello-avatars.s3.amazonaws.com/" + member.getAvatarHash() + "/170.png");
            } else if (member.getGravatarHash() != null && !member.getGravatarHash().isEmpty()) {
                author.setAvatarUrl("http://www.gravatar.com/avatar/" + member.getGravatarHash() + "?s=170");
            }
        }

        return author;
    }

    private BoardList getBoardList(TList list) {
        BoardList boardList = boardListRepository.findOne(list.getId());
        if (boardList == null) {
            boardList = new BoardList();
            boardList.setId(list.getId());
            boardList.setName(list.getName());
        }

        return boardList;
    }

    private void populateUserStatMap(String idAuthor, Map<String, BoardList.Stats> userStatMap, Map<String, Author> memberMap) {
        if (!userStatMap.containsKey(idAuthor)) {
            Author author = memberMap.get(idAuthor);
            if (author == null) {
                author = new Author();
            }
            userStatMap.put(idAuthor, new BoardList.Stats(author.getTrelloId(), author.getFullName()));
        }
    }
}
