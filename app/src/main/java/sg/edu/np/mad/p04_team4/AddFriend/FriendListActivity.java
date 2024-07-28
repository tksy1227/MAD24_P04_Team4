package sg.edu.np.mad.p04_team4.AddFriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;

public class FriendListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;
    private static final int REQUEST_CODE_INVITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize friend list and adapter
        friendList = new ArrayList<>();

        // For demo purposes, adding sample data
        friendList.add(new Friend("John Doe", "January 1, 1990"));
        friendList.add(new Friend("Jane Smith", "February 14, 1995"));

        friendAdapter = new FriendAdapter(friendList);
        recyclerView.setAdapter(friendAdapter);

        RelativeLayout shareButton = findViewById(R.id.ShareButton);
        shareButton.setOnClickListener(v -> shareInviteLink());

        // Optionally, initialize or load other data here

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(FriendListActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Home
        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> {
            Intent homeIntent = new Intent(FriendListActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Friend List
        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> {
            Intent friendIntent = new Intent(FriendListActivity.this, FriendListActivity.class);
            startActivity(friendIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Account
        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> {
            Intent accountIntent = new Intent(FriendListActivity.this, AccountActivity.class);
            startActivity(accountIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });
    }

    private void shareInviteLink() {
        String inviteLink = "https://www.yourwebsite.com/invite?user_id=123"; // Replace with your invite link
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,  getString(R.string.join_me_on_friendscape) + inviteLink);
        startActivity(Intent.createChooser(shareIntent,  getString(R.string.share_link_via)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INVITE && resultCode == RESULT_OK) {
            if (data != null) {
                String name = data.getStringExtra("name");
                String birthday = data.getStringExtra("birthday");
                if (name != null && birthday != null) {
                    // Add the new friend to the list and update the adapter
                    friendList.add(new Friend(name, birthday));
                    friendAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, getString(R.string.invite_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}