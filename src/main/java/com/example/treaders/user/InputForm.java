package com.example.treaders.user;

import org.springframework.stereotype.Repository;

@Repository
public class InputForm {
    private String inputString;
    private String responseString;

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}