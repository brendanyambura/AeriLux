package com.example.mytask.models;

public class Task {
    private String title;
    private String priority;
    private boolean isCompleted;
    private long id;
    private long reminderTime; // Time in milliseconds

    public Task(long id, String title, String priority, boolean isCompleted) {
        this(id, title, priority, isCompleted, 0);
    }

    public Task(long id, String title, String priority, boolean isCompleted, long reminderTime) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.reminderTime = reminderTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }
}