package com.infoq.myqapp.domain;

public class ValueObject {

    private String value;

    public ValueObject() {
    }

    public ValueObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
