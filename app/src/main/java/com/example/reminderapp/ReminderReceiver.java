package com.example.reminderapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Mendapatkan data dari intent
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");

        // Validasi data
        if (title == null || title.isEmpty()) {
            title = "Reminder";
        }
        if (note == null || note.isEmpty()) {
            note = "You have a reminder!";
        }

        // Log untuk debugging
        Log.d("ReminderReceiver", "Received reminder: title=" + title + ", note=" + note);

        // Mendapatkan NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Membuat NotificationChannel untuk Android Oreo (API 26) ke atas
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for reminder notifications");

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Membangun notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm) // Pastikan ikon tersedia
                .setContentTitle(title)
                .setContentText(note)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Menampilkan notifikasi dengan ID unik
        if (notificationManager != null) {
            int notificationId = intent.getIntExtra("id", (int) System.currentTimeMillis());
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
