package com.example.reminderapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TextView weatherLocation, weatherTemp, weatherCondition, weatherHighTemp, weatherLowTemp, reminderCount;
    private ImageView weatherIcon, logoutIcon;
    private SharedPreferences sharedPreferences;
    private SQLiteHelper dbHelper;
    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private EditText searchBar;
    private LinearLayout myListButton, newReminderButton;
    private MaterialCardView weatherCard;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi Views
        weatherLocation = findViewById(R.id.weatherLocation);
        weatherTemp = findViewById(R.id.weatherTemp);
        weatherCondition = findViewById(R.id.weatherCondition);
        weatherHighTemp = findViewById(R.id.weatherHighTemp);
        weatherLowTemp = findViewById(R.id.weatherLowTemp);
        weatherIcon = findViewById(R.id.weatherIcon);
        reminderCount = findViewById(R.id.reminderCount);
        logoutIcon = findViewById(R.id.logoutIcon);
        searchBar = findViewById(R.id.searchBar);
        myListButton = findViewById(R.id.myListButton);
        newReminderButton = findViewById(R.id.newReminderButton);
        weatherCard = findViewById(R.id.weatherCard);

        // Inisialisasi SharedPreferences, SQLiteHelper, dan FusedLocationProviderClient
        sharedPreferences = getSharedPreferences("ReminderAppPrefs", MODE_PRIVATE);
        dbHelper = new SQLiteHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup RecyclerView dan ReminderAdapter
        recyclerView = findViewById(R.id.searchResultsRecyclerView);
        reminderAdapter = new ReminderAdapter(dbHelper.getAllReminders(), dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reminderAdapter);

        // Memuat lokasi pengguna dan data cuaca
        fetchLocationAndWeather();

        // Memuat jumlah pengingat
        loadReminderCount();

        // Tombol untuk membuka daftar pengingat
        myListButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MyListActivity.class);
            startActivityForResult(intent, 1);
        });

        // Tombol untuk menambahkan pengingat baru
        newReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NewReminderActivity.class);
            startActivityForResult(intent, 2);
        });

        // Fungsi logout
        logoutIcon.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Mengelola pencarian pengingat
        recyclerView.setVisibility(View.GONE);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                reminderAdapter.filterList(charSequence.toString());
                recyclerView.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void fetchLocationAndWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                loadWeather(latitude, longitude);
            } else {
                Toast.makeText(HomeActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeather(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherAPI = retrofit.create(WeatherApi.class);

        Call<WeatherResponse> call = weatherAPI.getWeatherByCoords(latitude, longitude, "82e2b85c17384d93ffe40a51b83c32fb", "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    weatherLocation.setText(weather.name);
                    weatherTemp.setText(String.format("%.0f°C", weather.main.temp));
                    weatherCondition.setText(weather.weather[0].description);
                    weatherHighTemp.setText(String.format("H: %.0f°C", weather.main.temp_max));
                    weatherLowTemp.setText(String.format("L: %.0f°C", weather.main.temp_min));

                    String iconUrl = "https://openweathermap.org/img/wn/" + weather.weather[0].icon + "@2x.png";
                    Picasso.get().load(iconUrl).into(weatherIcon);

                    updateWeatherBackground(weather.weather[0].main);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Unable to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWeatherBackground(String weatherCondition) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(weatherCard, "alpha", 1f, 0f);
        fadeOut.setDuration(300);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if ("Clear".equalsIgnoreCase(weatherCondition)) {
                    weatherCard.setBackgroundResource(R.drawable.bg_cerah);
                } else if ("Clouds".equalsIgnoreCase(weatherCondition)) {
                    weatherCard.setBackgroundResource(R.drawable.bg_berawan);
                } else if ("Rain".equalsIgnoreCase(weatherCondition)) {
                    weatherCard.setBackgroundResource(R.drawable.bg_hujan);
                } else {
                    weatherCard.setBackgroundResource(R.drawable.bg_default);
                }

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(weatherCard, "alpha", 0f, 1f);
                fadeIn.setDuration(300);
                fadeIn.start();
            }
        });

        fadeOut.start();
    }

    private void loadReminderCount() {
        int count = dbHelper.getReminderCount();
        reminderCount.setText(String.valueOf(count));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            loadReminderCount();
            reminderAdapter = new ReminderAdapter(dbHelper.getAllReminders(), dbHelper);
            recyclerView.setAdapter(reminderAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndWeather();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}
