package sg.edu.np.mad.p04_team4.Timer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class Stopwatch_Timer extends AppCompatActivity {
    private EditText editTextTime;
    private EditText editTextPurpose;
    private Button buttonStart;
    private Button buttonTenSec, buttonOneMin, buttonThreeMin;
    private Button buttonStop, buttonPause;
    private Button buttonTimerHistory;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isTimerRunning;
    private boolean isPaused;

    private String purposeText;
    private String startTime;

    private FirebaseAuth mAuth;
    private DatabaseReference timerLogRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch_timer);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            timerLogRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("timer_logs");
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        buttonStart.setOnClickListener(v -> {
            if (isPaused) {
                resumeTimer();
            } else {
                startTimer();
            }
        });
        buttonStop.setOnClickListener(v -> stopTimer());
        buttonPause.setOnClickListener(v -> pauseTimer());

        buttonTimerHistory.setOnClickListener(v -> {
            Intent intent = new Intent(Stopwatch_Timer.this, TimerLogActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Stopwatch_Timer.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });
    }

    private void startTimer() {
        String timeInput = editTextTime.getText().toString();
        purposeText = editTextPurpose.getText().toString().trim();

        if (timeInput.equals("00:00:00")) {
            Toast.makeText(Stopwatch_Timer.this, "Please enter a valid time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(purposeText)) {
            purposeText = "Unknown";
        }

        String[] timeParts = timeInput.split(":");
        if (timeParts.length != 3) {
            Toast.makeText(Stopwatch_Timer.this, "Please enter time in HH:MM:SS format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;

            startTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            buttonStart.setEnabled(false);
            buttonPause.setEnabled(true);
            buttonStop.setEnabled(true);

            startCountDownTimer();
        } catch (NumberFormatException e) {
            Toast.makeText(Stopwatch_Timer.this, "Invalid time format", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;

                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;

                editTextTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                editTextTime.setText("00:00:00");
                Toast.makeText(Stopwatch_Timer.this, "Timer Finished", Toast.LENGTH_SHORT).show();

                MediaPlayer mediaPlayer = MediaPlayer.create(Stopwatch_Timer.this, R.raw.alarm_sound);
                mediaPlayer.start();

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(1000);
                }

                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                Time time = new Time(startTime, purposeText, currentTime);
                timerLogRef.push().setValue(time);

                resetTimer();
            }

        }.start();

        isTimerRunning = true;
        isPaused = false;
    }

    private void pauseTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isPaused = true;
            isTimerRunning = false;
            buttonPause.setEnabled(false);
            buttonStart.setEnabled(true);
        }
    }

    private void resumeTimer() {
        buttonStart.setEnabled(false);
        buttonPause.setEnabled(true);
        startCountDownTimer();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        resetTimer();
    }

    private void resetTimer() {
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        isTimerRunning = false;
        isPaused = false;
        editTextTime.setText("00:00:00");
    }
}
