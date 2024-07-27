package sg.edu.np.mad.p04_team4.Calender;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

import sg.edu.np.mad.p04_team4.R;

public class EditEventActivity extends AppCompatActivity {
    private AppDatabase db;
    private long eventId, selectedDateInMillis;
    private Calendar calendarStart, calendarEnd;

    private EditText editTextTitle;
    private CalendarView calendarView;
    private TimePicker timePickerStart, timePickerEnd;
    private Button buttonConfirm;

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
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);
        buttonConfirm = findViewById(R.id.buttonConfirm);

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

        buttonConfirm.setOnClickListener(v -> {
            String eventTitle = editTextTitle.getText().toString();
            if (!eventTitle.isEmpty()) {
                calendarStart.set(Calendar.HOUR_OF_DAY, timePickerStart.getCurrentHour());
                calendarStart.set(Calendar.MINUTE, timePickerStart.getCurrentMinute());

                calendarEnd.set(Calendar.HOUR_OF_DAY, timePickerEnd.getCurrentHour());
                calendarEnd.set(Calendar.MINUTE, timePickerEnd.getCurrentMinute());

                new UpdateEventTask().execute(new Event(eventId, eventTitle, selectedDateInMillis, calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis()));
            } else {
                Toast.makeText(this, "Please enter an event title", Toast.LENGTH_SHORT).show();
            }
        });
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
                timePickerStart.setCurrentHour(calendarStart.get(Calendar.HOUR_OF_DAY));
                timePickerStart.setCurrentMinute(calendarStart.get(Calendar.MINUTE));
                calendarEnd.setTimeInMillis(event.getEndTime());
                timePickerEnd.setCurrentHour(calendarEnd.get(Calendar.HOUR_OF_DAY));
                timePickerEnd.setCurrentMinute(calendarEnd.get(Calendar.MINUTE));
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
            Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditEventActivity.this, MainCalender.class);
            intent.putExtra("selectedDate", selectedDateInMillis);
            startActivity(intent);
            finish();
        }
    }

}
