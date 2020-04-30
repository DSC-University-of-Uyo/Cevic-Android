package com.android.cevic.model;

public class QuizBuilder {
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String time;
    private String title;

    public QuizBuilder setQuestion(String question) {
        this.question = question;
        return this;
    }

    public QuizBuilder setOptionA(String optionA) {
        this.optionA = optionA;
        return this;
    }

    public QuizBuilder setOptionB(String optionB) {
        this.optionB = optionB;
        return this;
    }

    public QuizBuilder setOptionC(String optionC) {
        this.optionC = optionC;
        return this;
    }

    public QuizBuilder setOptionD(String optionD) {
        this.optionD = optionD;
        return this;
    }

    public QuizBuilder setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public QuizBuilder setTime(String time) {
        this.time = time;
        return this;
    }

    public QuizBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public Quiz createQuiz() {
        return new Quiz(question, optionA, optionB, optionC, optionD, answer, time, title);
    }
}