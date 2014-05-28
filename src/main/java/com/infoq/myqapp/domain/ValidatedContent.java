package com.infoq.myqapp.domain;

import java.util.Date;

public class ValidatedContent {

    private String name;
    private Date dateLastActivity;

    private String trelloUrl;
    private String githubUrl;
    private String infoqUrl;

    private String node;

    private boolean isArticle;
    private boolean isOriginal;
    private boolean isMentoring;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateLastActivity(Date dateLastActivity) {
        this.dateLastActivity = dateLastActivity;
    }

    public String getTrelloUrl() {
        return trelloUrl;
    }

    public void setTrelloUrl(String trelloUrl) {
        this.trelloUrl = trelloUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getInfoqUrl() {
        return infoqUrl;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setInfoqUrl(String infoqUrl) {
        this.infoqUrl = infoqUrl;
    }

    public boolean isArticle() {
        return isArticle;
    }

    public void setArticle(boolean article) {
        this.isArticle = article;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public boolean isMentoring() {
        return isMentoring;
    }

    public void setMentoring(boolean mentoring) {
        isMentoring = mentoring;
    }
}
