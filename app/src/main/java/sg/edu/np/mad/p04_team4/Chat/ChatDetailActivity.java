package sg.edu.np.mad.p04_team4.Chat;

import android.os.Bundle;
import android.util.Log;
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

public class ChatDetailActivity extends AppCompatActivity {

    private static final String TAG = "ChatDetailActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference messagesDatabaseReference;

    private TextView chatNameTextView;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve data passed with the Intent
        String chatName = getIntent().getStringExtra("chat_name");
        chatRoomId = getIntent().getStringExtra("chat_room_id");

        // Initialize Views
        chatNameTextView = findViewById(R.id.chat_name);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, this);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        if (chatName != null) {
            chatNameTextView.setText(chatName);
        }

        // Ensure Firebase is initialized and a user is signed in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && chatRoomId != null) {
            messagesDatabaseReference = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId).child("messages");
            loadMessages();
        } else {
            Log.e(TAG, "No authenticated user or chatRoomId is null.");
        }
    }

    private void loadMessages() {
        messagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String messageType = snapshot.child("type").getValue(String.class);
                        Message message = null;

                        if ("text".equals(messageType)) {
                            message = snapshot.getValue(TextMessage.class);
                        } else if ("image".equals(messageType)) {
                            message = snapshot.getValue(ImageMessage.class);
                        } else if ("sticker".equals(messageType)) {
                            message = snapshot.getValue(StickerMessage.class);
                        } else {
                            Log.w(TAG, "Unknown message type: " + messageType);
                            continue;
                        }

                        if (message != null) {
                            messageList.add(message);
                            Log.d(TAG, "Message added: " + message.toString());
                        } else {
                            Log.d(TAG, "Message is null for snapshot: " + snapshot.toString());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse message", e);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                Log.d(TAG, "Messages loaded successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load messages", databaseError.toException());
            }
        });
    }

    public void sendStickerMessage(String stickerUrl) {
        long currentTime = System.currentTimeMillis();
        StickerMessage stickerMessage = new StickerMessage(currentTime, currentTime, stickerUrl, mAuth.getCurrentUser().getUid());
        Log.d(TAG, "Sending sticker message with URL: " + stickerUrl);
        messagesDatabaseReference.push().setValue(stickerMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Sticker message sent successfully with URL: " + stickerUrl);
            } else {
                Log.e(TAG, "Failed to send sticker", task.getException());
            }
        });
    }
}
