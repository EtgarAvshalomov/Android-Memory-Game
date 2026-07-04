# MemoBoard

A classic memory card-matching game for Android. Flip cards to find matching pairs before the timer runs out. Scores are saved to a global leaderboard via Firebase.

## Features

- **3 Difficulty Levels** — Easy (8 cards), Medium (12 cards), Hard (16 cards)
- **60-Second Timer** — race the clock to match all pairs
- **Scoring** — 100 points per match + 10-point time bonus for every second remaining
- **Card Flip Animations** — smooth 3D flip effect on every card reveal
- **Global Leaderboard** — scores stored in Firebase Realtime Database, filterable by date
- **SMS Congratulations** — optionally send a score notification via SMS when the game ends
- **Dark / Light Theme** — toggle from the Settings screen with an animated transition
- **Background Music** — optional background music toggled in Settings
- **Splash Screen** — video intro on launch

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Platform | Android (minSdk 24, targetSdk 35) |
| Build | Gradle with Kotlin DSL |
| Backend | Firebase Realtime Database, Firebase Analytics |
| UI | ConstraintLayout, GridLayout, RecyclerView, Material Components |

## Project Structure

```
app/src/main/java/com/example/myapplication/
├── AppConfig.java          # Central constants (title, pairs, timer, card images, Firebase path)
├── MyApplication.java      # Application class
├── SplashActivity.java     # Video splash screen
├── MainActivity.java       # Main menu (Start, Settings, Scoreboard)
├── GameActivity.java       # Core game loop, card grid, timer, match logic
├── GameOverActivity.java   # Final score display, Firebase save, SMS send
├── TimeUpActivity.java     # Shown when timer expires
├── ScoresActivity.java     # Leaderboard with calendar date filter
├── SettingsActivity.java   # Difficulty, theme, sound, rating
├── ScoreRecord.java        # Firebase data model
├── MusicService.java       # Background music service
└── ThemeUtils.java         # Light/dark theme helpers
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 35
- A Firebase project with Realtime Database enabled

### Setup

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd MyApplication
   ```

2. Add your `google-services.json` to `app/` (download from the Firebase Console for your project).

3. Open the project in Android Studio and let Gradle sync.

4. Run the app on a device or emulator running Android 7.0 (API 24) or higher.

### Configuration

All tuneable constants live in `AppConfig.java`:

| Constant | Default | Description |
|---|---|---|
| `GAME_TITLE` | `"MemoBoard"` | Displayed title |
| `GAME_TIME_SECONDS` | `60` | Timer duration |
| `POINTS_PER_MATCH` | `100` | Points awarded per matched pair |
| `EASY_PAIRS` | `4` | Card pairs in Easy mode |
| `MEDIUM_PAIRS` | `6` | Card pairs in Medium mode |
| `HARD_PAIRS` | `8` | Card pairs in Hard mode |
| `FIREBASE_SCORES_PATH` | `"high_scores"` | Firebase database node |

## How to Play

1. Tap **Start Game** from the main menu.
2. Tap any card to flip it and reveal the image.
3. Tap a second card — if the images match, the pair is removed from the board.
4. Find all pairs before the 60-second timer hits zero to win.
5. On the Game Over screen, enter your name and phone number to save your score and receive an SMS.

## Permissions

| Permission | Purpose |
|---|---|
| `SEND_SMS` | Send a congratulatory SMS after a completed game |
| Internet | Firebase Realtime Database access |
