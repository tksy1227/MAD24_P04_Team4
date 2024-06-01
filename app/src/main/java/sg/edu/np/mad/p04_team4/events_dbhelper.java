package sg.edu.np.mad.p04_team4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import android.util.Log;
public class events_dbhelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user_events.db";
    public static final String TABLE_NAME = "event_info";
    private static final String USERID = "userid";
    private static final String COLUMN_CHALLENGES = "challenges";
    private static final String COLUMN_MILESTONES = "milestones";
    private static final String COLUMN_GOALS = "goals";
    private static final String COLUMN_C_EMPTY = "c_empty";
    private static final String COLUMN_M_EMPTY = "m_empty";
    private static final String COLUMN_G_EMPTY = "g_empty";

    // Constructor
    public events_dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " ("
                + USERID + " INTEGER PRIMARY KEY,"
                + COLUMN_CHALLENGES + " TEXT,"
                + COLUMN_MILESTONES + " TEXT,"
                + COLUMN_GOALS + " TEXT,"
                + COLUMN_C_EMPTY + " INTEGER,"  // Boolean column for challenges
                + COLUMN_M_EMPTY + " INTEGER,"  // Boolean column for milestones
                + COLUMN_G_EMPTY + " INTEGER)"; // Boolean column for goals
        db.execSQL(CREATE_TABLE_QUERY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insertEvent(int userId, String challenges, String milestones, String goals, int cEmpty, int mEmpty, int gEmpty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(COLUMN_CHALLENGES, challenges);
        values.put(COLUMN_MILESTONES, milestones);
        values.put(COLUMN_GOALS, goals);
        values.put(COLUMN_C_EMPTY, cEmpty);
        values.put(COLUMN_M_EMPTY, mEmpty);
        values.put(COLUMN_G_EMPTY, gEmpty);

        if (checkUserIdExist(userId)) {
            // If userId exists, update the existing entry
            db.update(TABLE_NAME, values, USERID + "=?", new String[]{String.valueOf(userId)});
            Log.d("TEST", "Event with userId " + userId + " updated.");
        } else {
            // If userId doesn't exist, insert a new entry
            db.insert(TABLE_NAME, null, values);
            Log.d("TEST", "New event inserted with userId " + userId + ".");
        }

        // Closing database connection
        db.close();
    }

    // Method to check if userId already exists in the table
    private boolean checkUserIdExist(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERID + " = ?", new String[]{String.valueOf(userId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public User_events getUserEvent(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User_events userEvent = null;

        Cursor cursor = db.query(TABLE_NAME, null, USERID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int userIdIndex = cursor.getColumnIndex(USERID);
                int challengeEmptyIndex = cursor.getColumnIndex(COLUMN_C_EMPTY);
                int milestoneEmptyIndex = cursor.getColumnIndex(COLUMN_M_EMPTY);
                int goalEmptyIndex = cursor.getColumnIndex(COLUMN_G_EMPTY);
                int challengeIndex = cursor.getColumnIndex(COLUMN_CHALLENGES);
                int milestoneIndex = cursor.getColumnIndex(COLUMN_MILESTONES);
                int goalIndex = cursor.getColumnIndex(COLUMN_GOALS);

                if (userIdIndex != -1 && challengeEmptyIndex != -1 && milestoneEmptyIndex != -1 &&
                        goalEmptyIndex != -1 && challengeIndex != -1 && milestoneIndex != -1 && goalIndex != -1) {
                    boolean challenge_e = cursor.getInt(challengeEmptyIndex) != 0;
                    boolean milestone_e = cursor.getInt(milestoneEmptyIndex) != 0;
                    boolean goals_e = cursor.getInt(goalEmptyIndex) != 0;
                    String challenge = cursor.getString(challengeIndex);
                    String milestone = cursor.getString(milestoneIndex);
                    String goals = cursor.getString(goalIndex);

                    userEvent = new User_events(userId, challenge_e, milestone_e, goals_e, challenge, milestone, goals);
                } else {
                    Log.e("getUserEvent", "One or more columns not found in cursor");
                }
            } else {
                Log.e("getUserEvent", "No data found for userId: " + userId);
            }
            cursor.close();
        } else {
            Log.e("getUserEvent", "Cursor is null");
        }

        db.close();

        return userEvent;
    }
    public void updateEventPart(int userId, String columnNameLiteral, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Map literal column name to actual column name constant
        String columnName;
        switch (columnNameLiteral) {
            case "COLUMN_CHALLENGES":
                columnName = COLUMN_CHALLENGES;
                break;
            case "COLUMN_MILESTONES":
                columnName = COLUMN_MILESTONES;
                break;
            case "COLUMN_GOALS":
                columnName = COLUMN_GOALS;
                break;
            case "COLUMN_C_EMPTY":
                columnName = COLUMN_C_EMPTY;
                break;
            case "COLUMN_M_EMPTY":
                columnName = COLUMN_M_EMPTY;
                break;
            case "COLUMN_G_EMPTY":
                columnName = COLUMN_G_EMPTY;
                break;
            default:
                Log.e("TEST", "Invalid column name: " + columnNameLiteral);
                db.close();
                return;
        }

        // Check if the column name corresponds to a boolean column
        if (columnName.equals(COLUMN_C_EMPTY) || columnName.equals(COLUMN_M_EMPTY) || columnName.equals(COLUMN_G_EMPTY)) {
            // If the column is a boolean column, parse the newValue string to a boolean value
            boolean boolValue = newValue.equals("1");
            values.put(columnName, boolValue ? 1 : 0); // Store boolean as integer (1 for true, 0 for false)
        } else {
            // If the column is not a boolean column, directly put the newValue string
            values.put(columnName, newValue);
        }

        if (checkUserIdExist(userId)) {
            // If userId exists, update the specified column with the new value
            db.update(TABLE_NAME, values, USERID + "=?", new String[]{String.valueOf(userId)});
            Log.d("TEST", columnName + " updated for user with userId " + userId + ".");
        } else {
            // If userId doesn't exist, log an error
            Log.e("TEST", "User with userId " + userId + " does not exist. Cannot update " + columnName + ".");
        }

        // Closing database connection
        db.close();
    }


}