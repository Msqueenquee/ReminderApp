package com.example.reminderapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    // Mendapatkan cuaca berdasarkan nama kota
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units // Celsius
    );

    // Mendapatkan cuaca berdasarkan koordinat (latitude dan longitude)
    @GET("weather")
    Call<WeatherResponse> getWeatherByCoords(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units // Celsius
    );
}
