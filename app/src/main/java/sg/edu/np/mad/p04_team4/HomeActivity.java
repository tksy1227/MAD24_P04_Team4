package sg.edu.np.mad.p04_team4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        events_dbhelper dbHelper = new events_dbhelper(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set Click Listener for the "Message your friends!" image
        ImageView messageFriendsImage = findViewById(R.id.imageViewdanial);
        messageFriendsImage.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatHomeActivity.class);
            startActivity(intent);
        });

        // Set Click Listener for the "Stopwatch/Timer" image
        ImageView stopwatchTimerImage = findViewById(R.id.imageViewchloe);
        stopwatchTimerImage.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Stopwatch_Timer.class);
            startActivity(intent);
        });

        // Set Click Listener for the "Challenge yourself!" image
        ImageView challangeYourselfImage = findViewById(R.id.imageViewjacob);
        challangeYourselfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.insertEvent(
                        1,                         // userId
                        "Some challenges",        // challenges
                        "Some milestones",        // milestones
                        "Some goals",             // goals
                        1,                         // cEmpty (challenge empty status)
                        1,                         // mEmpty (milestone empty status)
                        1                          // gEmpty (goal empty status)
                );
                int userid = 1;
                User_events userEvents = dbHelper.getUserEvent(userid);
                Intent intent = new Intent(HomeActivity.this, Friendship_Events.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        // Set Click Listener for the "To-Do List" image
        ImageView todoListImage = findViewById(R.id.imageViewshida);
        todoListImage.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity_TodoList.class);
            startActivity(intent);
        });
    }
}
