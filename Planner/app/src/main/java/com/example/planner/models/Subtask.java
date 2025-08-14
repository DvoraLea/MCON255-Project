package com.example.planner.models;

import java.io.Serializable;
import java.util.UUID;

public class Subtask implements Serializable {
    private String id;
    private String title;
    private boolean done;

    public Subtask(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.done = false;
    }

    // constructor
    public Subtask(String id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    // getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isDone() { return done; }

    public void setTitle(String title) { this.title = title; }
    public void setDone(boolean done) { this.done = done; }
}
