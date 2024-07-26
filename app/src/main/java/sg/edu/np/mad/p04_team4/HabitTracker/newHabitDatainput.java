package sg.edu.np.mad.p04_team4.HabitTracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.R;

public class newHabitDatainput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_habit_datainput);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton backbutton = findViewById(R.id.backButton);
        Button saveData = findViewById(R.id.buttonSaveData);
        TextInputLayout textInputLayoutValue = findViewById(R.id.textInputLayoutValue);
        TextInputEditText editTextValue = findViewById(R.id.editTextValue);
        TextView title = findViewById(R.id.textViewToolbarTitle);
        title.setText("Data Input");

        chartDBhandler dbHandler = new chartDBhandler(newHabitDatainput.this);

        // Getting habit passed from previous activity
        String habit = getIntent().getStringExtra("habit");
        String desc = getIntent().getStringExtra("desc");
        String unit = getIntent().getStringExtra("unit");
        textInputLayoutValue.setHint("Number of "+unit);



        if (habit == null) {
            // Error handling
            Log.e("Button", "Habit not passed");
        }

        // Back button logic
        backbutton.setOnClickListener(v -> {
            Intent back = new Intent(newHabitDatainput.this, createHabit.class);
            startActivity(back);
        });

        // Save data button logic
        saveData.setOnClickListener(v -> {
            String valueStr = editTextValue.getText().toString();
            if (!valueStr.isEmpty()) {
                try {
                    int value = Integer.parseInt(valueStr);
                    // Get current date in ISO 8601 format
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    chartData data = new chartData(habit, value, currentDate,unit,desc);
                    dbHandler.insertData(data);
                    Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                    // Optionally, you can navigate back or clear the input
                    editTextValue.setText("");
                    Intent gochartview = new Intent(newHabitDatainput.this, Chartview.class);
                    gochartview.putExtra("habit",habit);
                    startActivity(gochartview);
                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid integer
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
            }
        });
    }
}