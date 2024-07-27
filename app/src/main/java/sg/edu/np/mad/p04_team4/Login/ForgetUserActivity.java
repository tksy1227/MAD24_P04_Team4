package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        String phoneString = userInput.getText().toString().trim();

        // Validate input
        if (phoneString.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_valid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ForgetUserActivity", "Querying for phone number: " + phoneString);

        // Query Firebase to get user based on phone number input
        mDatabase.orderByChild("phone").equalTo(phoneString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("ForgetUserActivity", "Data snapshot exists");
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User foundUser = userSnapshot.getValue(User.class);
                        if (foundUser != null) {
                            Log.d("ForgetUserActivity", "User found: " + foundUser.getName());
                            // User found
                            Toast.makeText(ForgetUserActivity.this, getString(R.string.user_found) + foundUser.getName(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgetUserActivity.this, OTPActivity.class);
                            intent.putExtra("userId", foundUser.getId());
                            startActivity(intent);
                        } else {
                            Log.d("ForgetUserActivity", "User is null");
                        }
                    }
                } else {
                    // User not found
                    Log.d("ForgetUserActivity", "User not found");
                    Toast.makeText(ForgetUserActivity.this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.d("ForgetUserActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(ForgetUserActivity.this, getString(R.string.database_error) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
