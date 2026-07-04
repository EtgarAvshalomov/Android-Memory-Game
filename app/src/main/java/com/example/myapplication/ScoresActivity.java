package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ScoresActivity extends AppCompatActivity {

    private RecyclerView rvScores;
    private ScoreAdapter adapter;
    private List<ScoreRecord> allScores;
    private List<ScoreRecord> displayScores;
    private DatabaseReference databaseReference;
    private String selectedDate = null; // Format: yyyy-MM-dd
    private WebView webView;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        rvScores = findViewById(R.id.rvScores);
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        allScores = new ArrayList<>();
        displayScores = new ArrayList<>();
        adapter = new ScoreAdapter(displayScores);
        rvScores.setAdapter(adapter);

        tvEmptyState = findViewById(R.id.tvEmptyState);
        CalendarView calendarView = findViewById(R.id.calendarView);
        webView = findViewById(R.id.webViewInfo);
        Button btnBack = findViewById(R.id.btnBackToMain);

        databaseReference = FirebaseDatabase.getInstance().getReference(AppConfig.FIREBASE_SCORES_PATH);
        loadScores();

        btnBack.setOnClickListener(v -> finish());
        
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            filterScores();
        });
        
        ThemeUtils.applyTheme(this);
        updateWebViewTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
        updateWebViewTheme();
    }

    private void updateWebViewTheme() {
        boolean isDarkMode = getSharedPreferences("GameSettings", MODE_PRIVATE).getBoolean("darkMode", false);
        // Use semi-transparent background for the HTML content to keep text readable over the background image
        String bgColor = isDarkMode ? "rgba(0,0,0,0.5)" : "rgba(255,255,255,0.5)";
        String textColor = isDarkMode ? "white" : "black";

        String summaryHtml = "<html><body style='background-color:" + bgColor + "; color:" + textColor + "; font-family: sans-serif; padding: 10px;'>" +
                "<h3>Game Info</h3>" +
                "<p>Match pairs to win! Difficulty levels: Easy(8 cards), Medium(12 cards), Hard(16 cards).</p>" +
                "</body></html>";
        webView.loadData(summaryHtml, "text/html", "UTF-8");
        webView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void loadScores() {
        databaseReference.orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allScores.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ScoreRecord record = postSnapshot.getValue(ScoreRecord.class);
                    if (record != null) {
                        allScores.add(record);
                    }
                }
                Collections.reverse(allScores); // Descending order
                filterScores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScoresActivity.this, "Failed to load scores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterScores() {
        displayScores.clear();
        if (selectedDate == null) {
            displayScores.addAll(allScores);
        } else {
            for (ScoreRecord record : allScores) {
                if (record.date != null && record.date.startsWith(selectedDate)) {
                    displayScores.add(record);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
        
        if (displayScores.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            if (selectedDate != null) {
                tvEmptyState.setText("No scores for this date: " + selectedDate);
            } else {
                tvEmptyState.setText("No scores available");
            }
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private class ScoreAdapter extends RecyclerView.Adapter<ScoreViewHolder> {
        private List<ScoreRecord> list;
        public ScoreAdapter(List<ScoreRecord> list) { this.list = list; }
        @NonNull @Override public ScoreViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
            return new ScoreViewHolder(view);
        }
        @Override public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
            ScoreRecord record = list.get(position);
            holder.name.setText(record.playerName);
            holder.diff.setText(record.difficulty);
            holder.score.setText(String.valueOf(record.score));
            
            boolean isDarkMode = getSharedPreferences("GameSettings", MODE_PRIVATE).getBoolean("darkMode", false);
            int textColor = isDarkMode ? Color.WHITE : Color.BLACK;
            int secondaryColor = isDarkMode ? Color.LTGRAY : Color.DKGRAY;

            holder.name.setTextColor(textColor);
            holder.diff.setTextColor(secondaryColor);
            holder.score.setTextColor(textColor);
        }
        @Override public int getItemCount() { return list.size(); }
    }

    private static class ScoreViewHolder extends RecyclerView.ViewHolder {
        android.widget.TextView name, diff, score;
        public ScoreViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvPlayerName);
            diff = itemView.findViewById(R.id.tvDifficulty);
            score = itemView.findViewById(R.id.tvScoreValue);
        }
    }
}