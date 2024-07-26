package sg.edu.np.mad.p04_team4.Chat;

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

public class StickerPackActivity extends AppCompatActivity {

    private static final String TAG = "StickerPackActivity";
    private FirebaseAuth mAuth;
    private RecyclerView stickerRecyclerView;
    private StickerAdapter stickerAdapter;
    private List<Uri> stickerPaths;
    private DatabaseReference userStickerPacksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userStickerPacksRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("purchasedStickerPacks");
        }

        stickerRecyclerView = findViewById(R.id.stickerRecyclerView);
        ImageView backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> finish());

        stickerPaths = new ArrayList<>();
        stickerAdapter = new StickerAdapter(stickerPaths, this, uri -> sendStickerMessage(uri), true);
        stickerRecyclerView.setAdapter(stickerAdapter);
        stickerRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        checkPurchasedStickerPacks();
    }

    private void checkPurchasedStickerPacks() {
        if (userStickerPacksRef == null) {
            Log.e(TAG, "userStickerPacksRef is null.");
            return;
        }
        userStickerPacksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Purchased sticker packs found: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String packName = snapshot.getKey();
                        Boolean isPurchased = snapshot.getValue(Boolean.class);
                        Log.d(TAG, "Pack: " + packName + ", Purchased: " + isPurchased);
                        if (Boolean.TRUE.equals(isPurchased)) {
                            loadStickers(packName);
                        }
                    }
                } else {
                    Log.d(TAG, "No sticker packs purchased.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to check purchased sticker packs", databaseError.toException());
            }
        });
    }

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
            default:
                Log.e(TAG, "Unknown sticker pack: " + packName);
                break;
        }
        stickerAdapter.notifyDataSetChanged();
    }

    private void loadCatStickers() {
        stickerPaths.add(getResourceUri(R.drawable.cat_angry));
        stickerPaths.add(getResourceUri(R.drawable.cat_bored));
        stickerPaths.add(getResourceUri(R.drawable.cat_crying));
        stickerPaths.add(getResourceUri(R.drawable.cat_facepalm));
        stickerPaths.add(getResourceUri(R.drawable.cat_happy));
        stickerPaths.add(getResourceUri(R.drawable.cat_sleep));
    }

    private void loadDeadByDaylightStickers() {
        stickerPaths.add(getResourceUri(R.drawable.claudette));
        stickerPaths.add(getResourceUri(R.drawable.dwight));
        stickerPaths.add(getResourceUri(R.drawable.ghostface));
        stickerPaths.add(getResourceUri(R.drawable.legion));
        stickerPaths.add(getResourceUri(R.drawable.sadako));
    }

    private void loadAlphaWolfStickers() {
        stickerPaths.add(getResourceUri(R.drawable.wolf_grin));
        stickerPaths.add(getResourceUri(R.drawable.wolf_growl));
        stickerPaths.add(getResourceUri(R.drawable.wolf_howl));
        stickerPaths.add(getResourceUri(R.drawable.wolf_pack));
    }

    private void loadMonkeyStickers() {
        stickerPaths.add(getResourceUri(R.drawable.monkey_angry));
        stickerPaths.add(getResourceUri(R.drawable.monkey_fp));
        stickerPaths.add(getResourceUri(R.drawable.monkey_happy));
        stickerPaths.add(getResourceUri(R.drawable.monkey_no));
        stickerPaths.add(getResourceUri(R.drawable.monkey_yes));
        stickerPaths.add(getResourceUri(R.drawable.monkey_sad));
    }

    private void loadSkibidiStickers() {
        stickerPaths.add(getResourceUri(R.drawable.skibidi_angry));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_confused));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_happy));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_mindblown));
        stickerPaths.add(getResourceUri(R.drawable.skibidi_sad));
    }

    private Uri getResourceUri(int resourceId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
    }

    private void sendStickerMessage(Uri stickerUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            long currentTime = System.currentTimeMillis();
            Message message = new StickerMessage(currentTime, currentTime, stickerUri.toString(), userId);
            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("chats").child(getIntent().getStringExtra("chat_room_id")).child("messages");
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
