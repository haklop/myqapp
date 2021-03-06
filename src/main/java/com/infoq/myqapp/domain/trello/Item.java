package com.infoq.myqapp.domain.trello;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Item {

    @Id
    private String id;

    private String title;
    private String infoqUrl;
    private String githubUrl;
    private String trelloUrl;

    private Date lastActivity;

    @DBRef
    @Indexed
    private Author author;

    @DBRef
    @Indexed
    private Author validator;

    @DBRef
    @Indexed
    private Author mentor;

    @DBRef
    @Indexed
    private BoardList list;

    private boolean isOriginal = false;
    private boolean isAnArticle = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoqUrl() {
        return infoqUrl;
    }

    public void setInfoqUrl(String infoqUrl) {
        this.infoqUrl = infoqUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getTrelloUrl() {
        return trelloUrl;
    }

    public void setTrelloUrl(String trelloUrl) {
        this.trelloUrl = trelloUrl;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        if (author == null || author.getTrelloId() == null) {
            return;
        }
        this.author = author;
    }

    public Author getValidator() {
        return validator;
    }

    public void setValidator(Author validator) {
        if (validator == null || validator.getTrelloId() == null) {
            return;
        }
        this.validator = validator;
    }

    public BoardList getList() {
        return list;
    }

    public void setList(BoardList list) {
        this.list = list;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void isOriginal(boolean original) {
        isOriginal = original;
    }

    public boolean isAnArticle() {
        return isAnArticle;
    }

    public void isAnArticle(boolean anArticle) {
        isAnArticle = anArticle;
    }

    public Author getMentor() {
        return mentor;
    }

    public void setMentor(Author mentor) {
        if (mentor == null || mentor.getTrelloId() == null) {
            return;
        }
        this.mentor = mentor;
    }
}
