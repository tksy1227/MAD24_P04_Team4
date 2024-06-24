package sg.edu.np.mad.p04_team4;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

// Main activity class for the To-Do list application
public class MainActivity_TodoList extends AppCompatActivity {
    private ArrayList<String> items;
    private ListView list;
    private Button button;
    private ArrayAdapter<String> itemsAdapter;
    private TodoListDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo_list);

        // Set window insets to handle system bars (e.g., status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the database helper
        dbHelper = new TodoListDBHelper(this);

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

        // Load items from the database and set up the ListView adapter
        items = dbHelper.getAllItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(itemsAdapter);

        // Set the long click listener to remove items
        list.setOnItemLongClickListener((parent, view, position, id) -> remove(position));
    }

    // Method to remove an item from the list and database
    private boolean remove(int position) {
        Context context = getApplicationContext();
        String item = items.get(position);
        dbHelper.deleteItem(item); // Delete the item from the database
        Toast.makeText(context, "Item Removed", Toast.LENGTH_LONG).show(); // Show a toast message
        items.remove(position); // Remove the item from the list
        itemsAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
        return true;
    }

    // Method to add an item to the list and database
    private void addItem(View view) {
        EditText input = findViewById(R.id.edit_text);
        String itemText = input.getText().toString();
        if (!itemText.equals("")) {
            dbHelper.addItem(itemText); // Add the item to the database
            itemsAdapter.add(itemText); // Add the item to the list adapter
            input.setText(""); // Clear the input field
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter Text", Toast.LENGTH_SHORT).show(); // Show a toast message if input is empty
        }
    }
}
