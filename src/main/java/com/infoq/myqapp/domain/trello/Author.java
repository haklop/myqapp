package com.infoq.myqapp.domain.trello;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Author {

    @Id
    private String trelloId;

    private String username;
    private String fullName;
    private String avatarUrl;

    public int originalNews = 0;
    public int translatedNews = 0;
    public int originalArticles = 0;
    public int translatedArticles = 0;
    public int mentoredNews = 0;
    public int mentoredArticles = 0;
    public int validatedNews = 0;
    public int validatedArticles = 0;

    private Date lastActivity;

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTrelloId() {
        return trelloId;
    }

    public void setTrelloId(String trelloId) {
        this.trelloId = trelloId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
