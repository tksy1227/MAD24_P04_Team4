package sg.edu.np.mad.p04_team4.ScreenTime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.p04_team4.R;

public class ScreenTimeService extends Service {
    private final IBinder binder = new LocalBinder();
    private Map<String, Long> featureStartTimes = new HashMap<>();
    private Map<String, Long> featureDurations = new HashMap<>();
    private Handler handler = new Handler();
    private Runnable updateTask;
    private DatabaseReference databaseReference;

    // Binder class to return the service instance to clients
    public class LocalBinder extends Binder {
        public ScreenTimeService getService() {
            return ScreenTimeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("screenTime");

        // Create the notification for the foreground service
        Notification notification = createNotification();

        // Start the service in the foreground
        startForeground(1, notification);

        // Start tracking screen time
        startTracking();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Create a notification for the foreground service
    private Notification createNotification() {
        String channelId = "screen_time_channel";
        createNotificationChannel(channelId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.screen_time_tracking))
                .setContentText(getString(R.string.tracking_screentime))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    // Create a notification channel for devices running Android O and above
    private void createNotificationChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.screen_time_tracking);
            String description = getString(R.string.channel_screen_time);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Start tracking screen time
    private void startTracking() {
        updateTask = new Runnable() {
            @Override
            public void run() {
                updateScreenTime();
                handler.postDelayed(this, 60000); // Update every minute
            }
        };
        handler.post(updateTask);
    }

    // Update screen time for each tracked feature
    private void updateScreenTime() {
        for (String feature : featureStartTimes.keySet()) {
            long startTime = featureStartTimes.get(feature);
            long duration = System.currentTimeMillis() - startTime;
            long currentDuration = featureDurations.containsKey(feature) ? featureDurations.get(feature) : 0L;
            featureDurations.put(feature, currentDuration + duration);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Ensure the service is restarted if it gets terminated
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateTask); // Remove update task callbacks
        super.onDestroy();
    }

    // Start timer for a specific feature
    public void startFeatureTimer(String featureName) {
        long startTime = System.currentTimeMillis();
        featureStartTimes.put(featureName, startTime);
        Log.d("ScreenTimeService", "Starting timer for feature: " + featureName + " at " + startTime);
    }

    // Stop timer for a specific feature and save data to Firebase
    public void stopFeatureTimer(String featureName) {
        Long startTime = featureStartTimes.get(featureName);
        if (startTime != null) {
            long currentTime = System.currentTimeMillis();
            long duration = currentTime - startTime;
            long currentDuration = featureDurations.containsKey(featureName) ? featureDurations.get(featureName) : 0L;
            featureDurations.put(featureName, currentDuration + duration);
            featureStartTimes.remove(featureName);

            Log.d("ScreenTimeService", "Stopping timer for feature: " + featureName + " at " + currentTime + ", duration: " + duration + ", total duration: " + featureDurations.get(featureName));

            // Save data to Firebase
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DatabaseReference userRef = databaseReference.child(userId).child(date).child(featureName);
            userRef.setValue(featureDurations.get(featureName));
        } else {
            Log.w("ScreenTimeService", "No start time found for feature: " + featureName);
        }
    }

    // Return the current feature durations
    public Map<String, Long> getFeatureDurations() {
        return featureDurations;
    }
}
