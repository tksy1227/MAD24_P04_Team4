package sg.edu.np.mad.p04_team4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_page); // Assuming you have a separate layout for creating an account
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBHandler dbHandler = new DBHandler(this, null, null, 1);

        Button btnCreateAccount = findViewById(R.id.CreateAccountButton);

        btnCreateAccount.setOnClickListener(v -> {
            EditText etUsername = findViewById(R.id.etUsername);
            EditText etPassword = findViewById(R.id.etPassword);
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            User user = dbHandler.getUser(username);

            if (user != null) {
                user.setPassword(password);
                dbHandler.updateUser(user);
                Toast.makeText(getApplicationContext(), "User already exists. Password updated. Please sign in.", Toast.LENGTH_SHORT).show();
            } else {
                user = new User(0, username, password); // 0 as a placeholder ID; should be auto-incremented by the database
                dbHandler.addUser(user);
                Toast.makeText(getApplicationContext(), "Account created. Please sign in.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
