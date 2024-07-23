package sg.edu.np.mad.p04_team4.HabitTracker;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class chartData {
    private String habit;
    private float data;
    private String date; // Store date as String in ISO 8601 format
    private String unit;
    private String description;

    public chartData(String habit, float data, String date, String unit, String description) {
        this.habit = habit;
        this.data = data;
        this.date = date;
        this.unit = unit;
        this.description = description;
    }

    // Getters and Setters
    public String getHabit() {
        return habit;
    }

    public void setHabit(String habit) {
        this.habit = habit;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Convert ISO 8601 date string to milliseconds
    public long getDateTimeInMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date dateObj = sdf.parse(date);
            return dateObj.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("FAIL", "Parse failed for " + date);
            return 0;
        }
    }

    public BarEntry toBarEntry() {
        return new BarEntry(getDateTimeInMillis(), getData());
    }
}
