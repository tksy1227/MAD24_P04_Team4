package sg.edu.np.mad.p04_team4;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Chat_Main extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private static final String TAG = "MainActivity"; // Tag for logging

    private RecyclerView recyclerView; // RecyclerView to display messages
    private MessageAdapter adapter; // Adapter for RecyclerView
    private List<Message> messageList; // List to store messages
    private EditText editTextMessage; // EditText for message input
    private Button buttonSend; // Button to send message
    private ImageButton buttonSelectImage; // Button to select image
    private ImageButton backButton; // Button to go back

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private DatabaseReference messagesRef; // Database reference for messages
    private boolean isUserSignedIn = false; // Flag to indicate if user is signed in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        // Retrieve the data passed with the Intent
        String chatName = getIntent().getStringExtra("chat_name");
        String chatRoomId = getIntent().getStringExtra("chat_room_id"); // Get the chat room ID

        TextView chatNameTextView = findViewById(R.id.username);
        if (chatName != null) {
            chatNameTextView.setText(chatName);
        }

        // Update last seen time
        updateLastSeenTime(chatRoomId);

        // Display last seen time
        displayLastSeenTime(chatRoomId);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId).child("messages");

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        backButton = findViewById(R.id.backButton);

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
                if (isUserSignedIn) {
                    sendMessage();
                } else {
                    Log.e(TAG, "No user is currently signed in");
                }
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
                // Navigate back to the chat home page
                finish();
            }
        });

        // Ensure Firebase is initialized and a user is signed in
        ensureUserSignedIn();
    }

    private void ensureUserSignedIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.d(TAG, "No user signed in. Signing in anonymously...");
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInAnonymously:success");
                        isUserSignedIn = true;
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "User ID after sign-in: " + (user != null ? user.getUid() : "No User ID"));
                        loadMessages();
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                    }
                }
            });
        } else {
            Log.d(TAG, "User is already signed in");
            isUserSignedIn = true;
            Log.d(TAG, "User ID: " + user.getUid());
            loadMessages();
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
                messagesRef.push().setValue(message).addOnCompleteListener(task -> {
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
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);
        chatRef.child("time").setValue(String.valueOf(time));
    }

    private void updateLastSeenTime(String chatRoomId) {
        long currentTime = System.currentTimeMillis();
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);
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
                messagesRef.push().setValue(message).addOnCompleteListener(task -> {
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
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId).child("lastSeen");
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long lastSeenTime = dataSnapshot.getValue(Long.class);
                    String formattedTime = getTimeDifference(lastSeenTime);
                    TextView lastSeenTextView = findViewById(R.id.status); // Replace with your actual TextView ID
                    lastSeenTextView.setText("Last seen " + formattedTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to retrieve last seen time", databaseError.toException());
            }
        });
    }

    private String getTimeDifference(long timeInMillis) {
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - timeInMillis;

        long seconds = difference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " d " + (hours % 24) + " h " + (minutes % 60) + " min ago";
        } else if (hours > 0) {
            return hours + " h " + (minutes % 60) + " min ago";
        } else if (minutes > 0) {
            return minutes + " min ago";
        } else {
            return "just now";
        }
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
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
                isUserSignedIn = true;
                loadMessages();
            } else {
                Log.d(TAG, "No user is signed in.");
                isUserSignedIn = false;
            }
        }
    };
}

