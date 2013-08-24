package com.infoq.myqapp.domain;

import org.springframework.data.annotation.Id;

public class TrelloMember {

    @Id
    private String trelloId;
    private String fullName;

    private String trelloAvatarUrl;
    private String gravatarHash;

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

    public String getTrelloAvatarUrl() {
        return trelloAvatarUrl;
    }

    public void setTrelloAvatarUrl(String trelloAvatarUrl) {
        this.trelloAvatarUrl = trelloAvatarUrl;
    }

    public String getGravatarHash() {
        return gravatarHash;
    }

    public void setGravatarHash(String gravatarHash) {
        this.gravatarHash = gravatarHash;
    }
}
