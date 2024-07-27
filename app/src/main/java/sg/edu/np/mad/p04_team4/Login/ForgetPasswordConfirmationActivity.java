package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.p04_team4.R;

public class ForgetPasswordConfirmationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "ForgetPasswordConfirm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database and Auth
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Retrieve userId from intent extras
        String userId = getIntent().getStringExtra("userId");

        EditText password = findViewById(R.id.TextPassword);
        EditText passwordConfirm = findViewById(R.id.TextPasswordConfirm);

        // Password change logic
        Button changeButton = findViewById(R.id.Change);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = password.getText().toString();
                String newPasswordConfirm = passwordConfirm.getText().toString();

                if (newPassword.equals(newPasswordConfirm)) {
                    // Perform password change operation using userId
                    changePassword(userId, newPassword);
                } else {
                    Toast.makeText(ForgetPasswordConfirmationActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword(String userId, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update password in Firebase Realtime Database
                    mDatabase.child(userId).child("password").setValue(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(ForgetPasswordConfirmationActivity.this, getString(R.string.password_success_change), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgetPasswordConfirmationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Close current activity
                        } else {
                            String error = task1.getException() != null ? task1.getException().getMessage() : "Unknown error";
                            Log.e(TAG, "Database update failed: " + error);
                            Toast.makeText(ForgetPasswordConfirmationActivity.this, getString(R.string.password_change_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Log.e(TAG, "Password update failed: " + error);
                    Toast.makeText(ForgetPasswordConfirmationActivity.this, getString(R.string.password_change_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "User not authenticated.");
            Toast.makeText(ForgetPasswordConfirmationActivity.this, getString(R.string.user_not_authenticated2), Toast.LENGTH_SHORT).show();
        }
    }
}
