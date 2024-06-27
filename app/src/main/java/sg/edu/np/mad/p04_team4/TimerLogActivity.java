package sg.edu.np.mad.p04_team4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TimerLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimerAdapter timerAdapter;
    private List<Time> timerLogList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_log);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timerLogList = new ArrayList<>();

        databaseHelper = new DatabaseHelper(this);
        loadData();

        timerAdapter = new TimerAdapter(timerLogList, this);
        recyclerView.setAdapter(timerAdapter);
    }

    private void loadData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, DatabaseHelper.COLUMN_DATE + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int timerIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMER);
                int purposeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PURPOSE);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);

                if (timerIndex != -1 && purposeIndex != -1 && dateIndex != -1) {
                    String timer = cursor.getString(timerIndex);
                    String purpose = cursor.getString(purposeIndex);
                    String date = cursor.getString(dateIndex);

                    Time time = new Time(timer, purpose, date);
                    timerLogList.add(time);
                } else {
                    // Log or handle the case where the column index is not found
                    Log.e("TimerLogActivity", "One or more columns not found in the database.");
                }
            }
            cursor.close();
        }

    }
}