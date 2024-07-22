package sg.edu.np.mad.p04_team4.Timer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class TimerLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimerAdapter timerAdapter;
    private List<Time> timerLogList;
    private DatabaseReference timerLogRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_log);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timerLogList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            timerLogRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("timer_logs");
        } else {
            finish();
            return;
        }

        loadData();

        timerAdapter = new TimerAdapter(timerLogList, this);
        recyclerView.setAdapter(timerAdapter);
    }

    private void loadData() {
        timerLogRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timerLogList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Time time = dataSnapshot.getValue(Time.class);
                    if (time != null) {
                        timerLogList.add(time);
                    }
                }
                timerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
