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
    }
}

