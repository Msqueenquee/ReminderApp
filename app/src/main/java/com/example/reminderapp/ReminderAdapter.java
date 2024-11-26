package com.example.reminderapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private OnReminderCheckedListener onReminderCheckedListener;

    public ReminderAdapter(List<Reminder> reminderList, OnReminderCheckedListener listener) {
        this.reminderList = reminderList;
        this.onReminderCheckedListener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);

        holder.titleTextView.setText(reminder.getTitle());
        holder.notesTextView.setText(reminder.getNotes());

        Bitmap bitmap = decodeBase64ToBitmap(reminder.getImageBase64());
        if (bitmap != null) {
            holder.reminderImageView.setImageBitmap(bitmap);
        } else {
            holder.reminderImageView.setImageResource(R.drawable.default_image);
        }

        holder.reminderCheckBox.setChecked(reminder.isCompleted());
        holder.reminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onReminderCheckedListener != null) {
                onReminderCheckedListener.onChecked(reminder, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, notesTextView;
        ImageView reminderImageView;
        CheckBox reminderCheckBox;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.reminder_title);
            notesTextView = itemView.findViewById(R.id.reminder_detail);
            reminderImageView = itemView.findViewById(R.id.reminder_photo);
            reminderCheckBox = itemView.findViewById(R.id.reminder_checkbox);
        }
    }

    public interface OnReminderCheckedListener {
        void onChecked(Reminder reminder, boolean isChecked);
    }
}
