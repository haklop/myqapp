package com.infoq.myqapp.domain;

import java.util.List;

@Deprecated
public class ListStat {

    private String listName;
    private List<UserStat> userStats;
    private int originalNews;
    private int originalArticles;
    private int translatedNews;
    private int translatedArticles;

    public ListStat(String listName, List<UserStat> userStats) {
        this.listName = listName;
        this.userStats = userStats;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<UserStat> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStat> userStats) {
        this.userStats = userStats;
    }

    public int getOriginalNews() {
        return originalNews;
    }

    public void setOriginalNews(int originalNews) {
        this.originalNews = originalNews;
    }

    public int getOriginalArticles() {
        return originalArticles;
    }

    public void setOriginalArticles(int originalArticles) {
        this.originalArticles = originalArticles;
    }

    public int getTranslatedNews() {
        return translatedNews;
    }

    public void setTranslatedNews(int translatedNews) {
        this.translatedNews = translatedNews;
    }

    public int getTranslatedArticles() {
        return translatedArticles;
    }

    public void setTranslatedArticles(int translatedArticles) {
        this.translatedArticles = translatedArticles;
    }
}
