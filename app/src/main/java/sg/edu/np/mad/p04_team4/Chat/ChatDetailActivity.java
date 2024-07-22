package sg.edu.np.mad.p04_team4.Chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import sg.edu.np.mad.p04_team4.R;

public class ChatDetailActivity extends AppCompatActivity {

    private static final String TAG = "ChatDetailActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference chatRef;

    private TextView chatNameTextView;
    private TextView chatMessageTextView;
    private TextView chatTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve any data passed with the Intent
        String chatName = getIntent().getStringExtra("chat_name");
        String chatRoomId = getIntent().getStringExtra("chat_room_id");

        // Initialize Views
        chatNameTextView = findViewById(R.id.chat_name);
        chatMessageTextView = findViewById(R.id.chat_message);
        chatTimeTextView = findViewById(R.id.chat_time);

        if (chatName != null) {
            chatNameTextView.setText(chatName);
        }

        // Ensure Firebase is initialized and a user is signed in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && chatRoomId != null) {
            chatRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("chats").child(chatRoomId);
            loadChatDetails();
        } else {
            Log.e(TAG, "No authenticated user or chatRoomId is null.");
        }
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
}
