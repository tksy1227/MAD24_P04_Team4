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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.p04_team4.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class selectHabit extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_habit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton back = findViewById(R.id.backButton);
        Button top_left = findViewById(R.id.button_top_left);
        Button top_right = findViewById(R.id.button_top_right);
        Button bottom_left = findViewById(R.id.button_bottom_left);
        Button bottom_right = findViewById(R.id.button_bottom_right);
        Button create_habit = findViewById(R.id.button_small_bottom_right);
        TextView title = findViewById(R.id.textViewToolbarTitle);
        title.setText(getString(R.string.habit_tracker));

        chartDBhandler dbHandler = new chartDBhandler(selectHabit.this);
        //dbHandler.insertData(new chartData("uhm",125,"2024-06-01"));
        ArrayList<String> unique_habits = dbHandler.unique_habit();
        HashMap<Button, String> buttonToHabitMap = new HashMap<>();
        back.setOnClickListener(v->{
            Intent home = new Intent(selectHabit.this, HomeActivity.class);
            startActivity(home);
        });


        Button[] buttons = { top_left, top_right, bottom_left, bottom_right };

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
                    buttonText.append("<br/>"+getString(R.string.default_no_desc)+"<br/>"); // Default text if no description
                }

                // Add start date if available
                if (startDate != null) {
                    buttonText.append("<br/><i>"+getString(R.string.start_date)).append(startDate).append("</i>");
                } else {
                    buttonText.append("<br/><i>"+getString(R.string.start_date_not_avail)+"</i>"); // Default text if no start date
                }

                // Set formatted text with HTML
                buttons[i].setText(Html.fromHtml(buttonText.toString()));

                // Adjust text size and padding
                buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Adjust text size
                buttons[i].setPadding(20, 20, 20, 20); // Add padding


                // Map button to habit
                buttonToHabitMap.put(buttons[i], habit);

                // Set click listener
                buttons[i].setOnClickListener(v -> meow(habit));

                // Set long click listener
                buttons[i].setOnLongClickListener(v -> {
                    showConfirmationDialog(habit, dbHandler);
                    return true; // Return true to consume the long click event
                });
            } else {
                buttons[i].setVisibility(View.GONE); // Hide extra buttons if there are fewer than 4 habits
            }
        }
        if (unique_habits.size()<4) {
            create_habit.setText(getString(R.string.set_new_habit));
            create_habit.setOnClickListener(v-> {
                Intent set_habit = new Intent(selectHabit.this, createHabit.class);
                startActivity(set_habit);
            });

        }
        else if (unique_habits.size()>=4) {
            create_habit.setVisibility(View.GONE);
        }
    }

    public void meow(String habit) {
        Toast.makeText(this, getString(R.string.Habit ) + habit + getString(R.string.clicked_space), Toast.LENGTH_SHORT).show();
        Intent viewchart = new Intent(selectHabit.this, Chartview.class);

        viewchart.putExtra(getString(R.string.habit), habit);
        viewchart.putExtra(getString(R.string.option),"1");
        startActivity(viewchart);
    }
    private void showConfirmationDialog(String habit,chartDBhandler db) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_habit));
        builder.setMessage(getString(R.string.delete_habit_confirm1)+habit+getString(R.string.delete_habit_confirm2));



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
                db.deleteDataByHabit(habit);
                Toast.makeText(selectHabit.this,  habit + getString(R.string.habit_deleted), Toast.LENGTH_SHORT).show();

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}