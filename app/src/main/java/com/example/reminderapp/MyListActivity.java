package com.example.reminderapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyListActivity extends AppCompatActivity {

    private RecyclerView reminderRecyclerView;
    private ReminderAdapter reminderAdapter;
    private SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        LinearLayout backButtonLayout = findViewById(R.id.backButtonLayout);
        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);

        backButtonLayout.setOnClickListener(v -> onBackPressed());

        sqLiteHelper = new SQLiteHelper(this);
        loadReminders();
    }

    private void loadReminders() {
        List<Reminder> reminderList = sqLiteHelper.getAllReminders();
        if (reminderList == null || reminderList.isEmpty()) {
            Log.e("MyListActivity", "No reminders found");
        }

        reminderAdapter = new ReminderAdapter(reminderList, sqLiteHelper);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderRecyclerView.setAdapter(reminderAdapter);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
