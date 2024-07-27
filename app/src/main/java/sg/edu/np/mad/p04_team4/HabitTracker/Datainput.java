package sg.edu.np.mad.p04_team4.HabitTracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;

public class Datainput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datainput);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

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
        title.setText(getString(R.string.data_input));


        // Getting habit passed from previous activity
        String habit = getIntent().getStringExtra(getString(R.string.habit));
        if (habit == null) {
            // Error handling
            Log.e("Button", getString(R.string.habit_not_passed));
        }
        chartDBhandler dbHandler = new chartDBhandler(Datainput.this);
        ArrayList<chartData> existing_data = dbHandler.getDataByHabit(habit);
        String unit = existing_data.get(0).getUnit();
        String desc = existing_data.get(0).getDescription();
        textInputLayoutValue.setHint(getString(R.string.number_of)+unit);

        // Back button logic
        backbutton.setOnClickListener(v -> {
            Intent back = new Intent(Datainput.this, Chartview.class);
            back.putExtra(getString(R.string.habit),habit);
            startActivity(back);
        });

        // Save data button logic
        saveData.setOnClickListener(v -> {
            Log.d("save",getString(R.string.saved_button_pressed));
            String valueStr = editTextValue.getText().toString();
            if (!valueStr.isEmpty()) {
                try {
                    int value = Integer.parseInt(valueStr);
                    // Get current date in ISO 8601 format
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    chartData data = new chartData(habit, value, currentDate,unit,desc);
                    dbHandler.insertData(data);
                    Log.d("data",getString(R.string.data_inserted));
                    Toast.makeText(getApplicationContext(), getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
                    // Optionally, you can navigate back or clear the input
                   // editTextValue.setText("");
                    Intent gochartview = new Intent(Datainput.this, Chartview.class);
                    gochartview.putExtra(getString(R.string.habit), habit);
                    gochartview.putExtra(getString(R.string.option),"1");
                    Log.d("save",getString(R.string.saved_button_pressed));

                    startActivity(gochartview);
                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid integer
                    Toast.makeText(getApplicationContext(), getString(R.string.valid_number), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.enter_value), Toast.LENGTH_SHORT).show();
            }
        });

    }
}