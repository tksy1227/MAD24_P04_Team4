package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import sg.edu.np.mad.p04_team4.Calender.MainCalender;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.Feedback.FeedbackActivity;
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
            nameTextView.setText(getString(R.string.name2) + currentUser.getDisplayName());
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

        // Set click listener for Log Out button
        findViewById(R.id.LogOut).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for Language Toggle using RelativeLayout
        RelativeLayout languageRL = findViewById(R.id.language);
        languageRL.setOnClickListener(v -> toggleLanguage());

        // Set click listener for Language Toggle using Button
        ImageButton languageButton = findViewById(R.id.languageButton);
        languageButton.setOnClickListener(v -> toggleLanguage());

        // Set click listener for feedback
        setupClickListener(R.id.problemButton, FeedbackActivity.class);
    }

    private void setupClickListener(int viewId, Class<?> activityClass) {
        findViewById(viewId).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, activityClass);
            startActivity(intent);
        });
    }

    // Method to toggle the language of the app
    private void toggleLanguage() {
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        String currentLanguage = prefs.getString("language_code", "en"); // Default to English

        String newLanguage = currentLanguage.equals("en") ? "cn" : "en";
        changeLanguage(newLanguage);
        recreate(); // Recreate activity to apply the new locale
    }

    // Method to change the language of the app
    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Persist the language preference
        SharedPreferences prefs = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language_code", languageCode);
        editor.apply();
    }
}
