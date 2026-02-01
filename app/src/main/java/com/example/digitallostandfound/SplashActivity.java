package com.example.digitallostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            boolean onboardingFinished = getSharedPreferences("app_prefs", MODE_PRIVATE).getBoolean("onboarding_finished", false);
            if (!onboardingFinished) {
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
                finish();
                return;
            }

            boolean isLoggedIn = getSharedPreferences("user_session", MODE_PRIVATE).contains("user_id");
            if (isLoggedIn) {
                String role = getSharedPreferences("user_session", MODE_PRIVATE).getString("user_role", "user");
                if ("admin".equals(role)) {
                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}
