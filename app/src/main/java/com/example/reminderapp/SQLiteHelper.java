package com.example.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 4;  // Versi database diupdate menjadi 3

    private static final String TABLE_REMINDERS = "reminders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_NOTE = "notes";
    private static final String COLUMN_PHOTO_URI = "photoUri"; // Menambahkan kolom foto URI

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel dengan kolom baru photoUri
        String createTable = "CREATE TABLE " + TABLE_REMINDERS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER DEFAULT 0, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_PHOTO_URI + " TEXT)";  // Kolom photoUri ditambahkan
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {  // Pastikan memeriksa apakah versi lama lebih kecil dari versi baru
            db.execSQL("ALTER TABLE " + TABLE_REMINDERS + " ADD COLUMN " + COLUMN_PHOTO_URI + " TEXT");
        }
    }


    // Menambahkan reminder baru ke dalam database
    public void addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, reminder.getId());
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_TIME, reminder.getTime());
        values.put(COLUMN_COMPLETED, reminder.isCompleted() ? 1 : 0);
        values.put(COLUMN_NOTE, reminder.getNote());
        values.put(COLUMN_PHOTO_URI, reminder.getPhotoUri());  // Menyimpan URI foto
        db.insert(TABLE_REMINDERS, null, values);
        db.close();
    }

    // Mengambil semua reminder dari database
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Mengambil reminder dengan waktu terurut
        Cursor cursor = db.query(TABLE_REMINDERS, null, COLUMN_TIME + " IS NOT NULL", null, null, null, COLUMN_TIME + " ASC");

        // Tambahkan pengingat dengan waktu ke list
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1;
                String note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE));
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));

                reminders.add(new Reminder(id, title, time, completed, note, photoUri));
            }
            cursor.close();
        }

        // Mengambil pengingat tanpa waktu dan tambahkan di bawah pengingat yang memiliki waktu
        Cursor cursorNoTime = db.query(TABLE_REMINDERS, null, COLUMN_TIME + " IS NULL", null, null, null, COLUMN_ID + " DESC");
        if (cursorNoTime != null) {
            while (cursorNoTime.moveToNext()) {
                String id = cursorNoTime.getString(cursorNoTime.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursorNoTime.getString(cursorNoTime.getColumnIndexOrThrow(COLUMN_TITLE));
                String time = cursorNoTime.getString(cursorNoTime.getColumnIndexOrThrow(COLUMN_TIME));
                boolean completed = cursorNoTime.getInt(cursorNoTime.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1;
                String note = cursorNoTime.getString(cursorNoTime.getColumnIndexOrThrow(COLUMN_NOTE));
                String photoUri = cursorNoTime.getString(cursorNoTime.getColumnIndexOrThrow(COLUMN_PHOTO_URI));

                reminders.add(new Reminder(id, title, time, completed, note, photoUri));
            }
            cursorNoTime.close();
        }

        db.close();
        return reminders;
    }



    // Menghapus reminder berdasarkan ID
    public void deleteReminder(String reminderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, COLUMN_ID + " = ?", new String[]{reminderId});
        db.close();
    }

    // Memperbarui status 'completed' dari reminder
    public void updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, reminder.isCompleted() ? 1 : 0);
        db.update(TABLE_REMINDERS, values, COLUMN_ID + " = ?", new String[]{reminder.getId()});
        db.close();
    }

    // Menghitung jumlah pengingat
    public int getReminderCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_REMINDERS;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // Mengambil jumlah baris
        }

        cursor.close();
        db.close();
        return count;
    }

}
