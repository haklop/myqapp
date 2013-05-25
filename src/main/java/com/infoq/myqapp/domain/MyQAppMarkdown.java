package com.infoq.myqapp.domain;

public class MyQAppMarkdown {

    private String text;
    private String type;
    private String node;

    public MyQAppMarkdown() {

    }

    public MyQAppMarkdown(String text, String type, String node) {
        this.text = text;
        this.type = type;
        this.node = node;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public boolean isAnArticle() {
        return "articles".equals(type);
    }
}
