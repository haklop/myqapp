package com.infoq.myqapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote {

    @Id
    @Email
    private String newEditorMail;

    private boolean isOpen;

    private String newEditorName;

    private Set<String> votersForMails;

    private Set<String> votersAgainstMails;


    public Set<String> getVotersAgainstMails() {
        return votersAgainstMails;
    }

    public void setVotersAgainstMails(Set<String> votersAgainstMails) {
        this.votersAgainstMails = votersAgainstMails;
    }

    public Set<String> getVotersForMails() {
        return votersForMails;
    }

    public void setVotersForMails(Set<String> votersForMails) {
        this.votersForMails = votersForMails;
    }

    public String getNewEditorName() {
        return newEditorName;
    }

    public void setNewEditorName(String newEditorName) {
        this.newEditorName = newEditorName;
    }

    public String getNewEditorMail() {
        return newEditorMail;
    }

    public void setNewEditorMail(String newEditorMail) {
        this.newEditorMail = newEditorMail;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
