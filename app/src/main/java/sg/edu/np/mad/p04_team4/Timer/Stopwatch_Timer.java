package sg.edu.np.mad.p04_team4.Timer;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sg.edu.np.mad.p04_team4.AddFriend.FriendListActivity;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;

public class Stopwatch_Timer extends AppCompatActivity {
    public static final String CHANNEL_ID = "timer_channel";
    private EditText editTextTime;
    private EditText editTextPurpose;
    private Button buttonStart;
    private Button buttonTenSec, buttonOneMin, buttonThreeMin;
    private Button buttonStop, buttonPause;
    private Button buttonTimerHistory;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isTimerRunning;
    private boolean isPaused;
    private DatabaseHelper databaseHelper;
    private long initialTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch_timer);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        createNotificationChannel();

        databaseHelper = new DatabaseHelper(this);

        editTextTime = findViewById(R.id.timerText);
        editTextPurpose = findViewById(R.id.purposeText);
        buttonStart = findViewById(R.id.startButton);
        buttonTenSec = findViewById(R.id.tenSecButton);
        buttonOneMin = findViewById(R.id.oneMinButton);
        buttonThreeMin = findViewById(R.id.threeMinButton);
        buttonStop = findViewById(R.id.stopButton);
        buttonPause = findViewById(R.id.pauseButton);
        buttonTimerHistory = findViewById(R.id.timerHistory);
        ImageButton backButton = findViewById(R.id.backButton);

        buttonTenSec.setOnClickListener(v -> editTextTime.setText("00:00:10"));
        buttonOneMin.setOnClickListener(v -> editTextTime.setText("00:01:00"));
        buttonThreeMin.setOnClickListener(v -> editTextTime.setText("00:03:00"));

        buttonStart.setOnClickListener(v -> startTimer());
        buttonPause.setOnClickListener(v -> pauseTimer());
        buttonStop.setOnClickListener(v -> stopTimer());

        Button helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());

        buttonTimerHistory.setOnClickListener(v -> {
            Intent intent = new Intent(Stopwatch_Timer.this, TimerLogActivity.class);
            startActivity(intent);
        });

        // Handle custom back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the HomeActivity
                Intent intent = new Intent(Stopwatch_Timer.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Navigation buttons
        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> navigateToActivity(HomeActivity.class));

        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> navigateToActivity(FriendListActivity.class));

        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> navigateToActivity(AccountActivity.class));
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(Stopwatch_Timer.this, activityClass);
        startActivity(intent);
        // Optionally, finish this activity if you want to prevent the user from returning
        finish();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Timer Channel";
            String description = "Channel for timer notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Help));
        builder.setMessage(getString(R.string.timer_iinstructions));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void startTimer() {
        String time = editTextTime.getText().toString();
        String purpose = editTextPurpose.getText().toString();

        if (TextUtils.isEmpty(time)) {
            editTextTime.setError(getString(R.string.enter_time));
            return;
        }

        if (TextUtils.isEmpty(purpose)) {
            editTextPurpose.setError(getString(R.string.enter_purpose));
            return;
        }

        String[] timeArray = time.split(":");
        if (timeArray.length != 3) {
            editTextTime.setError(getString(R.string.enter_time));
            return;
        }

        int hours = Integer.parseInt(timeArray[0]);
        int minutes = Integer.parseInt(timeArray[1]);
        int seconds = Integer.parseInt(timeArray[2]);
        timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;
        initialTimeInMillis = timeLeftInMillis + 1000;

        if (!isTimerRunning) {
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);

                    timeLeftInMillis = millisUntilFinished;
                    updateTimer();
                    updateNotification();
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;
                    playAlarmSound();
                    vibrateDevice();
                    logTimerData();
                    showTimerFinishedNotification();
                }
            }.start();
            isTimerRunning = true;
            isPaused = false;
            startNotification();
        }
    }

    private void pauseTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isPaused = true;
            isTimerRunning = false;
            cancelNotification();
        }
    }

    private void stopTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            isPaused = false;
            logTimerData();
            cancelNotification();
        }
    }

    private void updateTimer() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        editTextTime.setText(timeLeftFormatted);
    }

    private void startNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.timer_running))
                .setContentText(getString(R.string.timer_running2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void updateNotification() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.timer_running))
                .setContentText(getString(R.string.time_left) + timeLeftFormatted)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void cancelNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1);
    }

    private void showTimerFinishedNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.timer_finished))
                .setContentText(getString(R.string.timer_finished2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(2, builder.build());
    }

    private void logTimerData() {
        String purpose = editTextPurpose.getText().toString();
        long durationInMillis = initialTimeInMillis - timeLeftInMillis;
        String durationFormatted = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(durationInMillis),
                TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60);

        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        boolean isInserted = databaseHelper.insertTimerData(purpose, durationFormatted, endTime);

        if (!isInserted) {
            Toast.makeText(this, getString(R.string.unsuccessful_log), Toast.LENGTH_SHORT).show();
        }
    }

    private void playAlarmSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.start();
    }

    private void vibrateDevice() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }
}