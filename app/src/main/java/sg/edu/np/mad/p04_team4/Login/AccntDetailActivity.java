package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.R;

public class AccntDetailActivity extends AppCompatActivity {

    private static final String TAG = "AccntDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.accnt_detail);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated");
            // Handle unauthenticated user
            return;
        }

        // Display name
        TextView NameTextView = findViewById(R.id.name);
        NameTextView.setText(currentUser.getDisplayName());

        // Display phone number
        TextView PhoneTextView = findViewById(R.id.number);
        PhoneTextView.setText(currentUser.getPhoneNumber());

        // Display email
        TextView EmailTextView = findViewById(R.id.emailaddr);
        EmailTextView.setText(currentUser.getEmail());

        // Start AccountActivity
        ImageButton account = findViewById(R.id.backButton);
        account.setOnClickListener(v -> {
            Intent accountIntent = new Intent(AccntDetailActivity.this, AccountActivity.class);
            startActivity(accountIntent);
        });

    }
}
