package com.infoq.myqapp.domain;

import java.util.List;

public class TrelloActivity extends UserStat {

    private List<TrelloHeartbeat> activity;

    public TrelloActivity(String memberId, String memberFullName, String listName,
                          List<TrelloHeartbeat> activity) {
        super(memberId, memberFullName, listName);
        this.activity = activity;
    }

    public List<TrelloHeartbeat> getActivity() {
        return activity;
    }
}
