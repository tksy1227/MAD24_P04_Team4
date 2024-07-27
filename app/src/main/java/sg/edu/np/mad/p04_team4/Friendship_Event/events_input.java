package sg.edu.np.mad.p04_team4.Friendship_Event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.material.textfield.TextInputEditText;

import sg.edu.np.mad.p04_team4.R;

public class events_input extends AppCompatActivity {

    private DatabaseReference userEventsRef;
    private String userId;
    private TextInputEditText inputText;
    private Button enter;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputText = findViewById(R.id.input);
        enter = findViewById(R.id.button);
        back = findViewById(R.id.backButton);

        back.setOnClickListener(v -> {
            Intent set_event = new Intent(events_input.this, Friendship_Events.class);
            startActivity(set_event);
        });

        Intent set_events = getIntent();
        userId = set_events.getStringExtra("userid");
        String actionPerformed = set_events.getStringExtra("ACTION_PERFORMED");

        userEventsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("events");

        enter.setOnClickListener(v -> {
            String text2 = inputText.getText().toString();
            Log.d("DB", getString(R.string.case_db) + actionPerformed);

            switch (actionPerformed) {
                case "CHALLENGES":
                    userEventsRef.child("challange").setValue(text2);
                    userEventsRef.child("challange_e").setValue(false);
                    break;
                case "MILESTONES":
                    userEventsRef.child("milestones").setValue(text2);
                    userEventsRef.child("milestone_e").setValue(false);
                    break;
                case "GOALS":
                    userEventsRef.child("goals").setValue(text2);
                    userEventsRef.child("goals_e").setValue(false);
                    break;
                default:
                    Log.e("DB", getString(R.string.invalid_action_performed) + actionPerformed);
                    break;
            }

            Log.d("DB", "text_raw: " + text2);

            Intent challenge_made = new Intent(events_input.this, Friendship_Events.class);
            challenge_made.putExtra("userid", userId);
            startActivity(challenge_made);
        });
    }
}
