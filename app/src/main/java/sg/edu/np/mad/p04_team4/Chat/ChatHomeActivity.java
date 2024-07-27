package sg.edu.np.mad.p04_team4.Chat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.R;

public class ChatHomeActivity extends AppCompatActivity {

    private static final String TAG = "ChatHomeActivity";

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private ImageButton backButton;
    private Button buttonAddChat;
    private SearchView searchView;

    private FirebaseAuth mAuth;
    private DatabaseReference userChatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);ThemeUtils.applyTheme(this, rootView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("chats");
        } else {
            Log.e(TAG, "No authenticated user found.");
            return;
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

        loadChats();
    }

    private void showAddChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_chat_name));

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
                Toast.makeText(ChatHomeActivity.this, getString(R.string.chat_cannot_empty), Toast.LENGTH_SHORT).show();
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton(getString(R.string.chat_cancel), (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    private void addChat(String chatName) {
        long currentTime = System.currentTimeMillis();
        Chat newChat = new Chat(chatName, getString(R.string.hey_friendo), String.valueOf(currentTime));

        DatabaseReference newChatRef = userChatsRef.push();
        String chatRoomId = newChatRef.getKey();

        newChatRef.setValue(newChat).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ChatHomeActivity.this, getString(R.string.chat_added), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChatHomeActivity.this, getString(R.string.chat_add_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChats() {
        userChatsRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(ChatHomeActivity.this, getString(R.string.failed_load_chats), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteChat(int position) {
        String chatKey = chatList.get(position).getKey();
        if (chatKey != null) {
            userChatsRef.child(chatKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChatHomeActivity.this, getString(R.string.chat_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatHomeActivity.this, getString(R.string.failed_delete_chat), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.chat_key_null), Toast.LENGTH_SHORT).show();
        }
    }
}
