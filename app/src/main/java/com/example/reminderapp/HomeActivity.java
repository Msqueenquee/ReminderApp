package com.example.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    private TextView weatherLocation, weatherTemp, weatherCondition, weatherHighTemp, weatherLowTemp, reminderCount;
    private ImageView weatherIcon;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi view
        weatherLocation = findViewById(R.id.weatherLocation);
        weatherTemp = findViewById(R.id.weatherTemp);
        weatherCondition = findViewById(R.id.weatherCondition);
        weatherHighTemp = findViewById(R.id.weatherHighTemp);
        weatherLowTemp = findViewById(R.id.weatherLowTemp);
        weatherIcon = findViewById(R.id.weatherIcon);
        reminderCount = findViewById(R.id.reminderCount);

        // Inisialisasi Firebase
        firebaseHelper = new FirebaseHelper();

        // Load cuaca
        loadWeather();

        // Load jumlah reminder
        loadReminderCount();

        // Tombol + Add Reminder
        findViewById(R.id.newReminderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke NewReminderActivity
                Intent intent = new Intent(HomeActivity.this, NewReminderActivity.class);
                startActivity(intent);
            }
        });

        // Tombol My List
        findViewById(R.id.myListCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke MyListActivity
                Intent intent = new Intent(HomeActivity.this, MyListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherAPI = retrofit.create(WeatherApi.class);

        // Gantilah "Cikarang" dengan lokasi yang sesuai atau mendapatkan dari lokasi pengguna
        Call<WeatherResponse> call = weatherAPI.getWeather("Cikarang", "82e2b85c17384d93ffe40a51b83c32fb", "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    // Update UI dengan data cuaca
                    weatherLocation.setText(weather.name);
                    weatherTemp.setText(String.format("%.0f°C", weather.main.temp));
                    weatherCondition.setText(weather.weather[0].description);
                    weatherHighTemp.setText(String.format("H: %.0f°C", weather.main.temp_max));
                    weatherLowTemp.setText(String.format("L: %.0f°C", weather.main.temp_min));

                    // Muat ikon cuaca secara dinamis menggunakan Picasso
                    String iconUrl = "https://openweathermap.org/img/wn/" + weather.weather[0].icon + "@2x.png";
                    Picasso.get().load(iconUrl).into(weatherIcon);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Failed to load weather", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReminderCount() {
        firebaseHelper.getRemindersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                reminderCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to load reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
