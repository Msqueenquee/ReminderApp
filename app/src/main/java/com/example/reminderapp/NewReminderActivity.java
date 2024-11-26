package com.example.reminderapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NewReminderActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText, notesEditText;
    private ImageView reminderImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        // Initialize UI elements
        titleEditText = findViewById(R.id.titleEditText);
        notesEditText = findViewById(R.id.notesEditText);
        reminderImageView = findViewById(R.id.reminderImageView);

        // Set onClick listener for reminder image
        reminderImageView.setOnClickListener(v -> openImageChooser());
    }

    // Handle Cancel button click
    public void onCancelClicked(View view) {
        finish(); // Finish the activity and go back to the previous one (HomeActivity)
    }

    // Handle Add button click
    public void onAddClicked(View view) {
        String title = titleEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        // Check if title and notes are entered
        if (title.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Please enter both title and notes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert image to Base64 if selected
        String imageBase64 = null;
        if (imageUri != null) {
            imageBase64 = encodeImageToBase64(imageUri);
        }

        // Create a new reminder object
        String reminderId = FirebaseDatabase.getInstance().getReference().push().getKey();
        Reminder reminder = new Reminder(reminderId, title, notes, null, imageBase64, "", false);

        // Save the reminder to Firebase
        FirebaseDatabase.getInstance().getReference("reminders")
                .child(reminderId)
                .setValue(reminder)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show());
    }

    // Handle Detail button click
    public void onDetailClicked(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }

    // Open image chooser to select an image
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of image chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            reminderImageView.setImageURI(imageUri); // Set the selected image to ImageView
        }
    }

    // Encode selected image to Base64 string
    private String encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
