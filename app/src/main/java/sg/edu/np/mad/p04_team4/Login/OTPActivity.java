package sg.edu.np.mad.p04_team4.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.p04_team4.R;

public class OTPActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private TextView resendBtn;
    private DatabaseReference mDatabase;
    private String generatedOTP;
    private boolean ResendEnabled = false;
    private int resendTime = 60;
    private int selectedETPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.otp);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        otp1 = findViewById(R.id.OTP1);
        otp2 = findViewById(R.id.OTP2);
        otp3 = findViewById(R.id.OTP3);
        otp4 = findViewById(R.id.OTP4);
        resendBtn = findViewById(R.id.Resend);
        final Button verifyBtn = findViewById(R.id.Verify);

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);

        ShowKeyboard(otp1);
        StartCountDown();
        sendOTP(); // Send OTP when activity is created

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ResendEnabled) {
                    sendOTP();
                    StartCountDown();
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOTP = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
                if (enteredOTP.length() == 4) {
                    String userId = getIntent().getStringExtra("userId");
                    verifyOTP(userId, enteredOTP);
                } else {
                    Toast.makeText(OTPActivity.this, getString(R.string.enter_valid_otp), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendOTP() {
        generatedOTP = generateOTP();
        String userId = getIntent().getStringExtra("userId");
        mDatabase.child(userId).child("otp").setValue(generatedOTP);

        // For demonstration purposes, show the OTP in a Toast message
        Toast.makeText(this, "OTP: " + generatedOTP, Toast.LENGTH_LONG).show();
    }

    private String generateOTP() {
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomPIN);
    }

    private void verifyOTP(String userId, String enteredOTP) {
        mDatabase.child(userId).child("otp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedOTP = dataSnapshot.getValue(String.class);
                    if (storedOTP.equals(enteredOTP)) {
                        Intent intent = new Intent(OTPActivity.this, ForgetPasswordConfirmationActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OTPActivity.this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OTPActivity.this, getString(R.string.otp_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OTPActivity.this, getString(R.string.database_error) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (selectedETPosition == 0) {
                    selectedETPosition = 1;
                    ShowKeyboard(otp2);
                } else if (selectedETPosition == 1) {
                    selectedETPosition = 2;
                    ShowKeyboard(otp3);
                } else if (selectedETPosition == 2) {
                    selectedETPosition = 3;
                    ShowKeyboard(otp4);
                }
            }
        }
    };

    private void ShowKeyboard(EditText otp) {
        otp.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otp, InputMethodManager.SHOW_IMPLICIT);
    }

    private void StartCountDown() {
        ResendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText(getString(R.string.resend_code) + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                ResendEnabled = true;
                resendBtn.setText(getString(R.string.resend_code2));
                resendBtn.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (selectedETPosition == 3) {
                selectedETPosition = 2;
                ShowKeyboard(otp3);
            } else if (selectedETPosition == 2) {
                selectedETPosition = 1;
                ShowKeyboard(otp2);
            } else if (selectedETPosition == 1) {
                selectedETPosition = 0;
                ShowKeyboard(otp1);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}
