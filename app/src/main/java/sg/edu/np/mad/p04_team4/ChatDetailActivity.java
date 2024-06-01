package sg.edu.np.mad.p04_team4;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ChatDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Retrieve any data passed with the Intent
        String chatName = getIntent().getStringExtra("chat_name");
        // Use the chatName to display details in this activity
    }
}
