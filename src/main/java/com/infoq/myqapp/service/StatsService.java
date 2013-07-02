package com.infoq.myqapp.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.infoq.myqapp.domain.ListStat;
import com.infoq.myqapp.domain.TrelloActivity;
import com.infoq.myqapp.domain.TrelloHeartbeat;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.domain.UserStat;
import com.infoq.myqapp.repository.UserActivityRepository;
import com.infoq.myqapp.repository.UserStatRepository;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;

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
	private UserActivityRepository userActivityRepository;

    @Resource
    private TrelloService trelloService;

    @Resource
    private MongoTemplate mongoTemplate;

    public List<TrelloActivity> getUsersStats() {
        List<UserStat> userStats = mongoTemplate.find(query(where("listName").in(A_VALIDER, EN_COURS_DE_VALIDATION, VALIDE, PUBLIE, EN_COURS_PUBLICATION, PROBLEME_FORMAT)), UserStat.class);

        return aggregateStats(userStats);
    }

    private List<TrelloActivity> aggregateStats(List<UserStat> userStats) {
        Map<String, TrelloActivity> aggregatedStats = new HashMap<>();
        List<TrelloActivity> allActivities = mongoTemplate.findAll(TrelloActivity.class);
        for (TrelloActivity activity : allActivities) {
			aggregatedStats.put(activity.getMemberId(), activity);
		}
        
        for (UserStat stat : userStats) {
            String userId = stat.getMemberId();
            
            TrelloActivity currentStat = aggregatedStats.get(userId);
            if (currentStat != null && !(userId.equals(NONE) || userId.equals("5024fa0753f944277fba9907"))) {
            	currentStat.setMentoredArticles(currentStat.getMentoredArticles() + stat.getMentoredArticles());
            	currentStat.setMentoredNews(currentStat.getMentoredNews() + stat.getMentoredNews());
            	currentStat.setOriginalArticles(currentStat.getOriginalArticles() + stat.getOriginalArticles());
            	currentStat.setOriginalNews(currentStat.getOriginalNews() + stat.getOriginalNews());
            	currentStat.setTranslatedArticles(currentStat.getTranslatedArticles() + stat.getTranslatedArticles());
            	currentStat.setTranslatedNews(currentStat.getTranslatedNews() + stat.getTranslatedNews());
            	currentStat.setValidatedArticles(currentStat.getValidatedArticles() + stat.getValidatedArticles());
            	currentStat.setValidatedNews(currentStat.getValidatedNews() + stat.getValidatedNews());
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
        for(UserStat stat : stats){
            count += stat.getTranslatedNews();
        }
        return count;
    }

    private int countTranslatedArticles(List<UserStat> stats) {
        int count = 0;
        for(UserStat stat : stats){
            count += stat.getTranslatedArticles();
        }
        return count;
    }

    private int countOriginalNews(List<UserStat> stats) {
        int count = 0;
        for(UserStat stat : stats){
            count += stat.getOriginalNews();
        }
        return count;
    }

    private int countOriginalArticles(List<UserStat> stats) {
        int count = 0;
        for(UserStat stat : stats){
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
        UserProfile adminUser = mongoTemplate.findOne(query(where("email").is("vey.julien@gmail.com")), UserProfile.class);

        Map<String, Member> memberMap = mapMemberList(trelloService.getMembers(adminUser.getTokenTrello()));
        List<TList> lists = trelloService.getLists(adminUser.getTokenTrello());

        userStatRepository.deleteAll();
		userActivityRepository.deleteAll();

        for (TList list : lists) {
            Map<String, UserStat> userStatMap = new HashMap<>();
            for (Card card : list.getCards()) {
                String idAuthor = card.getIdMembers().size() > 0 ? card.getIdMembers().get(0) : NONE;
                String idValidator = card.getIdMembers().size() > 1 ? card.getIdMembers().get(1) : NONE;

                if (!userStatMap.containsKey(idAuthor)) {
                    Member member = memberMap.get(idAuthor);
                    if(member == null){
                        member = new Member();
                    }
                    userStatMap.put(idAuthor, new UserStat(member.getId(), member.getFullName(), list.getName()));
                }
                if (!userStatMap.containsKey(idValidator)) {
                    Member member = memberMap.get(idValidator);
                    if(member == null){
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
		// get the heartbeat for each members
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

		userActivityRepository.save(activities);
    }

	public static boolean hasLabel(Card card, String label) {
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
