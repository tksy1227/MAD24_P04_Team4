package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import sg.edu.np.mad.p04_team4.R;

public class ThemeSelectionActivity extends AppCompatActivity {

    private static final String TAG = "ThemeSelectionActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private Set<String> purchasedThemes = new HashSet<>();
    private Button btnTheme1, btnTheme2, btnTheme3, btnRemoveTheme;
    private ImageView themeImage1, themeImage2, themeImage3, lockIcon1, lockIcon2, lockIcon3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable the title
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            fetchPurchasedThemes();
        } else {
            Log.e(TAG, "User not authenticated");
        }

        // Set Click Listener for the back arrow
        ImageButton backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        // Initialize theme buttons and images
        btnTheme1 = findViewById(R.id.btnTheme1);
        btnTheme2 = findViewById(R.id.btnTheme2);
        btnTheme3 = findViewById(R.id.btnTheme3);
        btnRemoveTheme = findViewById(R.id.btnRemoveTheme); // Initialize the remove theme button

        themeImage1 = findViewById(R.id.themeImage1);
        themeImage2 = findViewById(R.id.themeImage2);
        themeImage3 = findViewById(R.id.themeImage3);

        lockIcon1 = findViewById(R.id.lockIcon1);
        lockIcon2 = findViewById(R.id.lockIcon2);
        lockIcon3 = findViewById(R.id.lockIcon3);

        // Set Click Listeners for the theme buttons
        btnTheme1.setOnClickListener(v -> handleThemeClick("Fluid Harmony", R.drawable.theme_1));
        btnTheme2.setOnClickListener(v -> handleThemeClick("Blue Blossom", R.drawable.theme_2));
        btnTheme3.setOnClickListener(v -> handleThemeClick("Playful Safari", R.drawable.theme_3));
        btnRemoveTheme.setOnClickListener(v -> removeTheme()); // Set click listener for the remove theme button
    }

    private void fetchPurchasedThemes() {
        DatabaseReference userThemesRef = mDatabase.child("users").child(userId).child("purchasedThemes");
        userThemesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                purchasedThemes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    purchasedThemes.add(snapshot.getKey());
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch purchased themes", databaseError.toException());
            }
        });
    }

    private void updateUI() {
        updateButtonState(btnTheme1, themeImage1, lockIcon1, "Fluid Harmony");
        updateButtonState(btnTheme2, themeImage2, lockIcon2, "Blue Blossom");
        updateButtonState(btnTheme3, themeImage3, lockIcon3, "Playful Safari");
    }

    private void updateButtonState(Button button, ImageView themeImage, ImageView lockIcon, String themeName) {
        if (purchasedThemes.contains(themeName)) {
            button.setEnabled(true);
            button.setText(themeName);
            themeImage.setVisibility(ImageView.VISIBLE);
            lockIcon.setVisibility(ImageView.GONE);
        } else {
            button.setEnabled(true); // Enable button to allow purchasing
            button.setText(themeName);
            themeImage.setVisibility(ImageView.VISIBLE);
            lockIcon.setVisibility(ImageView.VISIBLE);
            themeImage.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY); // Gray out image
        }
    }

    private void handleThemeClick(String themeName, int themeDrawableId) {
        if (purchasedThemes.contains(themeName)) {
            showApplyThemeDialog(themeName);
        } else {
            showBuyThemeDialog(themeName, themeDrawableId);
        }
    }

    private void showBuyThemeDialog(String themeName, int themeDrawableId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_theme_preview, null);
        builder.setView(dialogView);

        TextView themeTitle = dialogView.findViewById(R.id.themeTitle);
        ImageView themePreviewImage = dialogView.findViewById(R.id.themePreviewImage);
        Button buyButton = dialogView.findViewById(R.id.buyButton);
        ImageView closeButton = dialogView.findViewById(R.id.closeButton);

        themeTitle.setText(themeName);
        themePreviewImage.setImageResource(themeDrawableId);

        AlertDialog alertDialog = builder.create();

        buyButton.setOnClickListener(v -> {
            // Add logic to handle theme purchase here
            // After successful purchase, save the theme as purchased
            purchasedThemes.add(themeName);
            updateUI();
            Toast.makeText(this, getString(R.string.theme_purchased) + themeName, Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
            showApplyThemeDialog(themeName);
        });

        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void showApplyThemeDialog(String themeName) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.apply_theme))
                .setMessage(getString(R.string.apply_theme_confirm))
                .setPositiveButton(getString(R.string.buy_yes), (dialog, which) -> {
                    applyTheme(themeName);
                    sendThemeChangedBroadcast();
                })
                .setNegativeButton(getString(R.string.buy_no), null)
                .show();
    }

    private void applyTheme(String themeName) {
        saveSelectedTheme(themeName);
        Toast.makeText(this, getString(R.string.applied_theme) + themeName, Toast.LENGTH_SHORT).show();
    }

    private void saveSelectedTheme(String themeName) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            editor.putString("selectedTheme_" + userId, themeName);
            editor.apply();
            Log.d(TAG, "Saved selected theme: " + themeName);
        }
    }

    private void sendThemeChangedBroadcast() {
        Intent intent = new Intent("sg.edu.np.mad.p04_team4.THEME_CHANGED");
        sendBroadcast(intent);
    }

    private void removeTheme() {
        saveSelectedTheme("default"); // Save the default theme
        Toast.makeText(this, getString(R.string.theme_removed), Toast.LENGTH_SHORT).show();
        sendThemeChangedBroadcast(); // Notify other components about the theme change
    }
}
