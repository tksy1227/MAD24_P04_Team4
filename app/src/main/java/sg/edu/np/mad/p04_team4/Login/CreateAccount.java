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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad.p04_team4.HomeActivity;
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

                String name = tvName.getText().toString().trim();
                String phone = tvPhone.getText().toString().trim();
                String password = tvPassword.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CreateAccount.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(phone + "@example.com", password) // Using phone as part of email
                        .addOnCompleteListener(CreateAccount.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid();
                                User newUser = new User(userId, name, password, phone); // Ensure phone is stored

                                // Store user with phone field in database
                                mDatabase.child("users").child(userId).setValue(newUser)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Intent intent = new Intent(CreateAccount.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(CreateAccount.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CreateAccount.this, "Database error: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(CreateAccount.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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
