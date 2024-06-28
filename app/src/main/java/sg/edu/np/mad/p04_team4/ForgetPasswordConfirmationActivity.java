package sg.edu.np.mad.p04_team4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgetPasswordConfirmationActivity extends AppCompatActivity {

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

        // Retrieve userId from intent extras
        String userId = getIntent().getStringExtra("userId");

        EditText password = findViewById(R.id.TextPassword);
        EditText passwordConfirm = findViewById(R.id.TextPasswordConfirm);

        // Example password change logic
        Button changeButton = findViewById(R.id.Change);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = password.getText().toString();
                String newPasswordConfirm = passwordConfirm.getText().toString();

                if (newPassword.equals(newPasswordConfirm)) {
                    // Perform password change operation using userId
                    // Example:
                    // changePassword(userId, newPassword);


                    Toast.makeText(ForgetPasswordConfirmationActivity.this, "Password successfully changed.", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(ForgetPasswordConfirmationActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


