package com.infoq.myqapp.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.infoq.myqapp.domain.ListStat;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.domain.UserStat;
import com.infoq.myqapp.repository.UserStatRepository;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class StatsService {

    public static final String POOL_D_ARTICLES = "Pool d'Articles";
    public static final String EN_COURS_D_ECRITURE_TRADUCTION = "En cours d'écriture / traduction";
    public static final String A_VALIDER = "A Valider";
    public static final String EN_COURS_DE_VALIDATION = "En cours de validation";
    public static final String VALIDE = "Validé";
    public static final String EN_COURS_PUBLICATION = "En cours de publication";
    public static final String PROBLEME_FORMAT = "Problème format";
    public static final String PUBLIE = "Publié";

    public static final String ARTICLES = "Articles";
    public static final String MENTORAT = "Mentorat";
    public static final String ORIGINAL = "Original";
    public static final String TRADUCTION = "Traduction";
    public static final String NEWS = "News";

    public static final String NONE = "None";
    public static final String AL_AMINE_USER_ID = "5024fa0753f944277fba9907";

    @Resource
    private UserStatRepository userStatRepository;

    @Resource
    private TrelloService trelloService;

    @Resource
    private MongoTemplate mongoTemplate;

    public List<UserStat> getUsersStats() {
        List<UserStat> userStats = mongoTemplate.find(query(where("listName").in(A_VALIDER, EN_COURS_DE_VALIDATION, VALIDE, PUBLIE, EN_COURS_PUBLICATION, PROBLEME_FORMAT)), UserStat.class);

        return aggregateStats(userStats);
    }

    private List<UserStat> aggregateStats(List<UserStat> userStats) {
        Map<String, UserStat> aggregatedStats = new HashMap<>();
        for (UserStat stat : userStats) {
            String userId = stat.getMemberId();
            if (aggregatedStats.containsKey(userId)) {
                UserStat currentStat = aggregatedStats.get(userId);
                stat.setMentoredArticles(currentStat.getMentoredArticles() + stat.getMentoredArticles());
                stat.setMentoredNews(currentStat.getMentoredNews() + stat.getMentoredNews());
                stat.setOriginalArticles(currentStat.getOriginalArticles() + stat.getOriginalArticles());
                stat.setOriginalNews(currentStat.getOriginalNews() + stat.getOriginalNews());
                stat.setTranslatedArticles(currentStat.getTranslatedArticles() + stat.getTranslatedArticles());
                stat.setTranslatedNews(currentStat.getTranslatedNews() + stat.getTranslatedNews());
                stat.setValidatedArticles(currentStat.getValidatedArticles() + stat.getValidatedArticles());
                stat.setValidatedNews(currentStat.getValidatedNews() + stat.getValidatedNews());
            } else {
                stat.setListName("Done");
            }
            if (!(userId.equals(NONE) || userId.equals("5024fa0753f944277fba9907"))) {
                aggregatedStats.put(userId, stat);
            }
        }

        return new ArrayList<>(aggregatedStats.values());
    }

    public List<ListStat> getListsStats() {
        String[] lists = new String[]{POOL_D_ARTICLES, EN_COURS_D_ECRITURE_TRADUCTION, A_VALIDER, EN_COURS_DE_VALIDATION, VALIDE, EN_COURS_PUBLICATION, PROBLEME_FORMAT, PUBLIE};

        List<ListStat> listStats = new ArrayList<>();

        for (String list : lists) {
            List<UserStat> listFromMongo = fetchListFromMongo(list);
            ListStat listStat = new ListStat(list, aggregateUserStats(listFromMongo));
            listStat.setOriginalArticles(countOriginalArticles(listFromMongo));
            listStat.setOriginalNews(countOriginalNews(listFromMongo));
            listStat.setTranslatedArticles(countTranslatedArticles(listFromMongo));
            listStat.setTranslatedNews(countTranslatedNews(listFromMongo));
            listStats.add(listStat);
        }

        return listStats;
    }

    private int countTranslatedNews(List<UserStat> stats) {
        int count = 0;
        for (UserStat stat : stats) {
            count += stat.getTranslatedNews();
        }
        return count;
    }

    private int countTranslatedArticles(List<UserStat> stats) {
        int count = 0;
        for (UserStat stat : stats) {
            count += stat.getTranslatedArticles();
        }
        return count;
    }

    private int countOriginalNews(List<UserStat> stats) {
        int count = 0;
        for (UserStat stat : stats) {
            count += stat.getOriginalNews();
        }
        return count;
    }

    private int countOriginalArticles(List<UserStat> stats) {
        int count = 0;
        for (UserStat stat : stats) {
            count += stat.getOriginalArticles();
        }
        return count;
    }

    private List<UserStat> aggregateUserStats(List<UserStat> stats) {
        return new ArrayList<>(Collections2.filter(stats, new Predicate<UserStat>() {
            @Override
            public boolean apply(UserStat stat) {
                String userId = stat.getMemberId();
                return !(userId.equals(NONE) || userId.equals(AL_AMINE_USER_ID));
            }
        }));
    }

    private List<UserStat> fetchListFromMongo(String list) {
        return mongoTemplate.find(query(where("listName").is(list)), UserStat.class);
    }

    public void calculateStats() {
        UserProfile adminUser = mongoTemplate.findOne(query(where("authorities").in("ROLE_ADMIN")),
                UserProfile.class);

        Map<String, Member> memberMap = mapMemberList(trelloService.getMembers(adminUser.getTokenTrello()));
        List<TList> lists = trelloService.getLists(adminUser.getTokenTrello());

        userStatRepository.deleteAll();

        for (TList list : lists) {
            Map<String, UserStat> userStatMap = new HashMap<>();
            for (Card card : list.getCards()) {
                String idAuthor = card.getIdMembers().size() > 0 ? card.getIdMembers().get(0) : NONE;
                String idValidator = card.getIdMembers().size() > 1 ? card.getIdMembers().get(1) : NONE;

                if (!userStatMap.containsKey(idAuthor)) {
                    Member member = memberMap.get(idAuthor);
                    if (member == null) {
                        member = new Member();
                    }
                    userStatMap.put(idAuthor, new UserStat(member.getId(), member.getFullName(), list.getName()));
                }
                if (!userStatMap.containsKey(idValidator)) {
                    Member member = memberMap.get(idValidator);
                    if (member == null) {
                        member = new Member();
                    }
                    userStatMap.put(idValidator, new UserStat(member.getId(), member.getFullName(), list.getName()));
                }
                if (!userStatMap.containsKey(NONE)) {
                    userStatMap.put(NONE, new UserStat(NONE, null, list.getName()));
                }

                UserStat author = userStatMap.get(idAuthor);
                UserStat validator = userStatMap.get(idValidator);
                UserStat noneStats = userStatMap.get(NONE);
                if (hasLabel(card, ARTICLES)) {
                    if (hasLabel(card, MENTORAT)) {
                        author.incrementMentoredArticles();
                        if (hasLabel(card, ORIGINAL)) {
                            noneStats.incrementOriginalArticles();
                        } else if (hasLabel(card, TRADUCTION)) {
                            noneStats.incrementTranslatedArticles();
                        }
                    } else {
                        validator.incrementValidatedArticles();
                        if (hasLabel(card, ORIGINAL)) {
                            author.incrementOriginalArticles();
                        } else if (hasLabel(card, TRADUCTION)) {
                            author.incrementTranslatedArticles();
                        }
                    }
                } else if (hasLabel(card, NEWS)) {
                    if (hasLabel(card, MENTORAT)) {
                        author.incrementMentoredNews();
                        if (hasLabel(card, ORIGINAL)) {
                            noneStats.incrementOriginalNews();
                        } else if (hasLabel(card, TRADUCTION)) {
                            noneStats.incrementTranslatedNews();
                        }
                    } else {
                        validator.incrementValidatedNews();
                        if (hasLabel(card, ORIGINAL)) {
                            author.incrementOriginalNews();
                        } else if (hasLabel(card, TRADUCTION)) {
                            author.incrementTranslatedNews();
                        }
                    }
                }
            }

            userStatRepository.save(userStatMap.values());
        }
    }

    private boolean hasLabel(Card card, String label) {
        for (Label l : card.getLabels()) {
            if (l.getName().equals(label)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Member> mapMemberList(List<Member> members) {
        Map<String, Member> memberMap = new HashMap<>();
        for (Member member : members) {
            memberMap.put(member.getId(), member);
        }
        Member noneMember = new Member();
        noneMember.setId(NONE);
        memberMap.put(NONE, noneMember);
        return memberMap;
    }
}
