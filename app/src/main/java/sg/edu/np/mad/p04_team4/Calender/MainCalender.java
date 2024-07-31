package sg.edu.np.mad.p04_team4.Calender;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.AddFriend.FriendListActivity;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;

public class MainCalender extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView selectedDateTextView, eventListTextView;
    private EditText editTextEvent;
    private Button buttonStartTime, buttonEndTime, buttonAdd, buttonStartDate, buttonEndDate, buttonViewEvents;
    private Switch switchAllDay;
    private long selectedDateInMillis;
    private Calendar startTime, endTime, startDate, endDate;
    private AppDatabase db;
    private RecyclerView eventListRecyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_calender);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = AppDatabase.getInstance(this);

        calendarView = findViewById(R.id.calendarView);
        switchAllDay = findViewById(R.id.switchAllDay);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        editTextEvent = findViewById(R.id.editTextEvent);
        buttonStartTime = findViewById(R.id.buttonStartTime);
        buttonEndTime = findViewById(R.id.buttonEndTime);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);
        buttonViewEvents = findViewById(R.id.buttonViewEvents);
        eventListTextView = findViewById(R.id.eventListTextView);
        eventListRecyclerView = findViewById(R.id.eventListRecyclerView);

        // Initialize start and end times and dates
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        // Set default date to current date
        currentCalendar = Calendar.getInstance();
        selectedDateInMillis = System.currentTimeMillis();
        updateSelectedDateTextView(selectedDateInMillis);

        // Set up RecyclerView
        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList, db, this::loadEvents, eventListRecyclerView);
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventListRecyclerView.setAdapter(adapter);

        // Listener for date changes
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);
            selectedDateInMillis = selectedDate.getTimeInMillis();
            updateSelectedDateTextView(selectedDateInMillis);
            updateEventListTextView();
            loadEvents();

            // Set default start and end dates to the selected date
            startDate.setTimeInMillis(selectedDateInMillis);
            endDate.setTimeInMillis(selectedDateInMillis);
            updateButtonDate(buttonStartDate, startDate);
            updateButtonDate(buttonEndDate, endDate);
        });

        switchAllDay.setChecked(true);  // Set switch to be on by default

        switchAllDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            findViewById(R.id.timePickerLayout).setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        buttonStartTime.setOnClickListener(v -> {
            showTimePickerDialog(startTime, (calendar) -> {
                startTime = calendar;
                updateButtonTime(buttonStartTime, calendar);
            });
        });

        buttonEndTime.setOnClickListener(v -> {
            showTimePickerDialog(endTime, (calendar) -> {
                endTime = calendar;
                updateButtonTime(buttonEndTime, calendar);
            });
        });

        buttonStartDate.setOnClickListener(v -> {
            showDatePickerDialog(startDate, (calendar) -> {
                startDate = calendar;
                updateButtonDate(buttonStartDate, calendar);
            });
        });

        buttonEndDate.setOnClickListener(v -> {
            showDatePickerDialog(endDate, (calendar) -> {
                endDate = calendar;
                updateButtonDate(buttonEndDate, calendar);
            });
        });

        buttonAdd.setOnClickListener(v -> {
            String eventTitle = editTextEvent.getText().toString();
            if (!eventTitle.isEmpty()) {
                if (switchAllDay.isChecked()) {
                    startTime.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), 0, 0);
                    endTime.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), 23, 59);
                } else {
                    startTime.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                    endTime.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                }

                if (startTime.before(endTime)) {
                    new InsertEventTask().execute(new Event(eventTitle, selectedDateInMillis, startTime.getTimeInMillis(), endTime.getTimeInMillis()));
                } else {
                    Toast.makeText(MainCalender.this, getString(R.string.ensure_details), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainCalender.this, getString(R.string.enter_event_title), Toast.LENGTH_SHORT).show();
            }
        });

        buttonViewEvents.setOnClickListener(v -> {
            Intent intent = new Intent(MainCalender.this, EventActivity.class);
            intent.putExtra("selectedDate", selectedDateInMillis);
            startActivity(intent);
        });

        // Load events for the initial selected date
        updateEventListTextView();
        loadEvents();

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(MainCalender.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Home
        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> {
            Intent homeIntent = new Intent(MainCalender.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Friend List
        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> {
            Intent friendIntent = new Intent(MainCalender.this, FriendListActivity.class);
            startActivity(friendIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Account
        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> {
            Intent accountIntent = new Intent(MainCalender.this, AccountActivity.class);
            startActivity(accountIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load today's events when returning to this activity
        selectedDateInMillis = System.currentTimeMillis();
        updateSelectedDateTextView(selectedDateInMillis);
        updateEventListTextView();
        loadEvents();
    }

    private void updateSelectedDateTextView(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault());
        selectedDateTextView.setText(sdf.format(dateInMillis));
    }

    private void updateEventListTextView() {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(selectedDateInMillis);

        if (isSameDay(selectedDate, currentCalendar)) {
            eventListTextView.setText(getString(R.string.today_event));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
            eventListTextView.setText(sdf.format(selectedDateInMillis) + getString(R.string.events_jointext));
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void loadEvents() {
        new LoadEventsTask().execute(selectedDateInMillis);
    }

    private void showTimePickerDialog(Calendar initialCalendar, DateTimePickerCallback callback) {
        new TimePickerDialog(MainCalender.this, (view, hourOfDay, minute) -> {
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            newCalendar.set(Calendar.MINUTE, minute);
            callback.onDateTimePicked(newCalendar);
        }, initialCalendar.get(Calendar.HOUR_OF_DAY), initialCalendar.get(Calendar.MINUTE), true).show();
    }

    private void showDatePickerDialog(Calendar initialCalendar, DateTimePickerCallback callback) {
        new DatePickerDialog(MainCalender.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.set(year, monthOfYear, dayOfMonth);
            callback.onDateTimePicked(newCalendar);
        }, initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateButtonTime(Button button, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        button.setText(sdf.format(calendar.getTime()));
    }

    private void updateButtonDate(Button button, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        button.setText(sdf.format(calendar.getTime()));
    }

    private class InsertEventTask extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... events) {
            db.scheduleDao().insert(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainCalender.this, getString(R.string.events_added_successfully), Toast.LENGTH_SHORT).show();
            editTextEvent.setText("");
            buttonStartTime.setText(getString(R.string.start_time_text));
            buttonEndTime.setText(getString(R.string.end_time_text));
            startTime = Calendar.getInstance();
            endTime = Calendar.getInstance();
            updateEventListTextView();
            loadEvents();
        }
    }

    private class LoadEventsTask extends AsyncTask<Long, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Long... params) {
            long selectedDateStart = params[0];
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectedDateStart);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long dayStart = calendar.getTimeInMillis();

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long dayEnd = calendar.getTimeInMillis();

            return db.scheduleDao().getEventsForDate(dayStart, dayEnd);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            eventList.clear();
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        }
    }

    interface DateTimePickerCallback {
        void onDateTimePicked(Calendar calendar);
    }
}
