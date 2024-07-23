package sg.edu.np.mad.p04_team4.HabitTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class chartDBhandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chart_data.db";
    private static final int DATABASE_VERSION = 1;

    public chartDBhandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //clearDatabase();
        //populate(3,200);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table
        db.execSQL(ChartDataContract.ChartEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database (drop existing table and recreate)
        db.execSQL("DROP TABLE IF EXISTS " + ChartDataContract.ChartEntry.TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> unique_habit() {
        ArrayList<String> habitList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Query to fetch distinct habits
            String query = "SELECT DISTINCT " + ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION +
                    " FROM " + ChartDataContract.ChartEntry.TABLE_NAME;

            cursor = db.rawQuery(query, null);
            // Iterate through the cursor and add habits to list
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String habit = cursor.getString(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION));
                    habitList.add(habit);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return habitList;
    }

    public void insertData(chartData data) {
        if (data == null) {
            Log.e("DB_INSERT", "chartData object is null");
            return;
        }

        String habit = data.getHabit();
        Float datapoint = data.getData();
        String date = data.getDate();
        String unit = data.getUnit();
        String description = data.getDescription();

        if (habit == null || date == null) {
            Log.e("DB_INSERT", "Required fields are missing");
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION, habit);

        if (datapoint != null) {
            values.put(ChartDataContract.ChartEntry.COLUMN_NAME_DATAPOINT, datapoint);
        } else {
            values.putNull(ChartDataContract.ChartEntry.COLUMN_NAME_DATAPOINT);
        }

        values.put(ChartDataContract.ChartEntry.COLUMN_NAME_DATE, date);
        values.put(ChartDataContract.ChartEntry.COLUMN_NAME_UNIT, unit);
        values.put(ChartDataContract.ChartEntry.COLUMN_NAME_DESCRIPTION, description);

        db.insert(ChartDataContract.ChartEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void populate(int habits, int rows) {
        // List of habit names
        List<String> habitNames = Arrays.asList(
                "Exercise", "Reading", "Meditation", "Cooking", "Journaling",
                "Walking", "Yoga", "Painting", "Gardening", "Studying"
        );

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Ensure the number of habits doesn't exceed the size of the habitNames list
        habits = Math.min(habits, habitNames.size());

        for (int x = 0; x < habits; x++) {
            String habit = habitNames.get(x);

            // Reset the calendar to the current date for each habit
            calendar = Calendar.getInstance();

            for (int i = 1; i <= rows; i++) {
                float data = (float) (Math.random() * 5);
                String date = sdf.format(calendar.getTime()); // Format the current date
                String unit = "units"; // Example unit, replace as needed
                String description = "Sample description"; // Example description, replace as needed

                // Create chartData object and insert into database
                chartData insert = new chartData(habit, data, date, unit, description);
                insertData(insert);
                Log.d("insert", "habit: " + habit + "  data: " + data);

                // Move to the next day
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
    }

    public ArrayList<chartData> getDataByHabit(String habitType) {
        ArrayList<chartData> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] projection = {
                    ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION,
                    ChartDataContract.ChartEntry.COLUMN_NAME_DATAPOINT,
                    ChartDataContract.ChartEntry.COLUMN_NAME_DATE,
                    ChartDataContract.ChartEntry.COLUMN_NAME_UNIT,
                    ChartDataContract.ChartEntry.COLUMN_NAME_DESCRIPTION
            };

            String selection = ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION + " = ?";
            String[] selectionArgs = { habitType };

            cursor = db.query(
                    ChartDataContract.ChartEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                String habit = cursor.getString(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION));
                float datapoint = cursor.getFloat(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_DATAPOINT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_DATE));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_UNIT));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_DESCRIPTION));

                chartData data = new chartData(habit, datapoint, date, unit, description);
                dataList.add(data);
            }
        } catch (Exception e) {
            Log.e("DB_QUERY", "Error querying database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return dataList;
    }

    public List<BarEntry> convertToBarEntries(ArrayList<chartData> chartDataList) {
        List<BarEntry> barEntries = new ArrayList<>();

        // Sort the chartDataList by date
        Collections.sort(chartDataList, new Comparator<chartData>() {
            @Override
            public int compare(chartData o1, chartData o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        // Create BarEntry objects with index numbers as x-values
        for (int i = 0; i < chartDataList.size(); i++) {
            chartData data = chartDataList.get(i);
            float yValue = data.getData();
            BarEntry barEntry = new BarEntry(i, yValue); // Use index as x-value
            barEntries.add(barEntry);
        }

        return barEntries;
    }
    public List<Entry> convertToLineEntries(ArrayList<chartData> chartDataList) {
        List<Entry> lineEntries = new ArrayList<>();

        // Sort the chartDataList by date
        Collections.sort(chartDataList, new Comparator<chartData>() {
            @Override
            public int compare(chartData o1, chartData o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        // Create Entry objects with date as x-values and data as y-values
        for (int i = 0; i < chartDataList.size(); i++) {
            chartData data = chartDataList.get(i);
            float xValue = i; // Use index as x-value (can be replaced with actual date if needed)
            float yValue = data.getData();
            Entry entry = new Entry(xValue, yValue);
            lineEntries.add(entry);
        }

        return lineEntries;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ChartDataContract.ChartEntry.TABLE_NAME);
        db.close();
    }
    public void deleteDataByHabit(String habit) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(ChartDataContract.ChartEntry.TABLE_NAME,
                    ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION + " = ?",
                    new String[]{habit});
        } catch (Exception e) {
            Log.e("DB_DELETE", "Error deleting data: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    public String getDescriptionForHabit(String habitType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String description = null;
        try {
            String query = "SELECT " + ChartDataContract.ChartEntry.COLUMN_NAME_DESCRIPTION +
                    " FROM " + ChartDataContract.ChartEntry.TABLE_NAME +
                    " WHERE " + ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION + " = ?" +
                    " LIMIT 1";
            cursor = db.rawQuery(query, new String[]{habitType});
            if (cursor != null && cursor.moveToFirst()) {
                description = cursor.getString(cursor.getColumnIndexOrThrow(ChartDataContract.ChartEntry.COLUMN_NAME_DESCRIPTION));
            }
        } catch (Exception e) {
            Log.e("DB_QUERY", "Error querying database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return description;
    }
    public String getHabitStartDate(String habit){
        SQLiteDatabase db = this.getReadableDatabase();
        String firstDate = null;

        String query = "SELECT MIN(" + ChartDataContract.ChartEntry.COLUMN_NAME_DATE + ") AS first_date " +
                "FROM " + ChartDataContract.ChartEntry.TABLE_NAME + " " +
                "WHERE " + ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{habit});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                firstDate = cursor.getString(cursor.getColumnIndexOrThrow("first_date"));
            }
            cursor.close();
        }

        return firstDate;

    }
    public String getHabitLastDate(String habit){
        SQLiteDatabase db = this.getReadableDatabase();
        String LastDate = null;

        String query = "SELECT MAX(" + ChartDataContract.ChartEntry.COLUMN_NAME_DATE + ") AS first_date " +
                "FROM " + ChartDataContract.ChartEntry.TABLE_NAME + " " +
                "WHERE " + ChartDataContract.ChartEntry.COLUMN_NAME_HABIT_CLASSIFICATION + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{habit});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                LastDate = cursor.getString(cursor.getColumnIndexOrThrow("first_date"));
            }
            cursor.close();
        }

        return LastDate;

    }
    public long getHabitDuration(String habit) {
        String startDateStr = getHabitStartDate(habit);
        String lastDateStr = getHabitLastDate(habit);

        if (startDateStr == null || lastDateStr == null) {
            return -1; // or any other error indicator
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date lastDate = dateFormat.parse(lastDateStr);

            long durationInMillis = lastDate.getTime() - startDate.getTime();
            return TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // or any other error indicator
        }
    }



}
