package sg.edu.np.mad.p04_team4.Friendship_Event;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.Html;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;


public class Friendship_Events extends AppCompatActivity {

    private DatabaseReference userEventsRef;
    private FirebaseAuth mAuth;
    private Button b1, b2, b3;
    private ImageButton b4;
    private String userId; // Declare userId here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friendship_events);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.backButton);

        b4.setOnClickListener(v -> {
            Intent set_event = new Intent(Friendship_Events.this, HomeActivity.class);
            startActivity(set_event);
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Initialize userId here
            userEventsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("events");
        } else {
            Log.e("DB", getString(R.string.user_not_authenticated));
            finish();
            return;
        }

        loadUserEvent();

        Button helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Help));
        builder.setMessage(getString(R.string.friendship_event_instructions));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadUserEvent() {
        userEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_events userEvents = snapshot.getValue(User_events.class);
                if (userEvents == null) {
                    // Initialize a new User_events object with default values
                    userEvents = new User_events(0L, "", "", "", true, true, true);
                    // Save this new object to Firebase
                    userEventsRef.setValue(userEvents);
                }
                updateUI(userEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB", getString(R.string.failed_load_user_events), error.toException());
            }
        });
    }

    private void updateUI(User_events userEvents) {
        Log.d("UI", getString(R.string.ui_method));

        if (userEvents == null) {
            Log.e("UI", getString(R.string.ui_null));
            return;
        }

        boolean challengeEmpty = userEvents.isChallange_e();
        boolean milestoneEmpty = userEvents.isMilestone_e();
        boolean goalsEmpty = userEvents.isGoals_e();

        Log.d("UI", getString(R.string.event_states1) + challengeEmpty + getString(R.string.event_states2) + milestoneEmpty + getString(R.string.event_states3) + goalsEmpty);

        if (b1 == null || b2 == null || b3 == null) {
            Log.e("UI", getString(R.string.one_more_buttons_null));
            return;
        }

        // Challenge button
        try {
            if (challengeEmpty) {
                String buttonText = "<b>"+getString(R.string.no_challenge)+"</b><br><br>"+getString(R.string.click_here_to);
                b1.setText(Html.fromHtml(buttonText));
                b1.setOnClickListener(v -> {
                    Intent set_event = new Intent(Friendship_Events.this, events_input.class);
                    set_event.putExtra("userid", userId);
                    set_event.putExtra("ACTION_PERFORMED", "CHALLENGES");
                    startActivity(set_event);
                });
            } else {
                int color = ContextCompat.getColor(this, R.color.teal_700);
                b1.setBackgroundColor(color);
                String challengeInfo = "<b>"+getString(R.string.challenge_set)+"</b><br><br>" + userEvents.getChallange();
                b1.setText(Html.fromHtml(challengeInfo));
            }
            Log.d("UI", getString(R.string.challenge_button_updated));
        } catch (Exception e) {
            Log.e("UI", getString(R.string.challenge_button_error), e);
        }

        // Milestone button
        try {
            if (milestoneEmpty) {
                String buttonText = "<b>"+getString(R.string.no_milestone)+"</b><br><br>"+getString(R.string.click_here_to);
                b2.setText(Html.fromHtml(buttonText));
                b2.setOnClickListener(v -> {
                    Intent set_event = new Intent(Friendship_Events.this, events_input.class);
                    set_event.putExtra("userid", userId);
                    set_event.putExtra("ACTION_PERFORMED", "MILESTONES");
                    startActivity(set_event);
                });
            } else {
                int color = ContextCompat.getColor(this, R.color.teal_700);
                b2.setBackgroundColor(color);
                String milestoneInfo = "<b>"+getString(R.string.milestone_set)+"</b><br><br>" + userEvents.getMilestones();
                b2.setText(Html.fromHtml(milestoneInfo));
            }
            Log.d("UI", getString(R.string.milestone_button_updated));
        } catch (Exception e) {
            Log.e("UI", getString(R.string.milestone_button_error), e);
        }

        // Goals button
        try {
            if (goalsEmpty) {
                String buttonText = "<b>"+getString(R.string.no_goals)+"</b><br><br>"+getString(R.string.click_here_to);
                b3.setText(Html.fromHtml(buttonText));
                b3.setOnClickListener(v -> {
                    Intent set_event = new Intent(Friendship_Events.this, events_input.class);
                    set_event.putExtra("userid", userId);
                    set_event.putExtra("ACTION_PERFORMED", "GOALS");
                    startActivity(set_event);
                });
            } else {
                int color = ContextCompat.getColor(this, R.color.teal_700);
                b3.setBackgroundColor(color);
                String goalsInfo = "<b>"+getString(R.string.goal_set)+"</b><br><br>" + userEvents.getGoals();
                b3.setText(Html.fromHtml(goalsInfo));
            }
            Log.d("UI", getString(R.string.goals_button_updated));
        } catch (Exception e) {
            Log.e("UI", getString(R.string.goals_button_error), e);
        }

        Log.d("UI", getString(R.string.finished_updateui));
    }
}
