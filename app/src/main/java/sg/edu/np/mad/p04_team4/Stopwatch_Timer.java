package sg.edu.np.mad.p04_team4;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class Stopwatch_Timer extends AppCompatActivity {

    private EditText editTextTime;
    private EditText editTextPurpose;
    private Button buttonStart;
    private Button buttonTenSec, buttonOneMin, buttonThreeMin;

    private String purposeText; // Variable to store purpose text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch_timer);

        editTextTime = findViewById(R.id.timerEditText);
        editTextPurpose = findViewById(R.id.purposeTextView);
        buttonStart = findViewById(R.id.startButton);
        buttonTenSec = findViewById(R.id.tenSecButton);
        buttonOneMin = findViewById(R.id.oneMinButton);
        buttonThreeMin = findViewById(R.id.threeMinButton);

        buttonTenSec.setOnClickListener(v -> editTextTime.setText("00:00:10"));
        buttonOneMin.setOnClickListener(v -> editTextTime.setText("00:01:00"));
        buttonThreeMin.setOnClickListener(v -> editTextTime.setText("00:03:00"));

        buttonStart.setOnClickListener(v -> {
            String timeInput = editTextTime.getText().toString();
            purposeText = editTextPurpose.getText().toString().trim(); // Store the purpose text

            if (TextUtils.isEmpty(timeInput)) {
                Toast.makeText(Stopwatch_Timer.this, "Please enter a valid time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(purposeText)) {
                purposeText = "Unknown"; // Set default purpose if not provided
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

                long totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;

                buttonStart.setEnabled(false); // Disable the button

                new CountDownTimer(totalMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
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
                        buttonStart.setEnabled(true); // Re-enable the button

                        /* Pass the purpose text to SecondActivity
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        intent.putExtra("PURPOSE_TEXT", purposeText);
                        startActivity(intent); */
                    }
                }.start();
            } catch (NumberFormatException e) {
                Toast.makeText(Stopwatch_Timer.this, "Invalid time format", Toast.LENGTH_SHORT).show();
            }
        });
    }
}