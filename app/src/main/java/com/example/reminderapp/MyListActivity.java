package com.example.reminderapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();

        reminderAdapter = new ReminderAdapter(reminderList, (reminder, isChecked) -> {
            reminder.setCompleted(isChecked);
            if (isChecked) {
                database.child("reminders").child(reminder.getId()).removeValue();
            } else {
                database.child("reminders").child(reminder.getId()).setValue(reminder);
            }
        });
        recyclerView.setAdapter(reminderAdapter);

        loadReminders();
    }

    private void loadReminders() {
        database.child("reminders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reminderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reminder reminder = snapshot.getValue(Reminder.class);
                    reminderList.add(reminder);
                }
                reminderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("MyListActivity", "Failed to load reminders", error.toException());
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}
