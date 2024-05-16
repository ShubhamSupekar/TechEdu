package com.example.treaders;

import org.springframework.stereotype.Repository;

@Repository
public class InputForm {
    private String inputString;

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }
}