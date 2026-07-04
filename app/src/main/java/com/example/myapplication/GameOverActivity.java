package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameOverActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;
    private int score;
    private String difficulty;
    private EditText etPlayerName, etPhoneNumber;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getStringExtra("difficulty");

        TextView tvFinalScore = findViewById(R.id.tvFinalScore);
        tvFinalScore.setText("Your Score: " + score);

        etPlayerName = findViewById(R.id.etPlayerName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Button btnSave = findViewById(R.id.btnSaveScore);

        databaseReference = FirebaseDatabase.getInstance().getReference(AppConfig.FIREBASE_SCORES_PATH);

        btnSave.setOnClickListener(v -> {
            String name = etPlayerName.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveScoreToFirebase(name);
            checkSmsPermissionAndSend(name, phone);
            
            startActivity(new Intent(GameOverActivity.this, ScoresActivity.class));
            finish();
        });

        ThemeUtils.applyTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }

    private void saveScoreToFirebase(String name) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        float rating = prefs.getFloat("rating", 0f);

        ScoreRecord record = new ScoreRecord(name, score, date, difficulty, rating);
        databaseReference.push().setValue(record);
    }

    private void checkSmsPermissionAndSend(String name, String phone) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            sendSMS(name, phone);
        }
    }

    private void sendSMS(String playerName, String phoneNo) {
        String message = AppConfig.DEFAULT_SMS_MESSAGE_PREFIX + playerName + "! Your score is " + score;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String name = etPlayerName.getText().toString();
                String phone = etPhoneNumber.getText().toString();
                sendSMS(name, phone);
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
