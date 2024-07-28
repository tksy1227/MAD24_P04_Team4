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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import sg.edu.np.mad.p04_team4.R;

public class PasswordOTPActivity extends AppCompatActivity {

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (selectedETPosition == 0) {

                    selectedETPosition = 1;
                    ShowKeyboard(otp2);

                } else if (selectedETPosition == 1) {

                    selectedETPosition = 2;
                    ShowKeyboard(otp3);

                } else if (selectedETPosition == 3) {

                    selectedETPosition = 1;
                    ShowKeyboard(otp4);

                }
            }
        }
    };

    private EditText otp1, otp2, otp3, otp4;
    private TextView resendBtn;

    // true after every 60 secs
    private boolean ResendEnabled = false;

    // resend otp in 60 seconds
    private int resendTime = 60;

    private int selectedETPosition = 0;

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

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PasswordOTPActivity.this, ForgetUserActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        otp1 = findViewById(R.id.OTP1);
        otp2 = findViewById(R.id.OTP2);
        otp3 = findViewById(R.id.OTP3);
        otp4 = findViewById(R.id.OTP4);

        resendBtn = findViewById(R.id.Resend);
        final Button VerifyBtn = findViewById(R.id.Verify);

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);

        // by default open keyboard at otp1
        ShowKeyboard(otp1);

        // start resend count down timer
        StartCountDown();

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ResendEnabled) {
                    // handle resend code

                    // start new resend count down timer
                    StartCountDown();
                }
            }
        });

        VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String generateOTP = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();

                if (generateOTP.length() == 4) {
                    // otp verification
                }
                // Retrieve userId from intent extras
                String userId = getIntent().getStringExtra("userId");

                // Your OTP verification logic here...

                // If OTP verification is successful, proceed to password change activity
                Intent intent = new Intent(PasswordOTPActivity.this, ForgetPasswordActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    private void ShowKeyboard(EditText otp) {
        otp.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otp, InputMethodManager.SHOW_IMPLICIT);
    }


    private void StartCountDown() {
        ResendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 60, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText(getString(R.string.resend_code) + (millisUntilFinished / 60) + ")");
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
