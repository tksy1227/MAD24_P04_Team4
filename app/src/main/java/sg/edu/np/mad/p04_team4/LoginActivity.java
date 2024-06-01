package sg.edu.np.mad.p04_team4;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
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

        Toast.makeText(LoginActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();
    }
}

public class CreateAccount extends AppCompatActivity {

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
        DBHandler dbHandler = new DBHandler(this,null, null, 1);

        Button btnLogin = findViewById(R.id.Login);
        Button btnRegister = findViewById(R.id.CreateAccount);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something when Login is pressed.
                EditText etUsername = findViewById(R.id.etUsername);
                EditText etPassword = findViewById(R.id.etPassword);
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                User user = new User(1, "", "");
                user = dbHandler.getUser(username);
                if (username.equals(user.getName()) && password.equals(user.getPassword())) {
                    Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("name", user.getName());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Create Account!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something when Register is pressed.
                EditText etUsername = findViewById(R.id.etUsername);
                EditText etPassword = findViewById(R.id.etPassword);
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                User user = new User(1, "", "");
                user = dbHandler.getUser(username);
                if (username.equals(user.getName()))
                {
                    user.setPassword(password);
                    dbHandler.updateUser(user);
                    Toast.makeText(getApplicationContext(),"User Created. Please Sign In.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    user.setName(username);
                    user.setPassword(password);
                    dbHandler.addUser(user);
                    Toast.makeText(getApplicationContext(),"Create account",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}