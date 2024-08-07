package sg.edu.np.mad.p04_team4.Timer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.AddFriend.FriendListActivity;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;

public class TimerLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimerAdapter timerAdapter;
    private List<Time> timerLogList;
    private DatabaseHelper databaseHelper;
    private Button deleteAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_log);

        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timerLogList = new ArrayList<>();

        // Initialize your adapter with the timerLogList
        timerAdapter = new TimerAdapter(timerLogList, this);
        recyclerView.setAdapter(timerAdapter);

        // Initialize the database helper and load data
        databaseHelper = new DatabaseHelper(this);
        loadData();

        // Initialize the back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Close the current activity and return to the previous one

        // Initialize the delete all button
        deleteAllButton = findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(v -> {
            new AlertDialog.Builder(TimerLogActivity.this)
                    .setTitle(getString(R.string.clear_all_timer))
                    .setMessage(getString(R.string.check_clear_all_timer))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Clear all items
                        timerAdapter.clearAllItems();
                        // Optionally, clear from the database as well
                        databaseHelper.clearAllTimers();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        // Attach the SwipeToDeleteCallback to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(timerAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Navigation buttons
        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> navigateToActivity(HomeActivity.class));

        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> navigateToActivity(FriendListActivity.class));

        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> navigateToActivity(AccountActivity.class));
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(TimerLogActivity.this, activityClass);
        startActivity(intent);
        // Optionally, finish this activity if you want to prevent the user from returning
        finish();
    }

    private void loadData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_DURATION,
                DatabaseHelper.COLUMN_PURPOSE,
                DatabaseHelper.COLUMN_DATE
        };

        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION));
                String purpose = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PURPOSE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));

                timerLogList.add(new Time(id, duration, purpose, date));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        // Notify adapter about the loaded data
        timerAdapter.notifyDataSetChanged();
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_delete));
        builder.setPositiveButton(getString(R.string.check_yes), (dialog, which) -> deleteItem(position));
        builder.setNegativeButton(getString(R.string.check_no), (dialog, which) -> {
            timerAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void deleteItem(int position) {
        // Get item ID and delete from the database
        long itemId = timerAdapter.getItemId(position);
        databaseHelper.deleteTimer(itemId);

        // Remove item from adapter and notify
        timerAdapter.removeItem(position);
    }
}
