package sg.edu.np.mad.p04_team4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "timer_log.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "timer_logs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMER = "timer";
    public static final String COLUMN_PURPOSE = "purpose";
    public static final String COLUMN_DATE = "date";

    // SQL to create the table
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TIMER + " TEXT, " +
            COLUMN_PURPOSE + " TEXT, " +
            COLUMN_DATE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
