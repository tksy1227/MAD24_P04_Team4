package sg.edu.np.mad.p04_team4.HabitTracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import sg.edu.np.mad.p04_team4.R;

public class createHabit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_habit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.buttonSaveHabit);
        TextInputLayout habitDescriptionInput = findViewById(R.id.textInputLayoutHabitDescription);
        TextInputLayout habitNameInput = findViewById(R.id.textInputLayoutHabitName);
        TextInputLayout habitUnitInput = findViewById(R.id.textInputLayoutUnitOfMeasure);
/*
// Listen for changes in text to enable/disable the button
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if all fields have text entered
                String habitDescription = habitDescriptionInput.getEditText().getText().toString();
                String habitName = habitNameInput.getEditText().getText().toString();
                String habitUnit = habitUnitInput.getEditText().getText().toString();

                // Enable or disable the save button based on text presence
                saveButton.setEnabled(!habitDescription.isEmpty() && !habitName.isEmpty() && !habitUnit.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        };

        // Attach the text watcher to each TextInputEditText
        habitDescriptionInput.getEditText().addTextChangedListener(textWatcher);
        habitNameInput.getEditText().addTextChangedListener(textWatcher);
        habitUnitInput.getEditText().addTextChangedListener(textWatcher);

        // Initially disable the button until all fields have text
       //saveButton.setEnabled(false);

 */
        saveButton.setOnClickListener(v -> {
            // Check if any required field is empty
            String habitDescription = habitDescriptionInput.getEditText().getText().toString();
            String habitName = habitNameInput.getEditText().getText().toString();
            String habitUnit = habitUnitInput.getEditText().getText().toString();
            chartDBhandler dbHandler = new chartDBhandler(createHabit.this);
            ArrayList<String> unique_habits = dbHandler.unique_habit();


            if (habitDescription.isEmpty() || habitName.isEmpty() || habitUnit.isEmpty()) {
                // Show a Toast message indicating the requirements are not met
                Toast.makeText(getApplicationContext(), getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
            } else if (unique_habits.contains(habitName))
            {
                Toast.makeText(getApplicationContext(), getString(R.string.habit_alr_exists), Toast.LENGTH_SHORT).show();

            } else {
                // Proceed with saving the habit data
                // Add your saving logic here
                Toast.makeText(getApplicationContext(), getString(R.string.habit_saved), Toast.LENGTH_SHORT).show();
                // Example: Call a method to save data to database or perform other actions
                Intent datainput = new Intent(createHabit.this, newHabitDatainput.class);
                datainput.putExtra(getString(R.string.habit),habitName);
                datainput.putExtra(getString(R.string.desc),habitDescription);
                datainput.putExtra(getString(R.string.unit),habitUnit);

                startActivity(datainput);
            }
        });


        backButton.setOnClickListener(v->{
            Intent back = new Intent(createHabit.this,selectHabit.class);
            startActivity(back);
        });


    }
}