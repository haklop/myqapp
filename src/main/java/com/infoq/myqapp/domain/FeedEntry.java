package com.infoq.myqapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedEntry {

    @Id
    private String link;
    private String title;
    private String description;
    private String type;
    private Date publishedDate;
    private List<String> categories;
    private boolean addedInTrello;
    private String urlTrello;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAddedInTrello() {
        return addedInTrello;
    }

    public void setAddedInTrello(boolean addedInTrello) {
        this.addedInTrello = addedInTrello;
    }

    public String getUrlTrello() {
        return urlTrello;
    }

    public void setUrlTrello(String urlTrello) {
        this.urlTrello = urlTrello;
    }
}
