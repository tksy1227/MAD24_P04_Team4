package sg.edu.np.mad.p04_team4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ForgetUserActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    private EditText userInput; // Assuming you have an EditText for user input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password_number);

        // Initialize DBHandler
        dbHandler = new DBHandler(this);

        // Assuming you have an EditText with id editTextPhone in your layout
        userInput = findViewById(R.id.editTextPhone);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Example: Button click to retrieve user based on input
        Button retrieveID = findViewById(R.id.Send);
        retrieveID.setOnClickListener(view -> retrieveUser());
    }

    private void retrieveUser() {
        String usernameString = userInput.getText().toString();

        int username;
        try {
            username = Integer.parseInt(usernameString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call your DBHandler to get user based on username input
        ArrayList<User> userArrayList = dbHandler.getUsers();

        User foundUser = null;
        for (User user : userArrayList) {
            if (user.getID() == username) {
                foundUser = user;
                break;
            }
        }

        if (foundUser != null) {
            // User found
            // OTP Verification
            Toast.makeText(this, "User found: " + foundUser.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent (ForgetUserActivity.this, OTPActivity.class);
            intent.putExtra("user ID", username);
            startActivity(intent);

        } else {
            // User not found
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }
}