package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private Runnable fallbackRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.splashVideoView);
        try {
            String path = "android.resource://" + getPackageName() + "/" + R.raw.splash_video;
            Uri videoUri = Uri.parse(path);
            videoView.setVideoURI(videoUri);
            
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(false);
                videoView.start();
            });
            
            videoView.setOnErrorListener((mp, what, extra) -> {
                proceedToMain();
                return true;
            });

            videoView.setOnCompletionListener(mp -> proceedToMain());

        } catch (Exception e) {
            e.printStackTrace();
            proceedToMain();
        }

        // Increased fallback timer to 8 seconds to allow the 6-second video to finish
        fallbackRunnable = this::proceedToMain;
        handler.postDelayed(fallbackRunnable, 8000);

        ThemeUtils.applyTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }

    private void proceedToMain() {
        if (!isFinishing()) {
            // Start the background music if sound is enabled
            SharedPreferences prefs = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
            if (prefs.getBoolean("soundEnabled", true)) {
                startService(new Intent(this, MusicService.class));
            }

            // Remove the callback so it doesn't trigger twice
            if (handler != null && fallbackRunnable != null) {
                handler.removeCallbacks(fallbackRunnable);
            }
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}
