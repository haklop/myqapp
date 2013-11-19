package com.infoq.myqapp.domain.trello;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;

@Document
public class BoardList {

    @Id
    private String id;

    private String name;

    public int originalNews = 0;
    public int translatedNews = 0;
    public int originalArticles = 0;
    public int translatedArticles = 0;
    public int mentorNews = 0;
    public int mentorArticles = 0;

    public Collection<Stats> statsByAuthor = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Stats> getStatsByAuthor() {
        return statsByAuthor;
    }

    public void setStatsByAuthor(Collection<Stats> statsByAuthor) {
        this.statsByAuthor = statsByAuthor;
    }

    public static class Stats {

        private String trelloId;
        private String fullName;

        public int news = 0;
        public int articles = 0;
        public int validated = 0;
        public int mentored = 0;

        public Stats(String trelloId, String fullName) {
            this.trelloId = trelloId;
            this.fullName = fullName;
        }

        public Stats() {
            // nothing
        }

        public String getTrelloId() {
            return trelloId;
        }

        public void setTrelloId(String trelloId) {
            this.trelloId = trelloId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
