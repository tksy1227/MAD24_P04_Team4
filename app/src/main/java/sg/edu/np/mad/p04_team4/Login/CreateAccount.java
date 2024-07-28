package sg.edu.np.mad.p04_team4.Login;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.p04_team4.R;

public class CreateAccount extends AppCompatActivity {

    private boolean passwordShowing = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_account);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnRegister = findViewById(R.id.SignUp);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tvName = findViewById(R.id.TextName);
                EditText tvPhone = findViewById(R.id.TextPhoneNumber);
                EditText tvPassword = findViewById(R.id.TextPassword);
                ImageView btnHideIcon = findViewById(R.id.HideIcon);

                btnHideIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (passwordShowing) {
                            // Hide the password
                            passwordShowing = false;
                            tvPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            btnHideIcon.setImageResource(R.drawable.hide_icon);
                        } else {
                            // Show the password
                            passwordShowing = true;
                            tvPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnHideIcon.setImageResource(R.drawable.unhide_icon);
                        }
                        // Move cursor to the end of the password text
                        tvPassword.setSelection(tvPassword.length());
                    }
                });

                String name = tvName.getText().toString().trim();
                String phone = tvPhone.getText().toString().trim();
                String password = tvPassword.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CreateAccount.this, getString(R.string.fill_out_all_fields), Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // Move to OTP Page
                    Intent intent = new Intent(CreateAccount.this, CreateAccountOTPActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("phone number", phone);
                    intent.putExtra("password", password);

                    startActivity(intent);
                }
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
