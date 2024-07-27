package sg.edu.np.mad.p04_team4.Feedback;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class FeedbackActivity extends AppCompatActivity {

    private EditText editTextFeedback;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://friendscape-5e36e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDatabase = database.getReference("feedback");

        editTextFeedback = findViewById(R.id.editTextFeedback);
        Button buttonSendFeedback = findViewById(R.id.buttonSendFeedback);

        Button helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());

        buttonSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = editTextFeedback.getText().toString().trim();
                if (!feedback.isEmpty()) {
                    sendFeedback(feedback);
                } else {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.enter_ffeedback), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // navigate back to the homepage
        // Handle custom back button click
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the HomeActivity
                Intent intent = new Intent(FeedbackActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
// buttonSendFeedback
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.feedback_sent))
                .setMessage(getString(R.string.successful_feedback))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void sendFeedback(String feedback) {
        String feedbackId = mDatabase.push().getKey();
        Feedback feedbackObj = new Feedback(feedbackId, feedback, System.currentTimeMillis());

        mDatabase.child(feedbackId).setValue(feedbackObj)
                .addOnSuccessListener(aVoid -> {
                    showConfirmationDialog();
                    editTextFeedback.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.unsuccessful_feedback), Toast.LENGTH_SHORT).show();
                });
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Help));
        builder.setMessage(getString(R.string.feedback_instructions));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}