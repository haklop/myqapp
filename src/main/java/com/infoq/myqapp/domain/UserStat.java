package com.infoq.myqapp.domain;

import org.springframework.data.annotation.Id;

@Deprecated
public class UserStat {

    @Id
    private String id;

    private String listName;

    private int originalNews;

    private String memberId;
    private String memberFullName;
    private int originalArticles;
    private int translatedNews;
    private int translatedArticles;
    private int validatedNews;
    private int validatedArticles;
    private int mentoredNews;
    private int mentoredArticles;

    public UserStat(String memberId, String memberFullName, String listName) {
        this.memberId = memberId;
        this.memberFullName = memberFullName;
        this.id = memberId + listName;
        this.listName = listName;
    }

    public void incrementOriginalNews() {
        originalNews++;
    }

    public void incrementOriginalArticles() {
        originalArticles++;
    }

    public void incrementTranslatedNews() {
        translatedNews++;
    }

    public void incrementTranslatedArticles() {
        translatedArticles++;
    }

    public void incrementValidatedNews() {
        validatedNews++;
    }

    public void incrementValidatedArticles() {
        validatedArticles++;
    }

    public void incrementMentoredNews() {
        mentoredNews++;
    }

    public void incrementMentoredArticles() {
        mentoredArticles++;
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

    public int getValidatedNews() {
        return validatedNews;
    }

    public void setValidatedNews(int validatedNews) {
        this.validatedNews = validatedNews;
    }

    public int getValidatedArticles() {
        return validatedArticles;
    }

    public void setValidatedArticles(int validatedArticles) {
        this.validatedArticles = validatedArticles;
    }

    public int getMentoredNews() {
        return mentoredNews;
    }

    public void setMentoredNews(int mentoredNews) {
        this.mentoredNews = mentoredNews;
    }

    public int getMentoredArticles() {
        return mentoredArticles;
    }

    public void setMentoredArticles(int mentoredArticles) {
        this.mentoredArticles = mentoredArticles;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberFullName() {
        return memberFullName;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }
}
