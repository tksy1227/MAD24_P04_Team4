package sg.edu.np.mad.p04_team4.Feedback;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.p04_team4.R;

public class FeedbackActivity extends AppCompatActivity {

    private EditText editTextFeedback;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://friendscape-24f1f-default-rtdb.asia-southeast1.firebasedatabase.app");

        mDatabase = FirebaseDatabase.getInstance().getReference("feedback");

        editTextFeedback = findViewById(R.id.editTextFeedback);
        Button buttonSendFeedback = findViewById(R.id.buttonSendFeedback);

        buttonSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = editTextFeedback.getText().toString().trim();
                if (!feedback.isEmpty()) {
                    sendFeedback(feedback);
                } else {
                    Toast.makeText(FeedbackActivity.this, "Please enter feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Feedback Sent")
                .setMessage("Your feedback has been sent successfully.")
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
                    Toast.makeText(FeedbackActivity.this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                });
    }
}
