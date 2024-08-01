package sg.edu.np.mad.p04_team4.Calender;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class EventActivity extends AppCompatActivity {
    private List<Event> eventList;
    private EventAdapter adapter;
    private RecyclerView recyclerView;
    private AppDatabase db;
    private long selectedDate;
    private ImageButton backButton;
    private boolean viewMonth = true; // Default view is month

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        db = AppDatabase.getInstance(this);
        eventList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(eventList, db, this::loadEvents, recyclerView);
        recyclerView.setAdapter(adapter);

        Spinner viewModeSpinner = findViewById(R.id.viewModeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.view_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewModeSpinner.setAdapter(adapter);

        viewModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewMonth = position == 0; // 0 is Month, 1 is Week
                loadEvents(); // Reload events with the new view
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        selectedDate = getIntent().getLongExtra("selectedDate", -1);
        loadEvents();
    }

    private void loadEvents() {
        new LoadEventsTask().execute(selectedDate);
    }

    private class LoadEventsTask extends AsyncTask<Long, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Long... params) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(params[0]);

            long start, end;
            if (viewMonth) {
                // Set to the start of the month
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                start = calendar.getTimeInMillis();

                // Set to the start of the next month
                calendar.add(Calendar.MONTH, 1);
                end = calendar.getTimeInMillis();
            } else {
                // Set to the start of the week (assuming Sunday as the first day)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                start = calendar.getTimeInMillis();

                // Set to the start of the next week
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                end = calendar.getTimeInMillis();
            }

            return db.scheduleDao().getEventsForDate(start, end);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            eventList.clear();
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        }
    }
}