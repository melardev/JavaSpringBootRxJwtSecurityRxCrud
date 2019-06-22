package com.melardev.spring.rxsecjwt.entities;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Todo extends TimeStampedDocument {

    private String title;

    private String description;

    private boolean completed;

    public Todo() {
    }

    public Todo(String title, String description) {
        this(title, description, false);
    }

    public Todo(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


}