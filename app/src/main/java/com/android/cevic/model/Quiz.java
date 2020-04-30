package com.android.cevic.model;

import java.io.Serializable;

public class Quiz implements Serializable {

    private String question, optionA, optionB, optionC, optionD, answer, time, title;

    public Quiz(String question, String optionA, String optionB, String optionC, String optionD, String answer, String time, String title) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        this.time = time;
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
