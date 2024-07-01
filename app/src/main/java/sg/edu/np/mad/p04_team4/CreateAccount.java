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

public class CreateAccount extends AppCompatActivity {

    private boolean passwordShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBHandler dbHandler = new DBHandler(this);
        Intent myRecvIntent = getIntent();
        String name = myRecvIntent.getStringExtra("name");

        Button btnRegister = findViewById(R.id.SignUp);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tvUsername = findViewById(R.id.TextPhoneNumber);
                String usernameString = tvUsername.getText().toString();

                EditText tvName = findViewById(R.id.TextName);
                EditText tvPassword= findViewById(R.id.TextPassword);
                ImageView btnHideIcon = findViewById(R.id.HideIcon);

                btnHideIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (passwordShowing) {
                            passwordShowing = false;
                            tvPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            btnHideIcon.setImageResource(R.drawable.hide_icon);
                        } else {
                            passwordShowing = true;
                            tvPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnHideIcon.setImageResource(R.drawable.unhide_icon);
                        }
                        tvPassword.setSelection(tvPassword.length());
                    }
                });

                int username;
                try {
                    username = Integer.parseInt(usernameString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }
                String name = tvName.getText().toString();
                String password = tvPassword.getText().toString();

                User user = new User(username, name, password);
                dbHandler.addUser(user);
                Intent intent = new Intent(CreateAccount.this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(CreateAccount.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnLogin = findViewById(R.id.Login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
