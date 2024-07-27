package sg.edu.np.mad.p04_team4.HabitTracker;

import android.provider.BaseColumns;

/**
 * Defines the schema for the chart data table used in the Habit Tracker application.
 */
public class ChartDataContract {
    // Private constructor to prevent instantiation.
    private ChartDataContract() {}

    /**
     * Inner class that defines the table contents.
     */
    public static class ChartEntry implements BaseColumns {
        // Name of the table.
        public static final String TABLE_NAME = "chart_data";

        // Column names for the table.
        public static final String COLUMN_NAME_HABIT_CLASSIFICATION = "habit_classification";
        public static final String COLUMN_NAME_DATAPOINT = "datapoint";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_UNIT = "unit";
        public static final String COLUMN_NAME_DESCRIPTION = "description";

        // SQL query to create the table.
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," + // Primary key auto increment ID.
                        COLUMN_NAME_HABIT_CLASSIFICATION + " TEXT," + // Habit classification column.
                        COLUMN_NAME_DATAPOINT + " REAL," + // Data point value column.
                        COLUMN_NAME_DATE + " TEXT," + // Date of the data point.
                        COLUMN_NAME_UNIT + " TEXT," + // Unit of the data point.
                        COLUMN_NAME_DESCRIPTION + " TEXT)"; // Description of the data point.
    }
}
