package sg.edu.np.mad.p04_team4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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
                + COLUMN_C_EMPTY + " INTEGER,"
                + COLUMN_M_EMPTY + " INTEGER,"
                + COLUMN_G_EMPTY + " INTEGER)";
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertEvent(User_events userEvents) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERID, userEvents.getId());
        values.put(COLUMN_CHALLENGES, userEvents.getChallange());
        values.put(COLUMN_MILESTONES, userEvents.getMilestone());
        values.put(COLUMN_GOALS, userEvents.getGoals());
        values.put(COLUMN_C_EMPTY, userEvents.isChallange_e() ? 1 : 0);
        values.put(COLUMN_M_EMPTY, userEvents.isMilestone_e() ? 1 : 0);
        values.put(COLUMN_G_EMPTY, userEvents.isGoals_e() ? 1 : 0);

        if (checkUserIdExist(userEvents.getId())) {
            db.update(TABLE_NAME, values, USERID + "=?", new String[]{String.valueOf(userEvents.getId())});
            Log.d("DB", "Event with userId " + userEvents.getId() + " updated.");
        } else {
            db.insert(TABLE_NAME, null, values);
            Log.d("DB", "New event inserted with userId " + userEvents.getId() + ".");
        }

        db.close();
    }

    private boolean checkUserIdExist(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERID + " = ?", new String[]{String.valueOf(userId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
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

                    userEvent = new User_events(userId, challenge, milestone, goals, challenge_e, milestone_e, goals_e);
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
                Log.e("DB", "Invalid column name: " + columnNameLiteral);
                db.close();
                return;
        }

        if (columnName.equals(COLUMN_C_EMPTY) || columnName.equals(COLUMN_M_EMPTY) || columnName.equals(COLUMN_G_EMPTY)) {
            boolean boolValue = newValue.equals("1");
            values.put(columnName, boolValue ? 1 : 0);
        } else {
            values.put(columnName, newValue);
        }

        if (checkUserIdExist(userId)) {
            db.update(TABLE_NAME, values, USERID + "=?", new String[]{String.valueOf(userId)});
            Log.d("DB", columnName + " updated for user with userId " + userId + ".");
        } else {
            Log.e("DB", "User with userId " + userId + " does not exist. Cannot update " + columnName + ".");
        }

        db.close();
    }
}
