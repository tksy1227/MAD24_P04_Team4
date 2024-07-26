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

public class TimerWidgetProvider extends AppWidgetProvider {
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
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget);

            // Set up the intent to start the timer
            setPendingIntent(context, views, R.id.widget_start_button, "START_TIMER");

            // Set up the intent to pause the timer
            setPendingIntent(context, views, R.id.widget_pause_button, "PAUSE_TIMER");

            // Set up the intent to stop the timer
            setPendingIntent(context, views, R.id.widget_stop_button, "STOP_TIMER");

            // Set up the intent to set 10 seconds
            setPendingIntent(context, views, R.id.widget_ten_sec_button, "SET_10_SEC");

            // Set up the intent to set 1 minute
            setPendingIntent(context, views, R.id.widget_one_min_button, "SET_1_MIN");

            // Set up the intent to set 3 minutes
            setPendingIntent(context, views, R.id.widget_three_min_button, "SET_3_MIN");

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void setPendingIntent(Context context, RemoteViews views, int viewId, String action) {
        Intent intent = new Intent(context, TimerWidgetProvider.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(viewId, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Initialize the database helper
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, TimerWidgetProvider.class);

        switch (intent.getAction()) {
            case "START_TIMER":
                if (!isRunning && timeRemaining > 0) {
                    startTimer(context, views, appWidgetManager, thisWidget);
                } else if (timeRemaining == 0) {
                    Toast.makeText(context, context.getString(R.string.set_timer_first), Toast.LENGTH_SHORT).show();
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
                updateTime(context, views, appWidgetManager, thisWidget, 10000, "00:00:10");
                break;
            case "SET_1_MIN":
                updateTime(context, views, appWidgetManager, thisWidget, 60000, "00:01:00");
                break;
            case "SET_3_MIN":
                updateTime(context, views, appWidgetManager, thisWidget, 180000, "00:03:00");
                break;
        }
    }

    private void updateTime(Context context, RemoteViews views, AppWidgetManager appWidgetManager, ComponentName thisWidget, long timeInMillis, String timeString) {
        timeRemaining = timeInMillis;
        initialTime = timeRemaining; // Update the initial time
        views.setTextViewText(R.id.widget_timer_text, timeString);
        appWidgetManager.updateAppWidget(thisWidget, views);
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
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound); // Replace with your sound file
                mediaPlayer.start();

                long durationMillis = initialTime; // Use the initial time set when the timer started
                String duration = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(durationMillis),
                        TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60);
                insertTimerRecord(context, duration, "WidgetTimer");

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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());

        databaseHelper.insertTimerData(purpose, duration, date);
    }

    private void startNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer) // Add your own timer icon here
                .setContentTitle(context.getString(R.string.timer_running))
                .setContentText(context.getString(R.string.timer_running2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void updateNotification(Context context, String timeLeftFormatted) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer) // Add your own timer icon here
                .setContentTitle(context.getString(R.string.timer_running))
                .setContentText(context.getString(R.string.time_left) + timeLeftFormatted)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void showTimerFinishedNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer) // Add your own timer icon here
                .setContentTitle(context.getString(R.string.timer_finished))
                .setContentText(context.getString(R.string.timer_finished2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        }
        countDownTimer = null;
    }
}


