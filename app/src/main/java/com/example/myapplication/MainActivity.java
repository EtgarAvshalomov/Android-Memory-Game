package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvTitle = findViewById(R.id.tvGameTitle);
        Button btnStart = findViewById(R.id.btnStartGame);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnScoreboard = findViewById(R.id.btnScoreboard);

        tvTitle.setText(AppConfig.GAME_TITLE);

        btnStart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GameActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        btnScoreboard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ScoresActivity.class)));

        ThemeUtils.applyTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fix 2: Apply theme immediately when returning to this screen
        ThemeUtils.applyTheme(this);
    }
}
