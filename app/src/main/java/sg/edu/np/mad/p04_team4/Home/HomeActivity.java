package sg.edu.np.mad.p04_team4.Home;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.Chat.ChatHomeActivity;
import sg.edu.np.mad.p04_team4.DailyLoginReward.DailyRewardDialogFragment;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ShopActivity;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.Feedback.FeedbackActivity;
import sg.edu.np.mad.p04_team4.Friendship_Event.Friendship_Events;
import sg.edu.np.mad.p04_team4.HabitTracker.selectHabit;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.ScreenTime.ScreenTimeService;
import sg.edu.np.mad.p04_team4.Timer.Stopwatch_Timer;
import sg.edu.np.mad.p04_team4.ToDoList.MainActivity_TodoList;

import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ScreenTimeService screenTimeService;
    private boolean isBound = false;
    private static final String TAG = "HomeActivity";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ScreenTimeService.LocalBinder binder = (ScreenTimeService.LocalBinder) service;
            screenTimeService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Apply the new theme
            View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
            ThemeUtils.applyTheme(HomeActivity.this, rootView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        // Register the broadcast receiver for theme changes
        IntentFilter filter = new IntentFilter("sg.edu.np.mad.p04_team4.THEME_CHANGED");
        registerReceiver(themeChangeReceiver, filter);

        Button languageButton = findViewById(R.id.languageButton);
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLanguage();
            }
        });

        // Start the foreground service
        Intent serviceIntent = new Intent(this, ScreenTimeService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated");
            // Handle unauthenticated user
            return;
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "User is authenticated: " + userId);

        // Check if the daily reward dialog should be shown
        if (DailyRewardDialogFragment.shouldShowReward(this)) {
            DailyRewardDialogFragment dailyRewardDialogFragment = new DailyRewardDialogFragment();
            dailyRewardDialogFragment.show(getSupportFragmentManager(), "DailyRewardDialog");
        }

        // Set Click Listener for the "Message your friends!" layout
        RelativeLayout messageFriendsLayout = findViewById(R.id.chat);
        messageFriendsLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("Message your friends!");
            }
            Intent intent = new Intent(HomeActivity.this, ChatHomeActivity.class);
            startActivity(intent);
        });

        // Set Click Listener for the "Stopwatch/Timer" layout
        RelativeLayout stopwatchTimerLayout = findViewById(R.id.stopwatch);
        stopwatchTimerLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("Stopwatch/Timer");
            }
            Intent intent = new Intent(HomeActivity.this, Stopwatch_Timer.class);
            startActivity(intent);
        });

        // Set Click Listener for the "Feedback" layout
        RelativeLayout feedbackLayout = findViewById(R.id.feedback);
        feedbackLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("Feedback");
            }
            Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        // Start AccountActivity
        RelativeLayout Account = findViewById(R.id.account);
        Account.setOnClickListener(v -> {
            Intent accountIntent = new Intent(HomeActivity.this, AccountActivity.class);
            accountIntent.putExtra("name", currentUser.getDisplayName());
            accountIntent.putExtra("email", currentUser.getEmail());
            startActivity(accountIntent);
        });

        // Set Click Listener for the "Challenge yourself!" layout
        RelativeLayout challengeYourselfLayout = findViewById(R.id.fitness);
        challengeYourselfLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("Challenge yourself!");
            }
            Intent intent = new Intent(HomeActivity.this, Friendship_Events.class);
            String userId1 = currentUser.getUid();
            intent.putExtra("userid", userId1);
            startActivity(intent);
        });

        // Set Click Listener for the "To-Do List" layout
        RelativeLayout todoListLayout = findViewById(R.id.notepad);
        todoListLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("To-Do List");
            }
            Intent intent = new Intent(HomeActivity.this, MainActivity_TodoList.class);
            startActivity(intent);
        });

        RelativeLayout rewardLayout = findViewById(R.id.reward);
        rewardLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("Reward");
            }
            Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        // Set Click Listener for the "Screen Time" layout
        RelativeLayout HabitTrackerLayout = findViewById(R.id.chart);
        HabitTrackerLayout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer("Habit Tracker");
            }
            Intent intent = new Intent(HomeActivity.this, selectHabit.class);
            startActivity(intent);
        });
    }

    private void toggleLanguage() {
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        String currentLanguage = prefs.getString("language_code", "en"); // Default to English

        String newLanguage = currentLanguage.equals("en") ? "cn" : "en";
        changeLanguage(newLanguage);
        recreate(); // Recreate activity to apply the new locale
    }

    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Persist the language preference
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language_code", languageCode);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load saved language preference
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language_code", "en"); // Default to English
        changeLanguage(languageCode);
    }

    private boolean isSameDay(Calendar lastShown, Calendar today) {
        return lastShown.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && lastShown.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            // Stop all feature timers when the activity is paused
            screenTimeService.stopFeatureTimer("Message your friends!");
            screenTimeService.stopFeatureTimer("Stopwatch/Timer");
            screenTimeService.stopFeatureTimer("Challenge yourself!");
            screenTimeService.stopFeatureTimer("To-Do List");
            screenTimeService.stopFeatureTimer("Screen Time");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        // Unregister the broadcast receiver
        unregisterReceiver(themeChangeReceiver);
    }
}