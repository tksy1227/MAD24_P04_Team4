package sg.edu.np.mad.p04_team4;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.Chat.ChatHomeActivity;
import sg.edu.np.mad.p04_team4.DailyLoginReward.DailyRewardDialogFragment;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ShopActivity;
import sg.edu.np.mad.p04_team4.Friendship_Event.Friendship_Events;
import sg.edu.np.mad.p04_team4.HabitTracker.selectHabit;
import sg.edu.np.mad.p04_team4.ScreenTime.ScreenTimeService;
import sg.edu.np.mad.p04_team4.ScreenTime.ScreenTime_Main;
import sg.edu.np.mad.p04_team4.Timer.Stopwatch_Timer;
import sg.edu.np.mad.p04_team4.ToDoList.MainActivity_TodoList;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

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

        // Check if the daily reward dialog has been shown today
        SharedPreferences prefs = getSharedPreferences("DailyRewardPrefs", MODE_PRIVATE);
        long lastShownTime = prefs.getLong(userId + "_lastShownTime", 0);
        Calendar today = Calendar.getInstance();
        Calendar lastShown = Calendar.getInstance();
        lastShown.setTimeInMillis(lastShownTime);

        if (!isSameDay(lastShown, today)) {
            // Show the Daily Login Reward Dialog
            DailyRewardDialogFragment dailyRewardDialogFragment = new DailyRewardDialogFragment();
            dailyRewardDialogFragment.show(getSupportFragmentManager(), "DailyRewardDialog");

            // Update the last shown time
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(userId + "_lastShownTime", today.getTimeInMillis());
            editor.apply();
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
        //RelativeLayout screenTimeLayout = findViewById(R.id.chart);
        //screenTimeLayout.setOnClickListener(v -> {
        //    if (isBound) {
        //        screenTimeService.startFeatureTimer("Screen Time");
        //    }
        //    Intent intent = new Intent(HomeActivity.this, ScreenTime_Main.class);
        //    startActivity(intent);
        //});
        RelativeLayout HabitTrackerLayout = findViewById(R.id.chart);
        HabitTrackerLayout.setOnClickListener(v->{
            if (isBound) {
                screenTimeService.startFeatureTimer("Habit Tracker");
                }
                Intent intent = new Intent(HomeActivity.this, selectHabit.class);
                startActivity(intent);

        });
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
    }
}