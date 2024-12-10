package com.example.reminderapp;

public class WeatherResponse {
    public Main main;
    public Weather[] weather;
    public String name;

    public static class Main {
        public float temp;
        public float temp_max;
        public float temp_min;
    }

    public static class Weather {
        public String description;
        public String main;
        public String icon;
    }
}

