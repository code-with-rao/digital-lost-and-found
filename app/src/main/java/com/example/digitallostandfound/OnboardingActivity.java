package com.example.digitallostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button btnNext = findViewById(R.id.btnNext);
        TextView btnSkip = findViewById(R.id.btnSkip);

        btnNext.setOnClickListener(v -> finishOnboarding());
        btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
        editor.putBoolean("onboarding_finished", true);
        editor.apply();

        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
        finish();
    }
}
