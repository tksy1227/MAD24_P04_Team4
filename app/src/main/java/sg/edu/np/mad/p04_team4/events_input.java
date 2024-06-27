package sg.edu.np.mad.p04_team4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;

public class events_input extends AppCompatActivity {

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

        TextInputEditText Text = findViewById(R.id.input);
        Button enter = findViewById(R.id.button);
        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> {
            Intent set_event = new Intent(events_input.this, Friendship_Events.class);
            startActivity(set_event);
        });

        Intent set_events = getIntent();
        int userid = set_events.getIntExtra("userid", 1234);
        String actionPerformed = set_events.getStringExtra("ACTION_PERFORMED");

        events_dbhelper dbHelper = new events_dbhelper(this);
        User_events userEvents = dbHelper.getUserEvent(userid);
        if (userEvents == null) {
            Log.d("DB", "User events data is null for userId: " + userid);
            return; // Early return if userEvents is null
        }

        enter.setOnClickListener(v -> {
            String text2 = Text.getText().toString();
            Log.d("DB", "Case: " + actionPerformed);

            switch (actionPerformed) {
                case "CHALLENGES":
                    dbHelper.updateEventPart(userid, "COLUMN_CHALLENGES", text2);
                    dbHelper.updateEventPart(userid, "COLUMN_C_EMPTY", "0");
                    break;
                case "MILESTONES":
                    dbHelper.updateEventPart(userid, "COLUMN_MILESTONES", text2);
                    dbHelper.updateEventPart(userid, "COLUMN_M_EMPTY", "0");
                    break;
                case "GOALS":
                    dbHelper.updateEventPart(userid, "COLUMN_GOALS", text2);
                    dbHelper.updateEventPart(userid, "COLUMN_G_EMPTY", "0");
                    break;
                default:
                    Log.e("DB", "Invalid action performed: " + actionPerformed);
                    break;
            }

            Log.d("DB", "text_raw: " + text2);

            Intent challenge_made = new Intent(events_input.this, Friendship_Events.class);
            challenge_made.putExtra("userid", userid);
            startActivity(challenge_made);
        });
    }
}
