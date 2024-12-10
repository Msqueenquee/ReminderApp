package com.example.reminderapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewReminderActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private EditText titleInput, notesInput;
    private CalendarView calendarView;
    private TimePicker timePicker;
    private ImageView photoPreview;
    private Switch dateSwitch, timeSwitch;

    private String selectedDate;
    private Uri photoUri;

    private SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        // Inisialisasi views
        titleInput = findViewById(R.id.titleInput);
        notesInput = findViewById(R.id.notesInput);
        calendarView = findViewById(R.id.calendarView);
        timePicker = findViewById(R.id.timePicker);
        photoPreview = findViewById(R.id.photoPreview);
        dateSwitch = findViewById(R.id.dateSwitch);
        timeSwitch = findViewById(R.id.timeSwitch);

        // Detail Sections dan Spacer
        LinearLayout detailSection = findViewById(R.id.detailSection);
        LinearLayout detailSection2 = findViewById(R.id.detailSection2);
        View detailSpacer = findViewById(R.id.detailSpacer);
        View detailSpacer2 = findViewById(R.id.detailSpacer2);
        Switch detailSwitch = findViewById(R.id.detailSwitch);

        TextView cancelButton = findViewById(R.id.cancelButton);
        TextView addButton = findViewById(R.id.addButton);
        TextView addPhotoButton = findViewById(R.id.addPhotoButton);
        TextView addGalleryButton = findViewById(R.id.addGalleryButton);

        sqLiteHelper = new SQLiteHelper(this);

        // Memeriksa izin untuk menjadwalkan alarm yang tepat pada API 31+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }


        // Logika Detail Switch
        detailSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            detailSection.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            detailSection2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            detailSpacer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            detailSpacer2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Tombol Cancel
        cancelButton.setOnClickListener(v -> finish());

        // Tombol Add Photo untuk membuka kamera
        addPhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(this, "No Camera App Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Tombol Add Gallery untuk memilih gambar dari galeri
        addGalleryButton.setOnClickListener(v -> {
            Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (pickPhotoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
            } else {
                Toast.makeText(this, "No Gallery App Found", Toast.LENGTH_SHORT).show();
            }
        });

        // Switch untuk mengatur CalendarView visibility
        dateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> calendarView.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        // Switch untuk mengatur TimePicker visibility
        timeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> timePicker.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        // Listener untuk CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
        });

        // Tombol Add Reminder
        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String note = notesInput.getText().toString();
            boolean isDetailEnabled = detailSwitch.isChecked();

            // Validasi input
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Jika detail diaktifkan, periksa apakah tanggal dan waktu telah dipilih
            String reminderDateTime = null;
            if (isDetailEnabled) {
                if (selectedDate == null) {
                    Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!timeSwitch.isChecked()) {
                    Toast.makeText(this, "Please enable and set a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gabungkan tanggal yang dipilih dengan waktu yang dipilih
                reminderDateTime = selectedDate + " " + String.format(Locale.getDefault(), "%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            }

            // Jika detail diaktifkan, masukkan tanggal dan waktu
            // Jika tidak, reminderDateTime tetap null
            Reminder reminder = new Reminder(
                    String.valueOf(System.currentTimeMillis()),
                    title,
                    reminderDateTime,  // null jika detail tidak diaktifkan
                    false,
                    note,
                    photoUri != null ? photoUri.toString() : null
            );

            sqLiteHelper.addReminder(reminder);

            // Jadwalkan notifikasi
            scheduleNotification(reminder);

            Toast.makeText(this, "Reminder added!", Toast.LENGTH_SHORT).show();

            // Kembali ke HomeActivity
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

    }

    private void scheduleNotification(Reminder reminder) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("note", reminder.getNote());

        // Membuat PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                reminder.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Mengambil sistem AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null && reminder.getTime() != null) {
            // Konversi waktu reminder ke millis
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                Date date = sdf.parse(reminder.getTime());
                if (date != null) {
                    if (date.getTime() > System.currentTimeMillis()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
                        Log.d("scheduleNotification", "Alarm scheduled for: " + date.getTime());
                    } else {
                        Toast.makeText(this, "Reminder time is in the past!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("scheduleNotification", "Error parsing date", e);
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photoUri = getImageUri(photo);
                Glide.with(this).load(photoUri).into(photoPreview);
                photoPreview.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                photoUri = data.getData();
                Glide.with(this).load(photoUri).into(photoPreview);
                photoPreview.setVisibility(View.VISIBLE);
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ReminderImage", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
        }
    }
}
