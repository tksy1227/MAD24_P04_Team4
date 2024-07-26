package sg.edu.np.mad.p04_team4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.R;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Display user information
            TextView nameTextView = findViewById(R.id.Name);
            nameTextView.setText("Name: " + currentUser.getDisplayName());
        } else {
            Log.e(TAG, "No user is currently signed in.");
            // Handle the case when there is no user signed in
        }

        RelativeLayout ForgetPasswordRL = findViewById(R.id.ChangePassword);
        Intent ForgetPassword = new Intent(AccountActivity.this, ForgetUserActivity.class);

        RelativeLayout AccountDetailsRL = findViewById(R.id.AccountDetails);
        Intent AccountDetails = new Intent (AccountActivity.this, AccntDetailActivity.class);

    }
}
