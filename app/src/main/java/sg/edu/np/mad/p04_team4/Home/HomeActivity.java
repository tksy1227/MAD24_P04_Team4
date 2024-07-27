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

import sg.edu.np.mad.p04_team4.Calender.MainCalender;
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
import sg.edu.np.mad.p04_team4.ScreenTime.ScreenTime_Main;
import sg.edu.np.mad.p04_team4.Timer.Stopwatch_Timer;
import sg.edu.np.mad.p04_team4.ToDoList.MainActivity_TodoList;

import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ScreenTimeService screenTimeService;
    private boolean isBound = false;
    private static final String TAG = "HomeActivity";

    // Service connection to manage the binding and unbinding of the service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Bind to the service when connected
            ScreenTimeService.LocalBinder binder = (ScreenTimeService.LocalBinder) service;
            screenTimeService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Handle the service disconnection
            isBound = false;
        }
    };

    // Broadcast receiver to handle theme change events
    private BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Apply the new theme when a theme change event is received
            View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
            ThemeUtils.applyTheme(HomeActivity.this, rootView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage); // Set the layout for the activity

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView); // Apply the selected theme

        // Register the broadcast receiver for theme changes
        IntentFilter filter = new IntentFilter("sg.edu.np.mad.p04_team4.THEME_CHANGED");
        registerReceiver(themeChangeReceiver, filter);

        // Start and bind the screen time tracking service
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

        // Set up click listeners for each feature
        setupClickListener(R.id.chat, "Message your friends!", ChatHomeActivity.class);
        setupClickListener(R.id.stopwatch, "Stopwatch/Timer", Stopwatch_Timer.class);
        setupClickListener(R.id.feedback, "Feedback", FeedbackActivity.class);
        setupClickListener(R.id.screentime, "Screentime", ScreenTime_Main.class);
        setupClickListener(R.id.account, "Account", AccountActivity.class, currentUser);
        setupClickListener(R.id.fitness, "Challenge yourself!", Friendship_Events.class, currentUser);
        setupClickListener(R.id.notepad, "To-Do List", MainActivity_TodoList.class);
        setupClickListener(R.id.reward, "Reward", ShopActivity.class);
        setupClickListener(R.id.chart, "Habit Tracker", selectHabit.class);
        setupClickListener(R.id.calendar, "Calendar", MainCalender.class);


    }

    // Method to set up click listeners for features without additional user data
    private void setupClickListener(int layoutId, String featureName, Class<?> activityClass) {
        RelativeLayout layout = findViewById(layoutId);
        layout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer(featureName); // Start the feature timer
            }
            Intent intent = new Intent(HomeActivity.this, activityClass);
            startActivity(intent);
        });
    }

    // Overloaded method to set up click listeners for features with additional user data
    private void setupClickListener(int layoutId, String featureName, Class<?> activityClass, FirebaseUser currentUser) {
        RelativeLayout layout = findViewById(layoutId);
        layout.setOnClickListener(v -> {
            if (isBound) {
                screenTimeService.startFeatureTimer(featureName); // Start the feature timer
            }
            Intent intent = new Intent(HomeActivity.this, activityClass);
            if (activityClass.equals(AccountActivity.class)) {
                intent.putExtra("name", currentUser.getDisplayName());
                intent.putExtra("email", currentUser.getEmail());
            } else if (activityClass.equals(Friendship_Events.class)) {
                intent.putExtra("userid", currentUser.getUid());
            }
            startActivity(intent);
        });
    }

    // Method to toggle the language of the app
    private void toggleLanguage() {
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        String currentLanguage = prefs.getString("language_code", "en"); // Default to English

        String newLanguage = currentLanguage.equals("en") ? "cn" : "en";
        changeLanguage(newLanguage);
        recreate(); // Recreate activity to apply the new locale
    }

    // Method to change the language of the app
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
        // Load saved language preference and apply it
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language_code", "en"); // Default to English
        changeLanguage(languageCode);
    }

    // Method to check if two Calendar dates are the same day
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
            screenTimeService.stopFeatureTimer("Feedback");
            screenTimeService.stopFeatureTimer("Screentime");
            screenTimeService.stopFeatureTimer("Account");
            screenTimeService.stopFeatureTimer("Challenge yourself!");
            screenTimeService.stopFeatureTimer("To-Do List");
            screenTimeService.stopFeatureTimer("Reward");
            screenTimeService.stopFeatureTimer("Habit Tracker");
            screenTimeService.stopFeatureTimer("Calendar");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection); // Unbind the service
            isBound = false;
        }
        // Unregister the broadcast receiver
        unregisterReceiver(themeChangeReceiver);
    }
}
