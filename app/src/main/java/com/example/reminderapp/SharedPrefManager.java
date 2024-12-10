package com.example.reminderapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "ReminderAppPreferences";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";

    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void setLoggedIn(boolean isLoggedIn, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
