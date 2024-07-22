package sg.edu.np.mad.p04_team4.Chat;

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
    private List<Integer> stickerResIds;

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

        stickerResIds = new ArrayList<>();
        stickerAdapter = new StickerAdapter(stickerResIds, this, url -> sendStickerMessage(url));
        stickerRecyclerView.setAdapter(stickerAdapter);
        stickerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if ("Cat Sticker Pack".equals(packName)) {
            loadCatStickers();
        }
        // Add more packs here if needed
    }

    private void loadCatStickers() {
        stickerResIds.add(R.drawable.cat_angry);
        stickerResIds.add(R.drawable.cat_bored);
        stickerResIds.add(R.drawable.cat_crying);
        stickerResIds.add(R.drawable.cat_facepalm);
        stickerResIds.add(R.drawable.cat_happy);
        stickerResIds.add(R.drawable.cat_sleep);
        stickerAdapter.notifyDataSetChanged();
    }

    private void sendStickerMessage(int stickerResId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            long currentTime = System.currentTimeMillis();
            Message message = new StickerMessage(currentTime, currentTime, String.valueOf(stickerResId), userId);
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
