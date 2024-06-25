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
        buttonAddChat.setOnClickListener(v -> addChat());

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
    private void addChat() {
        String chatName = "New Chat " + (chatList.size() + 1);
        Chat newChat = new Chat(chatName, "Hello!", "Now");

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
