package com.example.reminderapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Reminder {
    private String id;
    private String title;
    private String details;
    private String notes;
    private String imageBase64;
    private Bitmap image;
    private boolean completed;

    // Default Constructor
    public Reminder() {
        // Firebase membutuhkan konstruktor default tanpa argumen
    }

    public Reminder(String id, String title, String notes, String imageUrl, String imageBase64, String details, boolean completed) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.imageBase64 = imageBase64;
        this.details = details;
        this.completed = completed;

        if (imageBase64 != null) {
            try {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                this.image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Bitmap getImage() { return image; }
    public void setImage(Bitmap image) { this.image = image; }
    public String getImageBase64() {
        if (image != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return null;
    }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
