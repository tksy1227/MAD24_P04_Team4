package sg.edu.np.mad.p04_team4.Timer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TimerDB";
    public static final String TABLE_NAME = "TimerData";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_PURPOSE = "PURPOSE";
    public static final String COLUMN_DURATION = "DURATION";
    public static final String COLUMN_DATE = "END_TIME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, PURPOSE TEXT, DURATION INTEGER, END_TIME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTimerData(String purpose, String duration, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PURPOSE, purpose);
        contentValues.put(COLUMN_DURATION, duration);
        contentValues.put(COLUMN_DATE, endTime);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }
    public void deleteTimer(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void clearAllTimers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}

