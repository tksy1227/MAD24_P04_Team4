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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loginpage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBHandler dbHandler = new DBHandler(this, null, null, 1);

        Button btnLogin = findViewById(R.id.Login);
        Button btnRegister = findViewById(R.id.CreateAccount);

        btnLogin.setOnClickListener(v -> {
            EditText etUsername = findViewById(R.id.etUsername);
            EditText etPassword = findViewById(R.id.etPassword);
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            User user = dbHandler.getUser(username);

            if (user != null && username.equals(user.getName()) && password.equals(user.getPassword())) {
                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Update this to the appropriate Activity
                intent.putExtra("name", user.getName());
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Invalid credentials or user does not exist. Please register.", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
            startActivity(intent);
        });
    }
}
