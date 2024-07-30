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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordShowing = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loginpage);

        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etPhone = findViewById(R.id.TextPhoneNumber);
        EditText etPassword = findViewById(R.id.TextPassword);
        Button btnLogin = findViewById(R.id.LoginBtn);
        Button btnRegister = findViewById(R.id.CreateAccount);
        Button btnForgotPassword = findViewById(R.id.ForgotPassword);
        ImageView btnHideIcon = findViewById(R.id.HideIcon);

        // Password visibility toggle
        btnHideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowing) {
                    passwordShowing = false;
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnHideIcon.setImageResource(R.drawable.hide_icon);
                } else {
                    passwordShowing = true;
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnHideIcon.setImageResource(R.drawable.unhide_icon);
                }
                etPassword.setSelection(etPassword.length());
            }
        });

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.fill_out_all_fields), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signOut();  // Sign out any existing user
                // Sign in with Firebase Auth using the unique email created during account creation
                String email = phone + "@example.com";
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Login successful
                                    Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    homeIntent.putExtra("userId", user.getUid());
                                    homeIntent.putExtra("email", user.getEmail());
                                    homeIntent.putExtra("name", user.getDisplayName());
                                    startActivity(homeIntent);
                                    finish(); // Finish LoginActivity so user cannot go back
                                }
                            } else {
                                // Handle sign-in failures
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Incorrect password
                                    Toast.makeText(getApplicationContext(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
                                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    // No account with this phone number
                                    Toast.makeText(getApplicationContext(), getString(R.string.user_no_exist), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.authentication_failed) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        // Forgot password button click listener
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
