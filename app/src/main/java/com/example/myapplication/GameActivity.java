package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private int score = 0;
    private int pairsFound = 0;
    private int totalPairs;
    private String difficulty;
    private boolean gameFinished = false;
    private long timeLeftInMillis;

    private TextView tvScore, tvTimer;
    private GridLayout glGameBoard;
    private CountDownTimer countDownTimer;

    private List<Integer> cardImagesList;
    private ImageButton firstClickedCard, secondClickedCard;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        glGameBoard = findViewById(R.id.glGameBoard);

        SharedPreferences prefs = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        difficulty = prefs.getString("difficulty", "Easy");

        setupGame();

        findViewById(R.id.btnStopGame).setOnClickListener(v -> {
            stopGame();
            finish();
        });
        
        ThemeUtils.applyTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }

    private void setupGame() {
        if (difficulty.equals("Easy")) {
            totalPairs = AppConfig.EASY_PAIRS;
            glGameBoard.setColumnCount(4);
            glGameBoard.setRowCount(2);
        } else if (difficulty.equals("Medium")) {
            totalPairs = AppConfig.MEDIUM_PAIRS;
            glGameBoard.setColumnCount(4);
            glGameBoard.setRowCount(3);
        } else {
            totalPairs = AppConfig.HARD_PAIRS;
            glGameBoard.setColumnCount(4);
            glGameBoard.setRowCount(4);
        }

        cardImagesList = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            cardImagesList.add(AppConfig.CARD_IMAGES[i]);
            cardImagesList.add(AppConfig.CARD_IMAGES[i]);
        }
        Collections.shuffle(cardImagesList);

        // 1. Wait for the layout to finish sizing itself so we know how much room we have
        glGameBoard.post(() -> {
            int gridWidth = glGameBoard.getWidth();
            int gridHeight = glGameBoard.getHeight();

            int cols = glGameBoard.getColumnCount();
            int rows = glGameBoard.getRowCount();

            // 2. Convert your 8dp margins to pixels
            float scale = getResources().getDisplayMetrics().density;
            int marginPx = (int) (8 * scale + 0.5f);

            // 3. Calculate the absolute MAXIMUM space one single card can take up in the grid
            int maxCellWidth = (gridWidth / cols) - (marginPx * 2);
            int maxCellHeight = (gridHeight / rows) - (marginPx * 2);

            int cardWidthPx;
            int cardHeightPx;

            // 4. Figure out the biggest perfect 9:16 ratio that fits inside that maximum space
            if (maxCellWidth * 16.0 / 9.0 > maxCellHeight) {
                // The screen is short, so height is the limiting factor
                cardHeightPx = maxCellHeight;
                cardWidthPx = (int) (cardHeightPx * 9.0 / 16.0);
            } else {
                // The screen is tall, so width is the limiting factor
                cardWidthPx = maxCellWidth;
                cardHeightPx = (int) (cardWidthPx * 16.0 / 9.0);
            }

            // 5. Build the cards with our perfect calculations
            for (int i = 0; i < cardImagesList.size(); i++) {
                ImageButton card = new ImageButton(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                params.width = cardWidthPx;
                params.height = cardHeightPx;

                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setGravity(android.view.Gravity.CENTER);
                params.setMargins(marginPx, marginPx, marginPx, marginPx);
                card.setLayoutParams(params);

                card.setPadding(0, 0, 0, 0);
                card.setBackground(null);

                card.setImageResource(AppConfig.CARD_BACK_IMAGE);
                card.setScaleType(ImageView.ScaleType.CENTER_CROP);
                card.setTag(cardImagesList.get(i));
                card.setContentDescription("Memory Card");

                card.setOnClickListener(v -> onCardClicked(card));
                glGameBoard.addView(card);
            }

            // 6. Start the timer AFTER the cards are actually drawn on the screen!
            startTimer();
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(AppConfig.GAME_TIME_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                if (!gameFinished) {
                    tvTimer.setText("Time: " + (millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (!gameFinished) {
                    startActivity(new Intent(GameActivity.this, TimeUpActivity.class));
                    finish();
                }
            }
        }.start();
    }

    private void onCardClicked(ImageButton clickedCard) {
        if (gameFinished || isProcessing || clickedCard == firstClickedCard || clickedCard.getVisibility() == View.INVISIBLE) {
            return;
        }

        flipCard(clickedCard, (Integer) clickedCard.getTag());

        if (firstClickedCard == null) {
            firstClickedCard = clickedCard;
        } else {
            secondClickedCard = clickedCard;
            checkMatch();
        }
    }

    private void checkMatch() {
        isProcessing = true;
        if (firstClickedCard.getTag().equals(secondClickedCard.getTag())) {
            score += AppConfig.POINTS_PER_MATCH;
            pairsFound++;
            tvScore.setText("Score: " + score);
            
            new Handler().postDelayed(() -> {
                firstClickedCard.setVisibility(View.INVISIBLE);
                secondClickedCard.setVisibility(View.INVISIBLE);
                resetSelection();
                if (pairsFound == totalPairs) {
                    winGame();
                }
            }, 500);
        } else {
            new Handler().postDelayed(() -> {
                flipBack(firstClickedCard);
                flipBack(secondClickedCard);
                resetSelection();
            }, 1000);
        }
    }

    private void resetSelection() {
        firstClickedCard = null;
        secondClickedCard = null;
        isProcessing = false;
    }

    private void flipCard(ImageButton card, int imageRes) {
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);
        flipOut.setTarget(card);
        flipIn.setTarget(card);
        
        flipOut.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) {
                card.setImageResource(imageRes);
                flipIn.start();
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        flipOut.start();
    }

    private void flipBack(ImageButton card) {
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);
        flipOut.setTarget(card);
        flipIn.setTarget(card);

        flipOut.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) {
                card.setImageResource(AppConfig.CARD_BACK_IMAGE);
                flipIn.start();
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        flipOut.start();
    }

    private void winGame() {
        gameFinished = true;
        stopGame();
        
        // Add Time Bonus: 10 points for every second left
        int timeBonus = (int) (timeLeftInMillis / 1000) * 10;
        score += timeBonus;

        Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
        finish();
    }

    private void stopGame() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGame();
    }
}
