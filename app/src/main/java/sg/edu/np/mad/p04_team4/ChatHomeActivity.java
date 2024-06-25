package sg.edu.np.mad.p04_team4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;



public class ChatHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // RecyclerView to display chats
    private ChatAdapter chatAdapter; // Adapter for RecyclerView
    private List<Chat> chatList; // List to store chats
    private ImageButton backButton; // Button to navigate back
    private Button buttonAddChat; // Button to add a new chat

    private DatabaseReference chatsRef; // Database reference for chats

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        // Set up the toolbar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize back button and set click listener
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize chat list and adapter
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        recyclerView.setAdapter(chatAdapter);

        // Initialize add chat button and set click listener
        buttonAddChat = findViewById(R.id.buttonAddChat);
        buttonAddChat.setOnClickListener(v -> showAddChatDialog());

        // Initialize Firebase Database reference for chats
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        // Load chats from Firebase
        loadChats();
    }

    // Method to load chats from Firebase
    private void loadChats() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null) {
                        chat.setKey(snapshot.getKey());
                        chatList.add(chat);
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatHomeActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to add a new chat
    private void addChat(String chatName) {
        long currentTime = System.currentTimeMillis();
        Chat newChat = new Chat(chatName, "Hey Friendo!", String.valueOf(currentTime));

        // Generate a unique ID for the new chat room
        DatabaseReference newChatRef = chatsRef.push();
        String chatRoomId = newChatRef.getKey();

        newChatRef.setValue(newChat).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ChatHomeActivity.this, "Chat added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChatHomeActivity.this, "Failed to add chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddChatDialog() {
        // Create an AlertDialog builder to build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Chat Name"); // Set the title of the dialog

        // Create a LinearLayout to hold the EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density); // 16dp padding
        layout.setPadding(padding, padding, padding, padding);

        // Create an EditText input field for the chat name
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set the width of the EditText in pixels
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                (int) (325 * getResources().getDisplayMetrics().density), // 200dp converted to pixels
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(lp);

        // Add the EditText to the LinearLayout
        layout.addView(input);
        builder.setView(layout);

        // Set the positive button with the action to be performed when clicked
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Get the entered chat name
            String chatName = input.getText().toString().trim();
            if (!chatName.isEmpty()) {
                // If the chat name is not empty, call the addChat method with the entered name
                addChat(chatName);
            } else {
                // Show a toast message if the chat name is empty
                Toast.makeText(ChatHomeActivity.this, "Chat name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    // Method to delete a chat
    public void deleteChat(int position) {
        String chatKey = chatList.get(position).getKey(); // Get the key of the chat to be deleted
        if (chatKey != null) {
            chatsRef.child(chatKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChatHomeActivity.this, "Chat deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatHomeActivity.this, "Failed to delete chat", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Chat key is null", Toast.LENGTH_SHORT).show();
        }
    }
}

