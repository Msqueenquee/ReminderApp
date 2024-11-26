package com.example.reminderapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private final DatabaseReference remindersRef;

    public FirebaseHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        remindersRef = database.getReference("reminders");
    }

    public DatabaseReference getRemindersRef() {
        return remindersRef;
    }

    public void addReminder(Reminder reminder, Runnable onSuccess, Runnable onFailure) {
        String id = remindersRef.push().getKey();
        if (id != null) {
            reminder.setId(id);
            remindersRef.child(id).setValue(reminder)
                    .addOnSuccessListener(aVoid -> onSuccess.run())
                    .addOnFailureListener(e -> onFailure.run());
        }
    }
}
