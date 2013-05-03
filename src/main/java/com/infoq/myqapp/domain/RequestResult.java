package com.infoq.myqapp.domain;

public class RequestResult<T> {

    private String result;
    private T body;

    public RequestResult(String result) {
        this.result = result;
    }

    public RequestResult(T body) {
        this.body = body;
    }

    public RequestResult(String result, T body) {
        this.result = result;
        this.body = body;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
