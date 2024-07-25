package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.p04_team4.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    private TextView coinAmountTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private String chatRoomId = "your_default_chat_room_id"; // Replace with actual chat room id or obtain dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page); // Ensure this matches your XML layout file name

        coinAmountTextView = findViewById(R.id.coinAmount);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            fetchUserCoins();
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

        // Set Click Listeners for the sticker pack buttons
        findViewById(R.id.btnSticker1).setOnClickListener(v -> openStickerPack("Cat Sticker Pack", 50));
        findViewById(R.id.btnSticker2).setOnClickListener(v -> openStickerPack("Monkey Sticker Pack", 50));
        findViewById(R.id.btnSticker3).setOnClickListener(v -> openStickerPack("Emoji Sticker Pack", 50));
        findViewById(R.id.btnSticker4).setOnClickListener(v -> openStickerPack("Alpha Wolf Sticker Pack", 50));
        findViewById(R.id.btnSticker5).setOnClickListener(v -> openStickerPack("Skibidi Toilet Sticker Pack", 50));
        findViewById(R.id.btnSticker6).setOnClickListener(v -> openStickerPack("Dead by Daylight Sticker Pack", 70));
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

    private void openStickerPack(String packName, int packCost) {
        StickerPackDialogFragment dialog = new StickerPackDialogFragment(packName, userId, chatRoomId, packCost);
        dialog.show(getSupportFragmentManager(), "StickerPackDialogFragment");
    }
}
