package sg.edu.np.mad.p04_team4;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class TEST extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent set_challange = getIntent();
        User user = (User) set_challange.getSerializableExtra("user");
        TextInputEditText Text = findViewById(R.id.input);
        Button enter = findViewById(R.id.button);
        String text2 = Text.getText().toString();
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent challange_made= new Intent(TEST.this,Friendship_Events.class);
                user.challange_e=false;
                challange_made.putExtra("user", user);
                challange_made.putExtra("challange",text2);
                startActivity(challange_made);
            }
        });
    }



}