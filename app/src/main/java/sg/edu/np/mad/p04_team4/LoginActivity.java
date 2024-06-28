package sg.edu.np.mad.p04_team4;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordShowing = false;

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

        DBHandler dbHandler = new DBHandler(this);

        Button btnLogin = findViewById(R.id.Login);
        Button btnRegister = findViewById(R.id.CreateAccount);
        Button btnForgotPassword = findViewById(R.id.ForgotPassword);
        ImageView btnHideIcon = findViewById(R.id.HideIcon);
        EditText password = findViewById(R.id.TextPassword);

        btnHideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowing) {
                    passwordShowing = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnHideIcon.setImageResource(R.drawable.unhide_icon);
                } else {
                    passwordShowing = true;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnHideIcon.setImageResource(R.drawable.hide_icon);
                }
                password.setSelection(password.length());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etUsername = findViewById(R.id.TextPhoneNumber);
                EditText etPassword = findViewById(R.id.TextPassword);
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                User user = dbHandler.getUser(username);

                if (user != null && username.equals(user.getName()) && password.equals(user.getPassword())) {
                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent home_page = new Intent(LoginActivity.this, HomeActivity.class);
                    home_page.putExtra("name", user.getName());
                    home_page.putExtra("user", user);
                    startActivity(home_page);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid credentials or user does not exist. Please register.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
