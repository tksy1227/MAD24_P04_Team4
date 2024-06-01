package sg.edu.np.mad.p04_team4;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize chat list with sample data
        chatList = new ArrayList<>();
        chatList.add(new Chat("MAD Team 4", "Alright", "01:01"));
        chatList.add(new Chat("HUEHUEHUEHUE", "2024_Apr_DW_ASG1_starter (1).ipynb", "Fri"));
        // Add more chat items as needed

        chatAdapter = new ChatAdapter(this, chatList);
        recyclerView.setAdapter(chatAdapter);
    }
}
