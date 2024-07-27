package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sg.edu.np.mad.p04_team4.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private User currentUser; // Assuming you have the current user loaded somehow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        EditText passwordEditText = findViewById(R.id.TextPassword);
        EditText confirmPasswordEditText = findViewById(R.id.TextPasswordConfirm);

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent Intent = new Intent(ForgetPasswordActivity.this, PasswordOTPActivity.class);
            startActivity(Intent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        Button changeButton = findViewById(R.id.Change);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = passwordEditText.getText().toString();
                String newPasswordConfirm = confirmPasswordEditText.getText().toString();

                if (newPassword.equals(newPasswordConfirm)) {
                    // Check if current password matches
                    // For demo purpose, compare with a fixed password
                    String currentPassword = "current_password"; // Replace with actual current password retrieval logic

                    if (currentUser.comparePassword(currentPassword)) {
                        // Perform password change operation
                        currentUser.setPassword(newPassword); // Update user's password
                        // Call method to save or update user details (e.g., in a database)

                        Toast.makeText(ForgetPasswordActivity.this, "Password successfully changed.", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity or navigate back
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Current password incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
