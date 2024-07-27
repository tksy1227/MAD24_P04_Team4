package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeSelectionActivity;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Display user information
            TextView nameTextView = findViewById(R.id.Name);
            nameTextView.setText("Name: " + currentUser.getDisplayName());
        } else {
            Log.e(TAG, "No user is currently signed in.");
            // Handle the case when there is no user signed in
        }

        // Set up the back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set click listener for Change Password
        RelativeLayout changePasswordRL = findViewById(R.id.ChangePassword);
        changePasswordRL.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ForgetUserActivity.class);
            startActivity(intent);
        });

        // Set click listener for Account Details
        RelativeLayout accountDetailsRL = findViewById(R.id.AccountDetails);
        accountDetailsRL.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, AccntDetailActivity.class);
            startActivity(intent);
        });

        // Set click listener for Layout
        RelativeLayout layoutRL = findViewById(R.id.layout);
        layoutRL.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ThemeSelectionActivity.class);
            startActivity(intent);
        });

        // Set click listener for Log Out button
        findViewById(R.id.LogOut).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
