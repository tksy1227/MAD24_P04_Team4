package sg.edu.np.mad.p04_team4.AddFriend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;

import java.io.IOException;

public class AcceptDeclineActivity extends AppCompatActivity {

    private LinearLayout requestContainer;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_decline);

        inflater = LayoutInflater.from(this);
        requestContainer = findViewById(R.id.requestContainer);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String inviteId = uri.getQueryParameter("invite_id");
            if (inviteId != null) {
                addInvitationBox(inviteId);
            }
        }

        // Example for testing multiple invitations
        // addInvitationBox("12345");
        // addInvitationBox("67890");

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(AcceptDeclineActivity.this, FriendListActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Home
        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> {
            Intent homeIntent = new Intent(AcceptDeclineActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Friend List
        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> {
            Intent friendIntent = new Intent(AcceptDeclineActivity.this, FriendListActivity.class);
            startActivity(friendIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Account
        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> {
            Intent accountIntent = new Intent(AcceptDeclineActivity.this, AccountActivity.class);
            startActivity(accountIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });
    }

    private void addInvitationBox(String inviteId) {
        View requestView = inflater.inflate(R.layout.request_item, requestContainer, false);

        TextView nameTextView = requestView.findViewById(R.id.name);
        nameTextView.setText("Invite ID: " + inviteId);

        ImageView acceptButton = requestView.findViewById(R.id.accept);
        ImageView declineButton = requestView.findViewById(R.id.decline);

        acceptButton.setOnClickListener(v -> handleInviteResponse(inviteId, true, requestView));
        declineButton.setOnClickListener(v -> handleInviteResponse(inviteId, false, requestView));

        requestContainer.addView(requestView);
    }

    private void handleInviteResponse(String inviteId, boolean accepted, View requestView) {
        new Thread(() -> {
            boolean success = sendPostRequest(inviteId, accepted);
            runOnUiThread(() -> {
                if (success) {
                    if (accepted) {
                        // Send back friend details to FriendListActivity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("name", "New Friend"); // Replace with actual friend's name
                        resultIntent.putExtra("birthday", "January 1, 2000"); // Replace with actual friend's birthday
                        setResult(RESULT_OK, resultIntent);
                    }
                    Toast.makeText(AcceptDeclineActivity.this, "Invitation " + (accepted ? "Accepted" : "Declined"), Toast.LENGTH_SHORT).show();
                    requestContainer.removeView(requestView);
                    if (accepted) {
                        finish(); // End the activity and return to the previous activity
                    }
                } else {
                    Toast.makeText(AcceptDeclineActivity.this, "Failed to process the invitation", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private boolean sendPostRequest(String inviteId, boolean accepted) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String requestBodyStr = "invite_id=" + inviteId + "&accepted=" + accepted;
        RequestBody body = RequestBody.create(requestBodyStr, mediaType);
        Request request = new Request.Builder()
                .url("https://www.yourwebsite.com/api/invite_response")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}