package sg.edu.np.mad.p04_team4.Calender;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private long date;
    private long startTime;
    private long endTime;
    private boolean isAllDay;

    // Constructor without id for new events
    public Event(String title, long date, long startTime, long endTime) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor with id for existing events
    @Ignore  // Add this annotation to ignore this constructor
    public Event(long id, String title, long date, long startTime, long endTime) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
