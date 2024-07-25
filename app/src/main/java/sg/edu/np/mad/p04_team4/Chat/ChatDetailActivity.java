package sg.edu.np.mad.p04_team4.Chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sg.edu.np.mad.p04_team4.DailyLoginReward.StickerPackDialogFragment;
import sg.edu.np.mad.p04_team4.R;

public class ChatDetailActivity extends AppCompatActivity {

    private static final String TAG = "ChatDetailActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference chatRef;
    private DatabaseReference messagesDatabaseReference;

    private TextView chatNameTextView;
    private TextView chatMessageTextView;
    private TextView chatTimeTextView;
    private ImageButton btnSendSticker;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve any data passed with the Intent
        String chatName = getIntent().getStringExtra("chat_name");
        chatRoomId = getIntent().getStringExtra("chat_room_id");

        // Initialize Views
        chatNameTextView = findViewById(R.id.chat_name);
        chatMessageTextView = findViewById(R.id.chat_message);
        chatTimeTextView = findViewById(R.id.chat_time);
        btnSendSticker = findViewById(R.id.btnSendSticker);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, this);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        if (chatName != null) {
            chatNameTextView.setText(chatName);
        }

        // Ensure Firebase is initialized and a user is signed in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && chatRoomId != null) {
            chatRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("chats").child(chatRoomId);
            messagesDatabaseReference = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId).child("messages");
            loadChatDetails();
            loadMessages();
        } else {
            Log.e(TAG, "No authenticated user or chatRoomId is null.");
        }

        btnSendSticker.setOnClickListener(v -> openStickerPicker());
    }

    private void loadChatDetails() {
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                if (chat != null) {
                    chatNameTextView.setText(chat.getName());
                    chatMessageTextView.setText(chat.getLastMessage());

                    try {
                        long timeInMillis = Long.parseLong(chat.getTime());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Singapore");
                        sdf.setTimeZone(localTimeZone);
                        String formattedTime = sdf.format(timeInMillis);
                        chatTimeTextView.setText(formattedTime);
                    } catch (NumberFormatException | NullPointerException e) {
                        Log.e(TAG, "Error parsing chat time", e);
                        chatTimeTextView.setText("Unknown time");
                    }
                } else {
                    Log.e(TAG, "Chat data is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load chat details", error.toException());
            }
        });
    }

    private void loadMessages() {
        messagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatDetailActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openStickerPicker() {
        // Open Sticker Picker (e.g., StickerPackDialogFragment) and handle sticker selection
        StickerPackDialogFragment dialog = new StickerPackDialogFragment("Pack Name", mAuth.getCurrentUser().getUid(), chatRoomId, 50);
        dialog.show(getSupportFragmentManager(), "StickerPackDialogFragment");
    }

    public void sendStickerMessage(String stickerUrl) {
        long currentTime = System.currentTimeMillis();
        StickerMessage stickerMessage = new StickerMessage(currentTime, currentTime, stickerUrl, mAuth.getCurrentUser().getUid());
        messagesDatabaseReference.push().setValue(stickerMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ChatDetailActivity.this, "Sticker sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChatDetailActivity.this, "Failed to send sticker.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
