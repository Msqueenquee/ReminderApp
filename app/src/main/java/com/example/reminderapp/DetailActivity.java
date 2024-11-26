package com.example.reminderapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private Switch dateSwitch, timeSwitch;
    private CalendarView calendarView;
    private TimePicker timePicker;
    private Uri selectedPhotoUri;
    private Button doneButton;
    private String selectedDate, selectedTime;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        dateSwitch = findViewById(R.id.date_switch);
        calendarView = findViewById(R.id.calendar_view);
        timeSwitch = findViewById(R.id.time_switch);
        timePicker = findViewById(R.id.time_picker);
        doneButton = findViewById(R.id.done_button);

        // Firebase reference
        database = FirebaseDatabase.getInstance().getReference("reminders");

        // Open gallery to select photo
        findViewById(R.id.add_photo_button).setOnClickListener(v -> openGallery());

        // Set listener for date switch
        dateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                calendarView.setVisibility(View.VISIBLE);
            } else {
                calendarView.setVisibility(View.GONE);
            }
        });

        // Set listener for time switch
        timeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                timePicker.setVisibility(View.VISIBLE);
            } else {
                timePicker.setVisibility(View.GONE);
            }
        });

        // Get selected date from calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        });

        // Get selected time from time picker
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
        });

        // Done button to save the reminder
        doneButton.setOnClickListener(v -> saveReminder());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedPhotoUri = data.getData();
            Toast.makeText(this, "Photo added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReminder() {
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique ID for the reminder
        String reminderId = database.push().getKey();

        // Prepare reminder data
        HashMap<String, Object> reminderData = new HashMap<>();
        reminderData.put("id", reminderId);
        reminderData.put("date", selectedDate);
        reminderData.put("time", selectedTime);
        reminderData.put("photoUri", selectedPhotoUri != null ? selectedPhotoUri.toString() : null);

        // Save to Firebase
        database.child(reminderId).setValue(reminderData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Reminder saved successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the previous activity
            } else {
                Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
