package com.example.reminderapp;

import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class WeatherActivity extends AppCompatActivity {

    private MaterialCardView weatherCard;
    private TextView weatherLocation, weatherTemp, weatherCondition, weatherHighTemp, weatherLowTemp;
    private ImageView weatherIcon;

    private String weatherConditionText = "82e2b85c17384d93ffe40a51b83c32fb"; // Kondisi cuaca (misalnya dari API cuaca)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        weatherCard = findViewById(R.id.weatherCard);
        weatherLocation = findViewById(R.id.weatherLocation);
        weatherTemp = findViewById(R.id.weatherTemp);
        weatherCondition = findViewById(R.id.weatherCondition);
        weatherHighTemp = findViewById(R.id.weatherHighTemp);
        weatherLowTemp = findViewById(R.id.weatherLowTemp);
        weatherIcon = findViewById(R.id.weatherIcon);

        // Update latar belakang dan cuaca
        updateWeatherBackground(weatherConditionText);
    }

    private void updateWeatherBackground(String weatherCondition) {
        // Mulai animasi fade-out sebelum mengganti latar belakang
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(weatherCard, "alpha", 1f, 0f);
        fadeOut.setDuration(300);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Ganti latar belakang berdasarkan kondisi cuaca
                if ("Clear".equalsIgnoreCase(weatherCondition)) {
                    weatherCard.setBackgroundResource(R.drawable.bg_cerah);
                } else if ("Clouds".equalsIgnoreCase(weatherCondition)) {
                    weatherCard.setBackgroundResource(R.drawable.bg_berawan);
                } else if ("Rain".equalsIgnoreCase(weatherCondition)) {
                    weatherCard.setBackgroundResource(R.drawable.bg_hujan);
                }

                // Efek fade-in setelah latar belakang berubah
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(weatherCard, "alpha", 0f, 1f);
                fadeIn.setDuration(300);
                fadeIn.start();
            }
        });

        fadeOut.start();
    }

}
