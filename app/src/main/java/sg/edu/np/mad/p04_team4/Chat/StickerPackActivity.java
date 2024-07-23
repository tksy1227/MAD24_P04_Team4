package sg.edu.np.mad.p04_team4.Chat;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class StickerPackActivity extends AppCompatActivity {

    private static final String TAG = "StickerPackActivity";
    private FirebaseAuth mAuth;
    private RecyclerView stickerRecyclerView;
    private StickerAdapter stickerAdapter;
    private List<String> stickerPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack);

        mAuth = FirebaseAuth.getInstance();

        stickerRecyclerView = findViewById(R.id.stickerRecyclerView);
        TextView packNameTextView = findViewById(R.id.stickerPackTitle);
        ImageView backArrow = findViewById(R.id.backArrow);

        String packName = getIntent().getStringExtra("packName");
        packNameTextView.setText(packName);

        backArrow.setOnClickListener(v -> finish());

        stickerPaths = new ArrayList<>();
        stickerAdapter = new StickerAdapter(stickerPaths, this, url -> sendStickerMessage(url));
        stickerRecyclerView.setAdapter(stickerAdapter);
        stickerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if ("Cat Sticker Pack".equals(packName)) {
            loadCatStickers();
        }
        // Add more packs here if needed
    }

    private void loadCatStickers() {
        stickerPaths.add(getResourceUri(R.drawable.cat_angry).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_bored).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_crying).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_facepalm).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_happy).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_sleep).toString());
        stickerAdapter.notifyDataSetChanged();
    }

    private Uri getResourceUri(int resourceId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
    }

    private void sendStickerMessage(String stickerPath) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            long currentTime = System.currentTimeMillis();
            Message message = new StickerMessage(currentTime, currentTime, stickerPath, userId);
            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("chats").child(getIntent().getStringExtra("chat_room_id")).child("messages");
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
