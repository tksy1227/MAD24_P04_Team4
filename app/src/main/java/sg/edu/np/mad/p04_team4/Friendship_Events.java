package sg.edu.np.mad.p04_team4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.Html;


import androidx.core.content.ContextCompat;

public class Friendship_Events extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friendship_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        Button b4 = findViewById(R.id.button4);

        User user = new User("John Doe", "MAD Developer",1,true,true,true,true);

        boolean challange_empty = user.challange_e;
        boolean milestone_empty = user.milestone_e;
        boolean goals_empty = user.goals_e;





        if(challange_empty){
            String buttonText = "<b>No challenge set yet!</b><br><br>Click here to set it for yourself or friends";
            b1.setText(Html.fromHtml(buttonText));
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent set_challange= new Intent(Friendship_Events.this, TEST.class);
                    set_challange.putExtra("user", user);
                    startActivity(set_challange);
                }
            });
        }
        else if(!challange_empty){
            int color = ContextCompat.getColor(this, R.color.teal_700);
            b1.setBackgroundColor(color);
            //get challange information and display

        }
        if(milestone_empty){
            String buttonText = "<b>No milestone set yet!</b><br><br>Click here to set it for yourself or friends";
            b2.setText(Html.fromHtml(buttonText));
        }
        else if(!milestone_empty){


        }
        if(goals_empty){
            String buttonText = "<b>No goals set yet!</b><br><br>Click here to set it for yourself and friends";
            b3.setText(Html.fromHtml(buttonText));
        }
        else if(!goals_empty){


        }

    }
}