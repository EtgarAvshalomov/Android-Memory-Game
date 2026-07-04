package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TimeUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_up);

        Button btnTryAgain = findViewById(R.id.btnTryAgain);
        Button btnExit = findViewById(R.id.btnExitToMain);

        btnTryAgain.setOnClickListener(v -> {
            startActivity(new Intent(TimeUpActivity.this, GameActivity.class));
            finish();
        });

        btnExit.setOnClickListener(v -> {
            startActivity(new Intent(TimeUpActivity.this, MainActivity.class));
            finish();
        });

        ThemeUtils.applyTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
