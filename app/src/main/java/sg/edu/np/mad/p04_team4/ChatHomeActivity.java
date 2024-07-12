package sg.edu.np.mad.p04_team4;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatHomeActivity extends AppCompatActivity {

    private static final String TAG = "ChatHomeActivity";

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private ImageButton backButton;
    private Button buttonAddChat;
    private SearchView searchView;

    private DatabaseReference chatsRef;
//hello testing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView); // This should be androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                chatAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatAdapter.getFilter().filter(newText);
                return false;
            }
        });

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        recyclerView.setAdapter(chatAdapter);

        buttonAddChat = findViewById(R.id.buttonAddChat);
        buttonAddChat.setOnClickListener(v -> showAddChatDialog());

        chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        loadChats();
    }

    private void showAddChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Chat Name");

        // Create an EditText input field for the chat name
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set layout parameters for the EditText to control the width
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                (int) (300 * getResources().getDisplayMetrics().density), // 300dp converted to pixels
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(lp);
        int padding = (int) (16 * getResources().getDisplayMetrics().density); // 16dp padding
        input.setPadding(padding, padding, padding, padding);
        builder.setView(input);

        // Set the positive button with the action to be performed when clicked
        builder.setPositiveButton("OK", (dialog, which) -> {
            String chatName = input.getText().toString().trim();
            if (!chatName.isEmpty()) {
                addChat(chatName);
            } else {
                Toast.makeText(ChatHomeActivity.this, "Chat name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    private void addChat(String chatName) {
        long currentTime = System.currentTimeMillis();
        Chat newChat = new Chat(chatName, "Hey Friendo!", String.valueOf(currentTime));

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
                        Log.d(TAG, "Loaded chat: " + chat.getName() + ", time: " + chat.getTime());
                    } else {
                        Log.e(TAG, "Chat is null for snapshot: " + snapshot.getKey());
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatAdapter.setChatListFull(new ArrayList<>(chatList)); // Ensure chatListFull is initialized

                // Log the size of the full chat list
                Log.d(TAG, "Chat list loaded: " + chatList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatHomeActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteChat(int position) {
        String chatKey = chatList.get(position).getKey();
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
