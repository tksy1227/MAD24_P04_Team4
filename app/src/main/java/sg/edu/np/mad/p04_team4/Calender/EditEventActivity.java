package sg.edu.np.mad.p04_team4.Calender;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.AddFriend.FriendListActivity;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.Login.AccountActivity;
import sg.edu.np.mad.p04_team4.R;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {
    private AppDatabase db;
    private long eventId, selectedDateInMillis;
    private Calendar calendarStart, calendarEnd;

    private EditText editTextTitle;
    private CalendarView calendarView;
    private Button buttonConfirm;
    private Button buttonStartDate, buttonEndDate, buttonStartTime, buttonEndTime;
    private Switch switchAllDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Event");
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(EditEventActivity.this, MainCalender.class);
            startActivity(intent);
            finish();
        });

        db = AppDatabase.getInstance(this);

        editTextTitle = findViewById(R.id.editTextTitle);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setDate(System.currentTimeMillis(), false, true); // Set to current date
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);
        buttonStartTime = findViewById(R.id.buttonStartTime);
        buttonEndTime = findViewById(R.id.buttonEndTime);
        switchAllDay = findViewById(R.id.switchAllDay);

        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        eventId = getIntent().getLongExtra("eventId", -1);
        selectedDateInMillis = getIntent().getLongExtra("date", -1);

        if (eventId != -1) {
            new LoadEventTask().execute(eventId);
        }

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            selectedDateInMillis = selectedDate.getTimeInMillis();
        });

        buttonStartDate.setOnClickListener(v -> showDatePickerDialog(calendarStart, calendar -> {
            calendarStart = calendar;
            updateButtonDate(buttonStartDate, calendarStart);
        }));

        buttonEndDate.setOnClickListener(v -> showDatePickerDialog(calendarEnd, calendar -> {
            calendarEnd = calendar;
            updateButtonDate(buttonEndDate, calendarEnd);
        }));

        switchAllDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonStartTime.setVisibility(View.GONE);
                buttonEndTime.setVisibility(View.GONE);
            } else {
                buttonStartTime.setVisibility(View.VISIBLE);
                buttonEndTime.setVisibility(View.VISIBLE);
            }
        });

        buttonStartTime.setOnClickListener(v -> showTimePickerDialog(calendarStart, calendar -> {
            calendarStart.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendarStart.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            updateButtonTime(buttonStartTime, calendarStart);
        }));

        buttonEndTime.setOnClickListener(v -> showTimePickerDialog(calendarEnd, calendar -> {
            calendarEnd.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendarEnd.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            updateButtonTime(buttonEndTime, calendarEnd);
        }));

        buttonConfirm.setOnClickListener(v -> {
            String eventTitle = editTextTitle.getText().toString();
            if (!eventTitle.isEmpty()) {
                if (switchAllDay.isChecked()) {
                    // Set time to the start and end of the day for all-day events
                    calendarStart.set(Calendar.HOUR_OF_DAY, 0);
                    calendarStart.set(Calendar.MINUTE, 0);
                    calendarStart.set(Calendar.SECOND, 0);
                    calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
                    calendarEnd.set(Calendar.MINUTE, 59);
                    calendarEnd.set(Calendar.SECOND, 59);
                }

                if (calendarStart.after(calendarEnd)) {
                    Toast.makeText(this, getString(R.string.ensure_details), Toast.LENGTH_SHORT).show();
                } else {
                    Event event = new Event(eventId, eventTitle, selectedDateInMillis, calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis());
                    event.setAllDay(switchAllDay.isChecked());

                    new UpdateEventTask().execute(event);
                }
            } else {
                Toast.makeText(this, getString(R.string.enter_event_title), Toast.LENGTH_SHORT).show();
            }
        });

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(EditEventActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Home
        RelativeLayout homeRL = findViewById(R.id.home);
        homeRL.setOnClickListener(v -> {
            Intent homeIntent = new Intent(EditEventActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Friend List
        RelativeLayout friendlistRL = findViewById(R.id.friendlist);
        friendlistRL.setOnClickListener(v -> {
            Intent friendIntent = new Intent(EditEventActivity.this, FriendListActivity.class);
            startActivity(friendIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

        // Account
        RelativeLayout accountRL = findViewById(R.id.account);
        accountRL.setOnClickListener(v -> {
            Intent accountIntent = new Intent(EditEventActivity.this, AccountActivity.class);
            startActivity(accountIntent);
            // Optionally, finish this activity if you want to prevent the user from returning
            finish();
        });

    }

    private void showDatePickerDialog(Calendar initialCalendar, DateTimePickerCallback callback) {
        new DatePickerDialog(EditEventActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.set(year, monthOfYear, dayOfMonth);
            callback.onDateTimePicked(newCalendar);
        }, initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog(Calendar initialCalendar, DateTimePickerCallback callback) {
        new TimePickerDialog(EditEventActivity.this, (view, hourOfDay, minute) -> {
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            newCalendar.set(Calendar.MINUTE, minute);
            callback.onDateTimePicked(newCalendar);
        }, initialCalendar.get(Calendar.HOUR_OF_DAY), initialCalendar.get(Calendar.MINUTE), true).show();
    }

    private void updateButtonDate(Button button, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        button.setText(sdf.format(calendar.getTime()));
    }

    private void updateButtonTime(Button button, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        button.setText(sdf.format(calendar.getTime()));
    }

    interface DateTimePickerCallback {
        void onDateTimePicked(Calendar calendar);
    }

    private class LoadEventTask extends AsyncTask<Long, Void, Event> {
        @Override
        protected Event doInBackground(Long... eventIds) {
            return db.scheduleDao().getEventById(eventIds[0]);
        }

        @Override
        protected void onPostExecute(Event event) {
            if (event != null) {
                editTextTitle.setText(event.getTitle());
                calendarView.setDate(event.getDate());
                calendarStart.setTimeInMillis(event.getStartTime());
                updateButtonTime(buttonStartTime, calendarStart);
                calendarEnd.setTimeInMillis(event.getEndTime());
                updateButtonTime(buttonEndTime, calendarEnd);

                // Check if the event is all day or if times are 00:00 and 23:59
                boolean isAllDay = event.isAllDay() || (calendarStart.get(Calendar.HOUR_OF_DAY) == 0 && calendarStart.get(Calendar.MINUTE) == 0 && calendarEnd.get(Calendar.HOUR_OF_DAY) == 23 && calendarEnd.get(Calendar.MINUTE) == 59);

                // Set the "All Day" switch state
                switchAllDay.setChecked(isAllDay);

                // Show or hide time pickers based on "All Day" switch state
                if (isAllDay) {
                    buttonStartTime.setVisibility(View.GONE);
                    buttonEndTime.setVisibility(View.GONE);
                } else {
                    buttonStartTime.setVisibility(View.VISIBLE);
                    buttonEndTime.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class UpdateEventTask extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... events) {
            db.scheduleDao().update(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(EditEventActivity.this, getString(R.string.event_updated_successfully), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditEventActivity.this, MainCalender.class);
            intent.putExtra("selectedDate", selectedDateInMillis);
            startActivity(intent);
            finish();
        }
    }
}
