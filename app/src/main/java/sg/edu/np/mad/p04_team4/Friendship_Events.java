package sg.edu.np.mad.p04_team4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.Html;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

public class Friendship_Events extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friendship_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        ImageButton b4 = findViewById(R.id.backButton);
        b4.setOnClickListener(v -> {
            Intent set_event = new Intent(Friendship_Events.this, HomeActivity.class);
            startActivity(set_event);
        });

        Log.d("Test", "Reached fs");

        Intent friendship_events = getIntent();
        events_dbhelper dbHelper = new events_dbhelper(this);
        int userid = friendship_events.getIntExtra("userid", 1234);

        User_events userEvents = dbHelper.getUserEvent(userid);
        if (userEvents != null) {
            boolean challengeEmpty = userEvents.isChallange_e();
            boolean milestoneEmpty = userEvents.isMilestone_e();
            boolean goalsEmpty = userEvents.isGoals_e();

            if (challengeEmpty) {
                String buttonText = "<b>No challenge set yet!</b><br><br>Click here to set it for yourself or friends";
                b1.setText(Html.fromHtml(buttonText));
                b1.setOnClickListener(v -> {
                    Intent set_event = new Intent(Friendship_Events.this, events_input.class);
                    set_event.putExtra("userid", userid);
                    set_event.putExtra("ACTION_PERFORMED", "CHALLENGES");
                    startActivity(set_event);
                });
            } else {
                int color = ContextCompat.getColor(this, R.color.teal_700);
                b1.setBackgroundColor(color);
                String challengeInfo = "<b>Challenge set!</b><br><br>" + userEvents.getChallange();
                b1.setText(Html.fromHtml(challengeInfo));
            }

            if (milestoneEmpty) {
                String buttonText = "<b>No milestone set yet!</b><br><br>Click here to set it for yourself or friends";
                b2.setText(Html.fromHtml(buttonText));
                b2.setOnClickListener(v -> {
                    Intent set_event = new Intent(Friendship_Events.this, events_input.class);
                    set_event.putExtra("userid", userid);
                    set_event.putExtra("ACTION_PERFORMED", "MILESTONES");
                    startActivity(set_event);
                });
            } else {
                int color = ContextCompat.getColor(this, R.color.teal_700);
                b2.setBackgroundColor(color);
                String milestoneInfo = "<b>Milestone set!</b><br><br>" + userEvents.getMilestone();
                b2.setText(Html.fromHtml(milestoneInfo));
            }

            if (goalsEmpty) {
                String buttonText = "<b>No goals set yet!</b><br><br>Click here to set it for yourself or friends";
                b3.setText(Html.fromHtml(buttonText));
                b3.setOnClickListener(v -> {
                    Intent set_event = new Intent(Friendship_Events.this, events_input.class);
                    set_event.putExtra("userid", userid);
                    set_event.putExtra("ACTION_PERFORMED", "GOALS");
                    startActivity(set_event);
                });
            } else {
                int color = ContextCompat.getColor(this, R.color.teal_700);
                b3.setBackgroundColor(color);
                String goalsInfo = "<b>Goal set!</b><br><br>" + userEvents.getGoals();
                b3.setText(Html.fromHtml(goalsInfo));
            }
        } else {
            Log.e("DB", "User_events object is null for userId: " + userid);
        }
    }
}
