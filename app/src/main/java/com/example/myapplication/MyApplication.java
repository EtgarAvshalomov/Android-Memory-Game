package com.example.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyApplication extends Application {
    private int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (activityCount == 0 && !(activity instanceof SplashActivity)) {
                    // App came to foreground and we're not on the splash screen
                    startMusicIfNeeded();
                }
                activityCount++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    // App went to background
                    stopMusic();
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });
    }

    private void startMusicIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        if (prefs.getBoolean("soundEnabled", true)) {
            startService(new Intent(this, MusicService.class));
        }
    }

    private void stopMusic() {
        stopService(new Intent(this, MusicService.class));
    }
}
