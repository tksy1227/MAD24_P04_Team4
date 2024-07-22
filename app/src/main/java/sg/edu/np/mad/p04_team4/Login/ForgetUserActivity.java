package sg.edu.np.mad.p04_team4.Login;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.p04_team4.R;

public class ForgetUserActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText userInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password_number);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Assuming you have an EditText with id editTextPhone in your layout
        userInput = findViewById(R.id.editTextPhone);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button click to retrieve user based on input
        Button retrieveID = findViewById(R.id.Send);
        retrieveID.setOnClickListener(view -> retrieveUser());
    }

    private void retrieveUser() {
        String usernameString = userInput.getText().toString();

        // Validate input
        if (usernameString.isEmpty()) {
            Toast.makeText(this, "Please enter a valid username.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firebase to get user based on username input
        mDatabase.orderByChild("name").equalTo(usernameString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User foundUser = userSnapshot.getValue(User.class);
                        if (foundUser != null) {
                            // User found
                            Toast.makeText(ForgetUserActivity.this, "User found: " + foundUser.getName(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgetUserActivity.this, OTPActivity.class);
                            intent.putExtra("userId", foundUser.getId().toString());
                            startActivity(intent);
                        }
                    }
                } else {
                    // User not found
                    Toast.makeText(ForgetUserActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(ForgetUserActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
