package com.example.reminderapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private List<Reminder> reminderListFull; // List untuk pencarian
    private SQLiteHelper sqLiteHelper;

    public ReminderAdapter(List<Reminder> reminderList, SQLiteHelper sqLiteHelper) {
        this.reminderList = reminderList;
        this.reminderListFull = new ArrayList<>(reminderList); // Duplikasi daftar asli untuk pencarian
        this.sqLiteHelper = sqLiteHelper;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.bind(reminder, position);
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // Method untuk menambahkan logika sorting berdasarkan waktu
    public void sortReminders() {
        // Ambil waktu handphone sekarang
        long currentTime = System.currentTimeMillis();

        // Mengurutkan berdasarkan waktu terdekat dengan waktu handphone
        reminderList.sort((reminder1, reminder2) -> {
            if (reminder1.getTime() != null && reminder2.getTime() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    long time1 = sdf.parse(reminder1.getTime()).getTime();
                    long time2 = sdf.parse(reminder2.getTime()).getTime();
                    return Long.compare(Math.abs(time1 - currentTime), Math.abs(time2 - currentTime));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0; // Mengembalikan 0 jika waktu tidak ada
        });
        notifyDataSetChanged();
    }

    // Metode untuk memfilter daftar pengingat
    public void filterList(String query) {
        List<Reminder> filteredList = new ArrayList<>();

        // Jika query kosong, tampilkan daftar penuh
        if (query.isEmpty()) {
            filteredList.addAll(reminderListFull); // Mengembalikan ke data asli
        } else {
            // Jika ada query, filter pengingat berdasarkan judul atau catatan
            for (Reminder reminder : reminderListFull) {
                if (reminder.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        reminder.getNote().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(reminder);
                }
            }
        }

        // Perbarui daftar pengingat yang ditampilkan dan beri tahu adapter
        reminderList.clear();
        reminderList.addAll(filteredList);
        sortReminders(); // Menyortir data setelah filter
        notifyDataSetChanged(); // Update RecyclerView
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, timeTextView, noteTextView;
        private final CheckBox reminderCheckBox;
        private final ImageView photoImageView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
            reminderCheckBox = itemView.findViewById(R.id.reminderCheckBox);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }

        public void bind(Reminder reminder, int position) {
            titleTextView.setText(reminder.getTitle());
            timeTextView.setText(reminder.getTime());
            noteTextView.setText(reminder.getNote());

            reminderCheckBox.setOnCheckedChangeListener(null);
            reminderCheckBox.setChecked(reminder.isCompleted());

            String photoUri = reminder.getPhotoUri();
            if (photoUri != null && !photoUri.isEmpty()) {
                photoImageView.setVisibility(View.VISIBLE);
                Glide.with(photoImageView.getContext())
                        .load(photoUri)
                        .into(photoImageView);
            } else {
                photoImageView.setVisibility(View.GONE);
            }

            reminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                    Log.e("ReminderAdapter", "Invalid position detected");
                    return;
                }

                Reminder currentReminder = reminderList.get(getAdapterPosition());

                if (isChecked) {
                    sqLiteHelper.deleteReminder(currentReminder.getId());
                    reminderList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}

