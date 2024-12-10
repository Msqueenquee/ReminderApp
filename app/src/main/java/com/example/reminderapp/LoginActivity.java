package com.example.reminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupLink;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ReminderAppPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToHome();
        }

        // Initialize Views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);

        // Handle Login Button Click
        loginButton.setOnClickListener(v -> loginUser());

        // Handle Signup Link Click
        signupLink.setOnClickListener(v -> navigateToSignup());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Save login status in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void navigateToSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
