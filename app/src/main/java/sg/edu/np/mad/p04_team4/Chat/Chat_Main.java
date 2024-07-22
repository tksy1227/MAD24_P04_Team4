package sg.edu.np.mad.p04_team4.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sg.edu.np.mad.p04_team4.R;

public class Chat_Main extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private static final String TAG = "Chat_Main"; // Tag for logging

    private RecyclerView recyclerView; // RecyclerView to display messages
    private MessageAdapter adapter; // Adapter for RecyclerView
    private List<Message> messageList; // List to store messages
    private EditText editTextMessage; // EditText for message input
    private Button buttonSend; // Button to send message
    private ImageButton buttonSelectImage; // Button to select image
    private ImageButton backButton; // Button to go back

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private DatabaseReference userChatsRef; // Database reference for user's chats

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main); // Ensure this matches your XML layout file name

        // Retrieve the data passed with the Intent
        String chatName = getIntent().getStringExtra("chat_name");
        String chatRoomId = getIntent().getStringExtra("chat_room_id"); // Get the chat room ID

        TextView chatNameTextView = findViewById(R.id.username);
        if (chatName != null) {
            chatNameTextView.setText(chatName);
        }

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        backButton = findViewById(R.id.backButton); // Ensure this ID exists in your layout

        // Initialize RecyclerView
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Click Listener for Send Button
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Send button clicked");
                sendMessage();
            }
        });

        // Set Click Listener for Select Image Button
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Select Image button clicked");
                openImagePicker();
            }
        });

        // Set Click Listener for Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Ensure Firebase is initialized and a user is signed in
        ensureUserSignedIn(chatRoomId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will navigate to the previous activity
    }

    private void ensureUserSignedIn(String chatRoomId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "User is already signed in");
            userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("chats").child(chatRoomId).child("messages");
            loadMessages();
            updateLastSeenTime(chatRoomId);
            displayLastSeenTime(chatRoomId);
        } else {
            Log.e(TAG, "No user is currently signed in");
        }
    }

    private void sendMessage() {
        String text = editTextMessage.getText().toString();
        if (!text.isEmpty()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                long currentTime = System.currentTimeMillis();
                Message message = new TextMessage(currentTime, currentTime, text, userId);
                userChatsRef.push().setValue(message).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Message sent successfully");
                        editTextMessage.setText("");
                        updateChatLastInteractedTime(currentTime); // Update chat's last interaction time
                    } else {
                        Log.e(TAG, "Failed to send message", task.getException());
                    }
                });
            } else {
                Log.e(TAG, "No user is currently signed in");
            }
        } else {
            Log.d(TAG, "Message text is empty");
        }
    }

    private void updateChatLastInteractedTime(long time) {
        String chatRoomId = getIntent().getStringExtra("chat_room_id");
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(chatRoomId);
        chatRef.child("time").setValue(String.valueOf(time));
    }

    private void updateLastSeenTime(String chatRoomId) {
        long currentTime = System.currentTimeMillis();
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(chatRoomId);
        chatRef.child("lastSeen").setValue(currentTime);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                Message message = new ImageMessage(System.currentTimeMillis(), System.currentTimeMillis(), imageUri.toString(), userId);
                userChatsRef.push().setValue(message).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Image message sent successfully");
                    } else {
                        Log.e(TAG, "Failed to send image message", task.getException());
                    }
                });
            }
        }
    }

    private void displayLastSeenTime(String chatRoomId) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(chatRoomId).child("lastSeen");
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long lastSeenTime = dataSnapshot.getValue(Long.class);

                    // Log the retrieved last seen time
                    Log.d("Chat_Main", "Retrieved last seen time: " + lastSeenTime);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                    // Manually set the time zone to your local time zone
                    TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Singapore"); // Set your local time zone
                    Log.d("Chat_Main", "Local TimeZone: " + localTimeZone.getID());
                    sdf.setTimeZone(localTimeZone);
                    String formattedTime = sdf.format(new Date(lastSeenTime));

                    // Log the formatted last seen time
                    Log.d("Chat_Main", "Formatted last seen time: " + formattedTime);

                    TextView lastSeenTextView = findViewById(R.id.status);
                    lastSeenTextView.setText("Last seen " + formattedTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to retrieve last seen time", databaseError.toException());
            }
        });
    }

    private void loadMessages() {
        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // First, read the "type" field as a String
                        String messageType = snapshot.child("type").getValue(String.class);

                        Message message;
                        if ("text".equals(messageType)) {
                            message = snapshot.getValue(TextMessage.class);
                        } else if ("image".equals(messageType)) {
                            message = snapshot.getValue(ImageMessage.class);
                        } else {
                            Log.w(TAG, "Unknown message type: " + messageType);
                            continue; // Skip unknown message types
                        }

                        if (message != null) {
                            messageList.add(message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse message", e);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
                Log.d(TAG, "Messages loaded successfully");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load messages", databaseError.toException());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    // Authentication State Listener
    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "User is signed in with UID: " + user.getUid());
                loadMessages();
            } else {
                Log.d(TAG, "No user is signed in.");
            }
        }
    };
}
