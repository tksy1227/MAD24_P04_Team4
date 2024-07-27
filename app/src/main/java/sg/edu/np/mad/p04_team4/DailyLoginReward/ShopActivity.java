package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    private TextView coinAmountTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private String chatRoomId = "chat_room_id"; // Replace with actual chat room id or obtain dynamically
    private Set<String> purchasedThemes = new HashSet<>();
    private Set<String> purchasedStickers = new HashSet<>();
    private Button btnSticker1, btnSticker2, btnSticker3, btnSticker4, btnSticker5, btnSticker6;
    private Button btnTheme1, btnTheme2, btnTheme3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page); // Ensure this matches your XML layout file name

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        coinAmountTextView = findViewById(R.id.coinAmount);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            fetchUserCoins();
            fetchPurchasedThemes();
            fetchPurchasedStickers();
        } else {
            Log.e(TAG, "User not authenticated");
        }

        // Set Click Listener for the back arrow
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize sticker buttons
        btnSticker1 = findViewById(R.id.btnSticker1);
        btnSticker2 = findViewById(R.id.btnSticker2);
        btnSticker3 = findViewById(R.id.btnSticker3);
        btnSticker4 = findViewById(R.id.btnSticker4);
        btnSticker5 = findViewById(R.id.btnSticker5);
        btnSticker6 = findViewById(R.id.btnSticker6);

        // Initialize theme buttons
        btnTheme1 = findViewById(R.id.btnTheme1);
        btnTheme2 = findViewById(R.id.btnTheme2);
        btnTheme3 = findViewById(R.id.btnTheme3);

        // Set Click Listeners for the sticker pack buttons
        btnSticker1.setOnClickListener(v -> openStickerPack("Cat Sticker Pack", 50, purchasedStickers.contains("Cat Sticker Pack")));
        btnSticker2.setOnClickListener(v -> openStickerPack("Monkey Sticker Pack", 50, purchasedStickers.contains("Monkey Sticker Pack")));
        btnSticker3.setOnClickListener(v -> openStickerPack("Emoji Sticker Pack", 50, purchasedStickers.contains("Emoji Sticker Pack")));
        btnSticker4.setOnClickListener(v -> openStickerPack("Alpha Wolf Sticker Pack", 50, purchasedStickers.contains("Alpha Wolf Sticker Pack")));
        btnSticker5.setOnClickListener(v -> openStickerPack("Skibidi Toilet Sticker Pack", 50, purchasedStickers.contains("Skibidi Toilet Sticker Pack")));
        btnSticker6.setOnClickListener(v -> openStickerPack("Dead by Daylight Sticker Pack", 70, purchasedStickers.contains("Dead by Daylight Sticker Pack")));

        // Set Click Listeners for the theme buttons
        btnTheme1.setOnClickListener(v -> showThemePreview("Fluid Harmony", R.drawable.theme_1, 80, purchasedThemes.contains("Fluid Harmony")));
        btnTheme2.setOnClickListener(v -> showThemePreview("Blue Blossom", R.drawable.theme_2, 80, purchasedThemes.contains("Blue Blossom")));
        btnTheme3.setOnClickListener(v -> showThemePreview("Playful Safari", R.drawable.theme_3, 100, purchasedThemes.contains("Playful Safari")));
    }

    private void fetchUserCoins() {
        DatabaseReference userCoinsRef = mDatabase.child("users").child(userId).child("friendCoins");
        userCoinsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer coins = dataSnapshot.getValue(Integer.class);
                if (coins != null) {
                    coinAmountTextView.setText(String.valueOf(coins));
                } else {
                    coinAmountTextView.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch coins", databaseError.toException());
            }
        });
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

    private void fetchPurchasedStickers() {
        DatabaseReference userStickersRef = mDatabase.child("users").child(userId).child("purchasedStickers");
        userStickersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                purchasedStickers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    purchasedStickers.add(snapshot.getKey());
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch purchased stickers", databaseError.toException());
            }
        });
    }

    private void updateUI() {
        updateButtonState(btnSticker1, "Cat Sticker Pack", purchasedStickers);
        updateButtonState(btnSticker2, "Monkey Sticker Pack", purchasedStickers);
        updateButtonState(btnSticker3, "Emoji Sticker Pack", purchasedStickers);
        updateButtonState(btnSticker4, "Alpha Wolf Sticker Pack", purchasedStickers);
        updateButtonState(btnSticker5, "Skibidi Toilet Sticker Pack", purchasedStickers);
        updateButtonState(btnSticker6, "Dead by Daylight Sticker Pack", purchasedStickers);

        updateButtonState(btnTheme1, "Fluid Harmony", purchasedThemes);
        updateButtonState(btnTheme2, "Blue Blossom", purchasedThemes);
        updateButtonState(btnTheme3, "Playful Safari", purchasedThemes);
    }

    private void updateButtonState(Button button, String itemName, Set<String> purchasedItems) {
        if (purchasedItems.contains(itemName)) {
            button.setBackgroundColor(Color.GRAY);
        }
    }

    private void openStickerPack(String packName, int packCost, boolean isUserView) {
        StickerPackDialogFragment dialog = new StickerPackDialogFragment(packName, userId, chatRoomId, packCost, isUserView);
        dialog.show(getSupportFragmentManager(), "StickerPackDialogFragment");
    }

    private void showThemePreview(String themeName, int themeImageResId, int themeCost, boolean isPurchased) {
        ThemePreviewDialogFragment dialog = new ThemePreviewDialogFragment(themeName, themeImageResId, themeCost, userId);
        dialog.setPurchaseListener(() -> applyTheme(themeName, themeCost));
        dialog.setPurchased(isPurchased);
        dialog.show(getSupportFragmentManager(), "ThemePreviewDialogFragment");
    }

    public void applyTheme(String themeName, int themeCost) {
        if (purchasedThemes.contains(themeName)) {
            saveSelectedTheme(themeName);
            sendThemeChangedBroadcast();
        } else {
            deductCoins(themeCost, () -> {
                DatabaseReference userThemesRef = mDatabase.child("users").child(userId).child("purchasedThemes");
                userThemesRef.child(themeName).setValue(true);
                purchasedThemes.add(themeName);
                updateUI();
                saveSelectedTheme(themeName);
                sendThemeChangedBroadcast();
            });
        }
    }

    private void deductCoins(int cost, Runnable onSuccess) {
        DatabaseReference userCoinsRef = mDatabase.child("users").child(userId).child("friendCoins");
        userCoinsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentCoins = mutableData.getValue(Integer.class);
                if (currentCoins == null || currentCoins < cost) {
                    return Transaction.abort();
                }
                mutableData.setValue(currentCoins - cost);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Failed to deduct coins or not enough coins.");
                }
            }
        });
    }

    private void sendThemeChangedBroadcast() {
        Intent intent = new Intent("sg.edu.np.mad.p04_team4.THEME_CHANGED");
        sendBroadcast(intent);
    }

    private int getThemeImageResId(String themeName) {
        switch (themeName) {
            case "Fluid Harmony":
                return R.drawable.theme_1;
            case "Blue Blossom":
                return R.drawable.theme_2;
            case "Playful Safari":
                return R.drawable.theme_3;
            default:
                throw new IllegalArgumentException("Invalid theme name: " + themeName);
        }
    }

    private void saveSelectedTheme(String themeName) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedTheme", themeName);
        editor.apply(); // Use apply() for asynchronous saving
    }

    public void updateCoinDisplay(int newBalance) {
        coinAmountTextView.setText(String.valueOf(newBalance));
    }
}
