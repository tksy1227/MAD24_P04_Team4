package sg.edu.np.mad.p04_team4.Timer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sg.edu.np.mad.p04_team4.R;

public class TimerWidgetProvider2x2 extends AppWidgetProvider {
    private static final String CHANNEL_ID = Stopwatch_Timer.CHANNEL_ID;
    private static CountDownTimer countDownTimer;
    private static long timeRemaining = 0;
    private static boolean isRunning = false;
    private static long initialTime = 0;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private DatabaseHelper databaseHelper;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget_small);

            // Set up the intent to start the timer
            Intent startIntent = new Intent(context, TimerWidgetProvider.class);
            startIntent.setAction("START_TIMER");
            PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_start_button, startPendingIntent);

            // Set up the intent to pause the timer
            Intent pauseIntent = new Intent(context, TimerWidgetProvider.class);
            pauseIntent.setAction("PAUSE_TIMER");
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_pause_button, pausePendingIntent);

            // Set up the intent to stop the timer
            Intent stopIntent = new Intent(context, TimerWidgetProvider.class);
            stopIntent.setAction("STOP_TIMER");
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_stop_button, stopPendingIntent);

            // Set up the intent to set 10 seconds
            Intent tenSecIntent = new Intent(context, TimerWidgetProvider.class);
            tenSecIntent.setAction("SET_10_SEC");
            PendingIntent tenSecPendingIntent = PendingIntent.getBroadcast(context, 0, tenSecIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_ten_sec_button, tenSecPendingIntent);

            // Set up the intent to set 1 minute
            Intent oneMinIntent = new Intent(context, TimerWidgetProvider.class);
            oneMinIntent.setAction("SET_1_MIN");
            PendingIntent oneMinPendingIntent = PendingIntent.getBroadcast(context, 0, oneMinIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_one_min_button, oneMinPendingIntent);

            // Set up the intent to set 3 minutes
            Intent threeMinIntent = new Intent(context, TimerWidgetProvider.class);
            threeMinIntent.setAction("SET_3_MIN");
            PendingIntent threeMinPendingIntent = PendingIntent.getBroadcast(context, 0, threeMinIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_three_min_button, threeMinPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Initialize the database helper
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget_small);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, TimerWidgetProvider.class);

        switch (intent.getAction()) {
            case "START_TIMER":
                if (!isRunning && timeRemaining > 0) {
                    startTimer(context, views, appWidgetManager, thisWidget);
                } else if (timeRemaining == 0) {
                    Toast.makeText(context, "Set the timer first!", Toast.LENGTH_SHORT).show();
                }
                break;
            case "PAUSE_TIMER":
                if (isRunning) {
                    pauseTimer();
                    isRunning = false;
                }
                break;
            case "STOP_TIMER":
                stopTimer();
                isRunning = false;
                timeRemaining = 0;
                views.setTextViewText(R.id.widget_timer_text, "00:00:00");
                appWidgetManager.updateAppWidget(thisWidget, views);
                break;
            case "SET_10_SEC":
                timeRemaining = 10000;
                initialTime = timeRemaining; // Update the initial time
                views.setTextViewText(R.id.widget_timer_text, "00:00:10");
                appWidgetManager.updateAppWidget(thisWidget, views);
                break;
            case "SET_1_MIN":
                timeRemaining = 60000;
                initialTime = timeRemaining; // Update the initial time
                views.setTextViewText(R.id.widget_timer_text, "00:01:00");
                appWidgetManager.updateAppWidget(thisWidget, views);
                break;
            case "SET_3_MIN":
                timeRemaining = 180000;
                initialTime = timeRemaining; // Update the initial time
                views.setTextViewText(R.id.widget_timer_text, "00:03:00");
                appWidgetManager.updateAppWidget(thisWidget, views);
                break;
        }
    }

    private void startTimer(final Context context, final RemoteViews views, final AppWidgetManager appWidgetManager, final ComponentName thisWidget) {
        isRunning = true;
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                views.setTextViewText(R.id.widget_timer_text, timeString);
                appWidgetManager.updateAppWidget(thisWidget, views);

                updateNotification(context, timeString); // Add this line to update the notification
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeRemaining = 0;
                // Play sound effect
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound); // Replace with your sound file
                mediaPlayer.start();

                // Insert the timer record into the database
                long durationMillis = initialTime; // Use the initial time set when the timer started
                String duration = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(durationMillis),
                        TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60);
                insertTimerRecord(context, duration, "WidgetTimer");

                // Vibrate the device
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(500);
                    }
                }
                views.setTextViewText(R.id.widget_timer_text, "00:00:00");
                appWidgetManager.updateAppWidget(thisWidget, views);

                showTimerFinishedNotification(context); // Add this line to show the finished notification
            }
        }.start();
        startNotification(context); // Add this line to start the notification
    }

    private void insertTimerRecord(Context context, String duration, String purpose) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }

        // Get the current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());

        databaseHelper.insertTimerData(purpose, duration,date);
    }

    private void startNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer) // Add your own timer icon here
                .setContentTitle("Timer Running")
                .setContentText("Your timer is running.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions,
            // and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void updateNotification(Context context, String timeLeftFormatted) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer) // Add your own timer icon here
                .setContentTitle("Timer Running")
                .setContentText("Time left: " + timeLeftFormatted)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions,
            // and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void showTimerFinishedNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer) // Add your own timer icon here
                .setContentTitle("Timer Finished")
                .setContentText("Your timer has finished.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions,
            // and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(2, builder.build());
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}

