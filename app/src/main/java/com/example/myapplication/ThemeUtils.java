package com.example.myapplication;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;

public class ThemeUtils {

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("darkMode", false);

        // Enable Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        int textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        int bgResId = isDarkMode ? R.drawable.background_dark : R.drawable.background_light;

        View decorView = activity.getWindow().getDecorView();
        WindowCompat.getInsetsController(activity.getWindow(), decorView).setAppearanceLightStatusBars(!isDarkMode);
        WindowCompat.getInsetsController(activity.getWindow(), decorView).setAppearanceLightNavigationBars(!isDarkMode);

        View rootView = activity.findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.setBackgroundResource(bgResId);
            updateViewColors(rootView, textColor, isDarkMode);
            applyWindowInsets(rootView);
            ViewCompat.requestApplyInsets(rootView);
        }
    }

    public static void applyThemeWithTransition(Activity activity, boolean toDark) {
        View rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) return;

        int textColorFrom = toDark ? Color.BLACK : Color.WHITE;
        int textColorTo = toDark ? Color.WHITE : Color.BLACK;
        
        // Background transition
        Drawable fromDrawable = ContextCompat.getDrawable(activity, toDark ? R.drawable.background_light : R.drawable.background_dark);
        Drawable toDrawable = ContextCompat.getDrawable(activity, toDark ? R.drawable.background_dark : R.drawable.background_light);
        
        if (fromDrawable != null && toDrawable != null) {
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{fromDrawable, toDrawable});
            rootView.setBackground(transitionDrawable);
            transitionDrawable.startTransition(500);
        } else {
            rootView.setBackgroundResource(toDark ? R.drawable.background_dark : R.drawable.background_light);
        }

        // Smooth text color transition
        ValueAnimator colorAnimation = ValueAnimator.ofFloat(0f, 1f);
        colorAnimation.setDuration(500);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        
        colorAnimation.addUpdateListener(animator -> {
            float fraction = (float) animator.getAnimatedValue();
            int currentText = (int) evaluator.evaluate(fraction, textColorFrom, textColorTo);
            updateViewColors(rootView, currentText, toDark);
        });
        colorAnimation.start();

        // Update system bar icons
        View decorView = activity.getWindow().getDecorView();
        WindowCompat.getInsetsController(activity.getWindow(), decorView).setAppearanceLightStatusBars(!toDark);
        WindowCompat.getInsetsController(activity.getWindow(), decorView).setAppearanceLightNavigationBars(!toDark);
    }

    public static void applyWindowInsets(View view) {
        Object tag = view.getTag(R.id.tag_initial_padding);
        final int[] initialPadding;
        if (tag instanceof int[]) {
            initialPadding = (int[]) tag;
        } else {
            initialPadding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
            view.setTag(R.id.tag_initial_padding, initialPadding);
        }

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    initialPadding[0] + insets.left,
                    initialPadding[1] + insets.top,
                    initialPadding[2] + insets.right,
                    initialPadding[3] + insets.bottom
            );
            return windowInsets;
        });
    }

    public static void updateViewColors(View rootView, int textColor, boolean isDarkMode) {
        List<View> views = new ArrayList<>();
        collectAllViews(rootView, views);
        for (View v : views) {
            updateSingleView(v, textColor, isDarkMode);
        }
    }

    private static void collectAllViews(View view, List<View> outList) {
        outList.add(view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                collectAllViews(group.getChildAt(i), outList);
            }
        }
    }

    public static void updateSingleView(View v, int textColor, boolean isDarkMode) {
        // Apply semi-transparent backgrounds to designated containers
        Object tag = v.getTag();
        if (v.getId() == R.id.text_container || "text_container".equals(tag)) {
            v.setBackgroundResource(isDarkMode ? R.drawable.bg_text_container_dark : R.drawable.bg_text_container_light);
        } else if (v.getId() == R.id.score_row_container || "score_row_container".equals(tag)) {
            v.setBackgroundResource(isDarkMode ? R.drawable.bg_score_row_dark_translucent : R.drawable.bg_score_row_light_translucent);
        }

        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            if (tv.getId() == R.id.btnBack || tv.getId() == R.id.btnBackToMain || tv.getId() == R.id.btnSaveScore) {
                // Keep turquoise/green buttons as specified
                if (tv.getId() == R.id.btnSaveScore) {
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                } else {
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                }
                tv.setTextColor(Color.WHITE);
            } else {
                tv.setTextColor(textColor);
                if (v instanceof EditText) {
                    ((EditText) v).setHintTextColor(isDarkMode ? Color.GRAY : Color.LTGRAY);
                }
            }
            
            if (v instanceof CompoundButton) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{
                                isDarkMode ? Color.LTGRAY : Color.DKGRAY,
                                isDarkMode ? Color.WHITE : Color.parseColor("#4CAF50")
                        }
                );
                ((CompoundButton) v).setButtonTintList(colorStateList);
                
                try {
                    String className = v.getClass().getSimpleName();
                    if (className.contains("Switch")) {
                        v.getClass().getMethod("setThumbTintList", ColorStateList.class).invoke(v, colorStateList);
                        v.getClass().getMethod("setTrackTintList", ColorStateList.class).invoke(v, ColorStateList.valueOf(isDarkMode ? Color.DKGRAY : Color.LTGRAY));
                    }
                } catch (Exception ignored) {}
            }
        } else if (v instanceof RatingBar) {
            RatingBar rb = (RatingBar) v;
            rb.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            rb.setProgressBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            rb.setSecondaryProgressTintList(ColorStateList.valueOf(Color.LTGRAY));
        } else if (v instanceof WebView) {
            v.setBackgroundColor(Color.TRANSPARENT);
        } else if (v instanceof CalendarView) {
            // CalendarView should always be white/light as requested
            v.setBackgroundColor(Color.WHITE);
            v.post(() -> {
                List<View> children = new ArrayList<>();
                collectAllViews(v, children);
                for (View child : children) {
                    if (child instanceof TextView && child != v) {
                        // Keep text black for the white calendar
                        ((TextView) child).setTextColor(Color.BLACK);
                    }
                }
            });
        }
    }
}
