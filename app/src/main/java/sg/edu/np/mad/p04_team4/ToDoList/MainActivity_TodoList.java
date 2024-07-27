package sg.edu.np.mad.p04_team4.ToDoList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;

// Main activity class for the To-Do list application
public class MainActivity_TodoList extends AppCompatActivity {
    private ArrayList<String> items;
    private ListView list;
    private Button button;
    private ArrayAdapter<String> itemsAdapter;
    private DatabaseReference todoRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo_list);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        // Set window insets to handle system bars (e.g., status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            todoRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("todo_items");
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable the default title

        // Set up the back button in the toolbar
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize the ListView and Button
        list = findViewById(R.id.list);
        button = findViewById(R.id.button);

        // Set the button click listener to add items
        button.setOnClickListener(this::addItem);

        // Load items from Firebase and set up the ListView adapter
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(itemsAdapter);

        // Set the long click listener to remove items
        list.setOnItemLongClickListener((parent, view, position, id) -> remove(position));

        loadItems();
    }

    // Method to remove an item from the list and Firebase
    private boolean remove(int position) {
        Context context = getApplicationContext();
        String item = items.get(position);
        todoRef.child(item).removeValue(); // Delete the item from Firebase
        Toast.makeText(context, "Item Removed", Toast.LENGTH_LONG).show(); // Show a toast message
        items.remove(position); // Remove the item from the list
        itemsAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
        return true;
    }

    // Method to add an item to the list and Firebase
    private void addItem(View view) {
        EditText input = findViewById(R.id.edit_text);
        String itemText = input.getText().toString();
        if (!itemText.equals("")) {
            todoRef.child(itemText).setValue(itemText); // Add the item to Firebase
            itemsAdapter.add(itemText); // Add the item to the list adapter
            input.setText(""); // Clear the input field
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter Text", Toast.LENGTH_SHORT).show(); // Show a toast message if input is empty
        }
    }

    // Method to load items from Firebase
    private void loadItems() {
        todoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String item = itemSnapshot.getValue(String.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
                itemsAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity_TodoList.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
