package com.infoq.myqapp.domain;

/**
 * Created with IntelliJ IDEA.
 * User: slim
 * Date: 03/05/13
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
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
