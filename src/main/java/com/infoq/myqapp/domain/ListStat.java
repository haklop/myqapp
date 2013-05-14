package com.infoq.myqapp.domain;

import java.util.List;

public class ListStat {

    private String listName;
    private List<UserStat> userStats;

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
}
