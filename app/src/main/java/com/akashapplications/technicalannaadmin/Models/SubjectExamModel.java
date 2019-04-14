package com.akashapplications.technicalannaadmin.Models;

import java.util.ArrayList;

public class SubjectExamModel {
    String name, subject, id;
    int fullMarks, fees, timeAlloted;
    ArrayList<QuizModel> quizList;
    ArrayList<String> registeredUser;

    public ArrayList<String> getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(ArrayList<String> registeredUser) {
        this.registeredUser = registeredUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFullMarks() {
        return fullMarks;
    }

    public void setFullMarks(int fullMarks) {
        this.fullMarks = fullMarks;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public int getTimeAlloted() {
        return timeAlloted;
    }

    public void setTimeAlloted(int timeAlloted) {
        this.timeAlloted = timeAlloted;
    }

    public ArrayList<QuizModel> getQuizList() {
        return quizList;
    }

    public void setQuizList(ArrayList<QuizModel> quizList) {
        this.quizList = quizList;
    }
}
