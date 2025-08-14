package com.example.planner.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Planner implements Serializable {

    private String id;
    private String title;
    private String description;
    private Date dueDate;
    private boolean isCompleted;

    private String notes;

    // NEW: subtasks
    private List<Subtask> subtasks;

    // Constructor
    public Planner(String id, String title, String description, Date dueDate, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.notes = "";
        this.subtasks = new ArrayList<>();
    }

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Subtask> getSubtasks() { return subtasks; }
    public void setSubtasks(List<Subtask> subtasks) { this.subtasks = subtasks; }


    public void addSubtask(Subtask s) {
        if (subtasks == null) subtasks = new ArrayList<>();
        subtasks.add(s);
    }

    public void removeSubtaskById(String subtaskId) {
        if (subtasks == null) return;
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i).getId().equals(subtaskId)) {
                subtasks.remove(i);
                break;
            }
        }
    }

    public void toggleSubtask(String subtaskId) {
        if (subtasks == null) return;
        for (Subtask s : subtasks) {
            if (s.getId().equals(subtaskId)) {
                s.setDone(!s.isDone());
                break;
            }
        }
    }
}
