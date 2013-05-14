package com.infoq.myqapp.service;

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
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class StatsService {

    @Resource
    private UserStatRepository userStatRepository;

    @Resource
    private TrelloService trelloService;

    @Resource
    private MongoTemplate mongoTemplate;

    public List<UserStat> getUsersStats() {
        List<UserStat> userStats = mongoTemplate.find(query(where("listName").all("A Valider", "En cours de validation", "Validé", "Publié")), UserStat.class);

        return aggregateStats(userStats);
    }

    private List<UserStat> aggregateStats(List<UserStat> userStats) {
        Map<String, UserStat> aggregatedStats = new HashMap<>();
        for (UserStat stat : userStats) {
            String userId = stat.getMember().getId();
            if (aggregatedStats.containsKey(userId)) {
                UserStat currentStat = aggregatedStats.get(userId);
                stat.setMentoredArticles(currentStat.getMentoredArticles() + stat.getMentoredArticles());
                stat.setMentoredNews(currentStat.getMentoredNews() + stat.getMentoredNews());
                stat.setOriginalArticles(currentStat.getOriginalArticles() + stat.getOriginalArticles());
                stat.setOriginalNews(currentStat.getOriginalNews() + stat.getOriginalNews());
                stat.setTranslatedArticles(currentStat.getTranslatedArticles() + stat.getTranslatedArticles());
                stat.setTranslatedNews(currentStat.getTranslatedNews() + stat.getTranslatedNews());
            } else {
                stat.setListName("Done");
            }
            aggregatedStats.put(userId, stat);
        }

        return new ArrayList<>(aggregatedStats.values());
    }

    public List<ListStat> getListsStats() {
        ListStat poolList = new ListStat("Pool d'Articles", fetchListFromMongo("Pool d'Articles"));
        ListStat inProgressList = new ListStat("En cours d'écriture / traduction", fetchListFromMongo("En cours d'écriture / traduction"));
        ListStat toValidateList = new ListStat("A Valider", fetchListFromMongo("A Valider"));
        ListStat validationInProgessList = new ListStat("En cours de validation", fetchListFromMongo("En cours de validation"));
        ListStat validatedList = new ListStat("Validé", fetchListFromMongo("Validé"));
        ListStat publishedList = new ListStat("Publié", fetchListFromMongo("Publié"));

        return Arrays.asList(poolList, inProgressList, toValidateList, validationInProgessList, validatedList, publishedList);
    }

    private List<UserStat> fetchListFromMongo(String list) {
        return mongoTemplate.find(query(where("listName").is(list)), UserStat.class);
    }

    public void calculateStats() {
        UserProfile adminUser = mongoTemplate.findOne(query(where("email").is("vey.julien@gmail.com")), UserProfile.class);

        Map<String, Member> memberMap = mapMemberList(trelloService.getMembers(adminUser.getTokenTrello()));
        List<TList> lists = trelloService.getLists(adminUser.getTokenTrello());

        for (TList list : lists) {
            Map<String, UserStat> userStatMap = new HashMap<>();
            for (Card card : list.getCards()) {
                String idAuthor = card.getIdMembers().size() > 0 ? card.getIdMembers().get(0) : "None";
                String idValidator = card.getIdMembers().size() > 1 ? card.getIdMembers().get(1) : "None";

                if (!userStatMap.containsKey(idAuthor)) {
                    userStatMap.put(idAuthor, new UserStat(memberMap.get(idAuthor), list.getName()));
                }
                if (!userStatMap.containsKey(idValidator)) {
                    userStatMap.put(idValidator, new UserStat(memberMap.get(idValidator), list.getName()));
                }

                if (hasLabel(card, "Articles")) {
                    if (hasLabel(card, "Mentorat")) {
                        userStatMap.get(idAuthor).setMentoredArticles(userStatMap.get(idAuthor).getMentoredArticles() + 1);
                    } else {
                        userStatMap.get(idValidator).setValidatedArticles(userStatMap.get(idValidator).getValidatedArticles() + 1);
                        if (hasLabel(card, "Original")) {
                            userStatMap.get(idAuthor).setOriginalArticles(userStatMap.get(idAuthor).getOriginalArticles() + 1);
                        } else if (hasLabel(card, "Traduction")) {
                            userStatMap.get(idAuthor).setTranslatedArticles(userStatMap.get(idAuthor).getTranslatedArticles() + 1);
                        }
                    }
                } else if (hasLabel(card, "News")) {
                    if (hasLabel(card, "Mentorat")) {
                        userStatMap.get(idAuthor).setMentoredNews(userStatMap.get(idAuthor).getMentoredNews() + 1);
                    } else {
                        userStatMap.get(idValidator).setValidatedNews(userStatMap.get(idValidator).getValidatedNews() + 1);
                        if (hasLabel(card, "Original")) {
                            userStatMap.get(idAuthor).setOriginalNews(userStatMap.get(idAuthor).getOriginalNews() + 1);
                        } else if (hasLabel(card, "Traduction")) {
                            userStatMap.get(idAuthor).setTranslatedNews(userStatMap.get(idAuthor).getTranslatedNews() + 1);
                        }
                    }
                }
            }

            for (UserStat stat : userStatMap.values()) {
                if (!(stat.getMember().getId().equals("None") || stat.getMember().getId().equals("5024fa0753f944277fba9907"))) {
                    userStatRepository.save(stat);
                }
            }
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
        noneMember.setId("None");
        memberMap.put("None", noneMember);
        return memberMap;
    }
}
