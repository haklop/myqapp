package com.infoq.myqapp.domain;

public enum TrelloLabel {

    NEWS("green"),
    ARTICLE("yellow"),
    SPONSOR("red"),
    TRADUCTION("purple"),
    ORIGINAL("blue");

    private String labelColor;

    private TrelloLabel(String labelColor) {
        this.labelColor = labelColor;
    }

    public String getLabelColor() {
        return labelColor;
    }
}
