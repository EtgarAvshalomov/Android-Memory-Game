package com.example.myapplication;

public class AppConfig {

    public static final String GAME_TITLE = "MemoBoard";

    // Video URI pointing to your splash_video.mp4 in res/raw
    public static final String VIDEO_URI = "android.resource://com.example.myapplication/" + R.raw.splash_video;

    public static final String FIREBASE_SCORES_PATH = "high_scores";

    public static final int GAME_TIME_SECONDS = 60;

    public static final int POINTS_PER_MATCH = 100;

    // Difficulty updated:
    // Easy = 8 cards total (4 pairs)
    // Medium = 12 cards total (6 pairs)
    // Hard = 16 cards total (8 pairs)
    public static final int EASY_PAIRS = 4;
    public static final int MEDIUM_PAIRS = 6;
    public static final int HARD_PAIRS = 8;

    public static final String DEFAULT_SMS_MESSAGE_PREFIX = "Congratulations ";

    public static final int[] CARD_IMAGES = {
        R.drawable.animal_1, R.drawable.animal_2, R.drawable.animal_3, R.drawable.animal_4,
        R.drawable.animal_5, R.drawable.animal_6, R.drawable.animal_7, R.drawable.animal_8
    };

    public static final int CARD_BACK_IMAGE = R.drawable.card_back;

    public static final int APP_ICON_IMAGE = R.mipmap.ic_launcher;
}
