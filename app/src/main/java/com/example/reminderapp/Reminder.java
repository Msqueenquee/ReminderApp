package com.example.reminderapp;

public class Reminder {
    private String id;
    private String title;
    private String time;
    private boolean completed;
    private String note;
    private String photoUri;

    public Reminder(String id, String title, String time, boolean completed, String note, String photoUri) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.completed = completed;
        this.note = note;
        this.photoUri = photoUri;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
}
