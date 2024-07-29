package sg.edu.np.mad.p04_team4.HabitTracker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.p04_team4.AddFriend.FriendListActivity;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;

public class selectHabit extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display for the activity
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_habit);

        // Apply window insets to handle system UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> {
            Intent homeIntent = new Intent(selectHabit.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Friend List
        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> {
            Intent friendIntent = new Intent(selectHabit.this, FriendListActivity.class);
            startActivity(friendIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Account
        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> {
            Intent accountIntent = new Intent(selectHabit.this, AccountActivity.class);
            startActivity(accountIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Initialize UI components
        ImageButton back = findViewById(R.id.backButton);
        Button top_left = findViewById(R.id.button_top_left);
        Button top_right = findViewById(R.id.button_top_right);
        Button bottom_left = findViewById(R.id.button_bottom_left);
        Button bottom_right = findViewById(R.id.button_bottom_right);
        Button create_habit = findViewById(R.id.button_small_bottom_right);
        TextView title = findViewById(R.id.textViewToolbarTitle);
        title.setText(getString(R.string.habit_tracker)); // Set the title of the activity

        // Help button to show instructions
        Button helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showHelpDialog());

        // Initialize database handler
        chartDBhandler dbHandler = new chartDBhandler(selectHabit.this);

        // Retrieve unique habits from the database
        ArrayList<String> unique_habits = dbHandler.unique_habit();
        HashMap<Button, String> buttonToHabitMap = new HashMap<>();

        // Set onClick listener for the back button to navigate to HomeActivity
        back.setOnClickListener(v -> {
            Intent home = new Intent(selectHabit.this, HomeActivity.class);
            startActivity(home);
        });

        // Array of buttons to display habits
        Button[] buttons = { top_left, top_right, bottom_left, bottom_right };

        // Iterate through the buttons and assign habits to them
        for (int i = 0; i < buttons.length; i++) {
            if (i < unique_habits.size()) {
                final int buttonIndex = i;
                String habit = unique_habits.get(i);
                String description = dbHandler.getDescriptionForHabit(habit);
                String startDate = dbHandler.getHabitStartDate(habit);

                // Format the button text with HTML-like tags for bold and spacing
                StringBuilder buttonText = new StringBuilder();
                buttonText.append("<b><big>").append(habit).append("</big></b><br/>");

                if (description != null) {
                    buttonText.append("<br/>").append(description).append("<br/>"); // Add description if available
                } else {
                    buttonText.append("<br/>").append(getString(R.string.default_no_desc)).append("<br/>"); // Default text if no description
                }

                // Add start date if available
                if (startDate != null) {
                    buttonText.append("<br/><i>").append(getString(R.string.start_date)).append(startDate).append("</i>");
                } else {
                    buttonText.append("<br/><i>").append(getString(R.string.start_date_not_avail)).append("</i>"); // Default text if no start date
                }

                // Set formatted text with HTML
                buttons[i].setText(Html.fromHtml(buttonText.toString()));

                // Adjust text size and padding
                buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Adjust text size
                buttons[i].setPadding(20, 20, 20, 20); // Add padding

                // Map button to habit
                buttonToHabitMap.put(buttons[i], habit);

                // Set click listener for the button
                buttons[i].setOnClickListener(v -> meow(habit));

                // Set long click listener for the button
                buttons[i].setOnLongClickListener(v -> {
                    showConfirmationDialog(habit, dbHandler);
                    return true; // Return true to consume the long click event
                });
            } else {
                buttons[i].setVisibility(View.GONE); // Hide extra buttons if there are fewer than 4 habits
            }
        }

        // Configure create habit button visibility and action based on the number of habits
        if (unique_habits.size() < 4) {
            create_habit.setText(getString(R.string.set_new_habit));
            create_habit.setOnClickListener(v -> {
                Intent set_habit = new Intent(selectHabit.this, createHabit.class);
                startActivity(set_habit);
            });
        } else if (unique_habits.size() >= 4) {
            create_habit.setVisibility(View.GONE);
        }
    }

    /**
     * Show a toast message and navigate to Chartview activity with the selected habit.
     *
     * @param habit The habit selected by the user.
     */
    public void meow(String habit) {
        Toast.makeText(this, getString(R.string.Habit) + habit + getString(R.string.clicked_space), Toast.LENGTH_SHORT).show();
        Intent viewchart = new Intent(selectHabit.this, Chartview.class);

        viewchart.putExtra(getString(R.string.habit), habit);
        viewchart.putExtra(getString(R.string.option), "1");
        startActivity(viewchart);
    }

    /**
     * Show a confirmation dialog to delete a habit.
     *
     * @param habit The habit to be deleted.
     * @param db The database handler to interact with the database.
     */
    private void showConfirmationDialog(String habit, chartDBhandler db) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_habit));
        builder.setMessage(getString(R.string.delete_habit_confirm1) + habit + getString(R.string.delete_habit_confirm2));

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform action when Cancel button is clicked
                Toast.makeText(getApplicationContext(), getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the habit from the database
                db.deleteDataByHabit(habit);
                Toast.makeText(selectHabit.this, habit + getString(R.string.habit_deleted), Toast.LENGTH_SHORT).show();

                // Refresh the activity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Show a help dialog with instructions for the user.
     */
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Help));
        builder.setMessage(getString(R.string.habit_instructions));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
