package sg.edu.np.mad.p04_team4.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class StickerPageActivity extends AppCompatActivity {

    private static final String TAG = "StickerPageActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private String chatRoomId;
    private RecyclerView stickerRecyclerView;
    private StickerAdapter stickerAdapter;
    private List<Uri> stickerPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_page);

        // Initialize FirebaseAuth and get the current user
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();

        // Retrieve the chat room ID from the intent
        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");

        // Set up the RecyclerView for displaying stickers
        stickerRecyclerView = findViewById(R.id.stickerRecyclerView);
        stickerPaths = new ArrayList<>();
        stickerAdapter = new StickerAdapter(stickerPaths, this, this::sendStickerMessage, true);

        stickerRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        stickerRecyclerView.setAdapter(stickerAdapter);

        // Fetch the purchased stickers for the user
        fetchPurchasedStickers();
    }

    // Fetch the purchased stickers from the Firebase database
    private void fetchPurchasedStickers() {
        DatabaseReference userStickersRef = mDatabase.child("users").child(userId).child("purchasedStickerPacks");
        userStickersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stickerPaths.clear();
                for (DataSnapshot packSnapshot : dataSnapshot.getChildren()) {
                    boolean isPurchased = packSnapshot.getValue(Boolean.class);
                    if (isPurchased) {
                        String packName = packSnapshot.getKey();
                        loadStickers(packName);
                    }
                }
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load user stickers", databaseError.toException());
            }
        });
    }

    // Load stickers based on the sticker pack name
    private void loadStickers(String packName) {
        switch (packName) {
            case "Cat Sticker Pack":
                loadCatStickers();
                break;
            case "Dead by Daylight Sticker Pack":
                loadDeadByDaylightStickers();
                break;
            case "Alpha Wolf Sticker Pack":
                loadAlphaWolfStickers();
                break;
            case "Monkey Sticker Pack":
                loadMonkeyStickers();
                break;
            case "Skibidi Toilet Sticker Pack":
                loadSkibidiStickers();
                break;
            case "Emoji Sticker Pack": // New case for Emoji Sticker Pack
                loadEmojiStickers();
                break;
            default:
                Log.e(TAG, "Unknown sticker pack: " + packName);
                break;
        }
    }

    // Load stickers for Cat Sticker Pack
    private void loadCatStickers() {
        stickerPaths.add(getResourceUri(R.drawable.cat_angry));
        stickerPaths.add(getResourceUri(R.drawable.cat_bored));
        stickerPaths.add(getResourceUri(R.drawable.cat_crying));
        stickerPaths.add(getResourceUri(R.drawable.cat_facepalm));
        stickerPaths.add(getResourceUri(R.drawable.cat_happy));
        stickerPaths.add(getResourceUri(R.drawable.cat_sleep));
    }

    // Load stickers for Dead by Daylight Sticker Pack
    private void loadDeadByDaylightStickers() {
        stickerPaths.add(getResourceUri(R.drawable.claudette));
        stickerPaths.add(getResourceUri(R.drawable.dwight));
        stickerPaths.add(getResourceUri(R.drawable.ghostface));
        stickerPaths.add(getResourceUri(R.drawable.legion));
        stickerPaths.add(getResourceUri(R.drawable.sadako));
    }

    // Load stickers for Alpha Wolf Sticker Pack
    private void loadAlphaWolfStickers() {
        stickerPaths.add(getResourceUri(R.drawable.wolf_grin));
        stickerPaths.add(getResourceUri(R.drawable.wolf_growl));
        stickerPaths.add(getResourceUri(R.drawable.wolf_howl));
        stickerPaths.add(getResourceUri(R.drawable.wolf_pack));
    }

    // Load stickers for Monkey Sticker Pack
    private void loadMonkeyStickers() {
        stickerPaths.add(getResourceUri(R.drawable.monkey_angry));
        stickerPaths.add(getResourceUri(R.drawable.monkey_fp));
        stickerPaths.add(getResourceUri(R.drawable.monkey_happy));
        stickerPaths.add(getResourceUri(R.drawable.monkey_no));
        stickerPaths.add(getResourceUri(R.drawable.monkey_yes));
        stickerPaths.add(getResourceUri(R.drawable.monkey_sad));
    }

    // Load stickers for Skibidi Toilet Sticker Pack
    private void loadSkibidiStickers() {
        stickerPaths.add(getResourceUri(R.drawable.skibidi_angry));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_confused));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_happy));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_mindblown));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_sad));
    }

    // Load stickers for Emoji Sticker Pack
    private void loadEmojiStickers() {
        stickerPaths.add(getResourceUri(R.drawable.emoji_angry));
        stickerPaths.add(getResourceUri(R.drawable.emoji_cry));
        stickerPaths.add(getResourceUri(R.drawable.emoji_laugh));
        stickerPaths.add(getResourceUri(R.drawable.emoji_mock));
    }

    // Get URI for a resource ID
    private Uri getResourceUri(int resourceId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
    }

    // Send sticker message to the chat
    private void sendStickerMessage(Uri stickerUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            long currentTime = System.currentTimeMillis();
            Message message = new StickerMessage(currentTime, currentTime, stickerUri.toString(), userId);
            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId).child("messages");
            userChatsRef.push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Sticker message sent successfully");
                } else {
                    Log.e(TAG, "Failed to send sticker message", task.getException());
                }
            });
        } else {
            Log.e(TAG, "No user is currently signed in");
        }
    }
}
