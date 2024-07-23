package sg.edu.np.mad.p04_team4.HabitTracker;

import android.provider.BaseColumns;

public class ChartDataContract {
    private ChartDataContract() {}

    public static class ChartEntry implements BaseColumns {
        public static final String TABLE_NAME = "chart_data";
        public static final String COLUMN_NAME_HABIT_CLASSIFICATION = "habit_classification";
        public static final String COLUMN_NAME_DATAPOINT = "datapoint";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_UNIT = "unit";
        public static final String COLUMN_NAME_DESCRIPTION = "description";

        // SQL query to create the table
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_HABIT_CLASSIFICATION + " TEXT," +
                        COLUMN_NAME_DATAPOINT + " REAL," +
                        COLUMN_NAME_DATE + " TEXT," +
                        COLUMN_NAME_UNIT + " TEXT," +
                        COLUMN_NAME_DESCRIPTION + " TEXT)";
    }
}
