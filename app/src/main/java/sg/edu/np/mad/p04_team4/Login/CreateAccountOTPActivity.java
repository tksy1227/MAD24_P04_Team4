package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.p04_team4.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class CreateAccountOTPActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private TextView resendBtn;
    private boolean resendEnabled = false;
    private final int resendTime = 60; // Resend time in seconds
    private String verificationId;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.password_otp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        otp1 = findViewById(R.id.OTP1);
        otp2 = findViewById(R.id.OTP2);
        otp3 = findViewById(R.id.OTP3);
        otp4 = findViewById(R.id.OTP4);
        resendBtn = findViewById(R.id.Resend);
        Button verifyBtn = findViewById(R.id.Verify);

        otp1.requestFocus(); // Request focus on first OTP field

        // TextWatcher to move focus to next OTP field automatically
        TextWatcher otpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (otp1.isFocused()) {
                        otp2.requestFocus();
                    } else if (otp2.isFocused()) {
                        otp3.requestFocus();
                    } else if (otp3.isFocused()) {
                        otp4.requestFocus();
                    }
                }
            }
        };

        otp1.addTextChangedListener(otpTextWatcher);
        otp2.addTextChangedListener(otpTextWatcher);
        otp3.addTextChangedListener(otpTextWatcher);
        otp4.addTextChangedListener(otpTextWatcher);

        // Retrieve verification ID from previous activity
        verificationId = getIntent().getStringExtra("verificationId");

        // Start the countdown for resend OTP
        startCountDown();

        resendBtn.setOnClickListener(v -> {
            if (resendEnabled) {
                // Handle resend OTP code here
                Toast.makeText(CreateAccountOTPActivity.this, "Resending OTP...", Toast.LENGTH_SHORT).show();
                // Restart countdown for resend
                startCountDown();
            }
        });

        verifyBtn.setOnClickListener(v -> {
            String otp = otp1.getText().toString().trim() +
                    otp2.getText().toString().trim() +
                    otp3.getText().toString().trim() +
                    otp4.getText().toString().trim();

            if (otp.length() == 4) {
                // Perform OTP verification
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(CreateAccountOTPActivity.this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCountDown() {
        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000")); // Gray color for disabled state

        new CountDownTimer(resendTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                resendBtn.setText("Resend Code (" + secondsRemaining + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText("Resend Code");
                resendBtn.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary)); // Reset to default color
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, proceed to account creation
                FirebaseUser user = task.getResult().getUser();
                Toast.makeText(CreateAccountOTPActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();

                // Retrieve user details from Intent extras
                String name = getIntent().getStringExtra("name");
                String phoneNumber = getIntent().getStringExtra("phone number");
                String password = getIntent().getStringExtra("password");

                // Example: Create user in Firebase Realtime Database
                createUserInDatabase(user.getUid(), name, phoneNumber, password);

                // Example: Move to HomeActivity or main activity
                Intent intent = new Intent(CreateAccountOTPActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Finish this activity to prevent going back
            } else {
                // Sign in failed
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid OTP code
                    Toast.makeText(CreateAccountOTPActivity.this, "Invalid OTP code", Toast.LENGTH_SHORT).show();
                } else {
                    // Other errors
                    Toast.makeText(CreateAccountOTPActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUserInDatabase(String userId, String name, String phoneNumber, String password) {
        // Example: Save user details in Firebase Realtime Database
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phoneNumber", phoneNumber);
        user.put("password", password);

        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // User data saved successfully
                    Toast.makeText(CreateAccountOTPActivity.this, "User created in database", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error saving user data
                    Toast.makeText(CreateAccountOTPActivity.this, "Failed to create user in database", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (otp4.isFocused()) {
                otp4.setText("");
                otp3.requestFocus();
            } else if (otp3.isFocused()) {
                otp3.setText("");
                otp2.requestFocus();
            } else if (otp2.isFocused()) {
                otp2.setText("");
                otp1.requestFocus();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void showKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
