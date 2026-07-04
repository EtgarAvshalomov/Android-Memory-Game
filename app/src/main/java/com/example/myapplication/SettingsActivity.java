package com.example.myapplication;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgDifficulty;
    private Switch switchTheme;
    private CheckBox cbSound;
    private RatingBar ratingBar;
    private SharedPreferences sharedPreferences;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootLayout = findViewById(android.R.id.content);
        rgDifficulty = findViewById(R.id.rgDifficulty);
        switchTheme = findViewById(R.id.switchTheme);
        cbSound = findViewById(R.id.cbSound);
        ratingBar = findViewById(R.id.ratingBar);
        Button btnBack = findViewById(R.id.btnBack);

        loadSettings();
        ThemeUtils.applyTheme(this); 

        // Instant save for Difficulty
        rgDifficulty.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkedId == R.id.rbEasy) editor.putString("difficulty", "Easy");
            else if (checkedId == R.id.rbMedium) editor.putString("difficulty", "Medium");
            else if (checkedId == R.id.rbHard) editor.putString("difficulty", "Hard");
            editor.apply();
        });

        // Instant save and animation for Theme
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("darkMode", isChecked).apply();
            ThemeUtils.applyThemeWithTransition(this, isChecked);
        });

        // Instant save and music control for Sound
        cbSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("soundEnabled", isChecked).apply();
            Intent musicIntent = new Intent(this, MusicService.class);
            if (isChecked) {
                startService(musicIntent);
            } else {
                stopService(musicIntent);
            }
        });

        // Instant save for Rating
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                sharedPreferences.edit().putFloat("rating", rating).apply();
            }
        });

        // "Back" button just closes the activity
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }

    private void loadSettings() {
        String difficulty = sharedPreferences.getString("difficulty", "Easy");
        if (difficulty.equals("Easy")) rgDifficulty.check(R.id.rbEasy);
        else if (difficulty.equals("Medium")) rgDifficulty.check(R.id.rbMedium);
        else if (difficulty.equals("Hard")) rgDifficulty.check(R.id.rbHard);

        switchTheme.setChecked(sharedPreferences.getBoolean("darkMode", false));
        cbSound.setChecked(sharedPreferences.getBoolean("soundEnabled", true));
        ratingBar.setRating(sharedPreferences.getFloat("rating", 0f));
    }
}
