package com.infoq.myqapp.domain;

public class GitHubMarkdown {

    private String text;
    private String mode;
    private String context;

    public GitHubMarkdown(String text, String mode, String context) {
        this.text = text;
        this.mode = mode;
        this.context = context;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
