package com.example.myapplication;

public class ScoreRecord {
    public String playerName;
    public int score;
    public String date;
    public String difficulty;
    public float rating;

    public ScoreRecord() {
        // Default constructor required for calls to DataSnapshot.getValue(ScoreRecord.class)
    }

    public ScoreRecord(String playerName, int score, String date, String difficulty, float rating) {
        this.playerName = playerName;
        this.score = score;
        this.date = date;
        this.difficulty = difficulty;
        this.rating = rating;
    }
}
