package sg.edu.np.mad.p04_team4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myuser.db";
    private static final String USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Database Operations", "Creating a Table.");
        try {
            String CREATE_USERS_TABLE = "CREATE TABLE " + USERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PASSWORD + " TEXT)";
            db.execSQL(CREATE_USERS_TABLE);

            // Generate 1 default user
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, "admin");
            values.put(COLUMN_PASSWORD, "admin");
            db.insert(USERS, null, values);
            Log.i("Database Operations", "Table created successfully.");
        } catch (SQLiteException e) {
            Log.e("Database Operations", "Error creating table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PASSWORD, user.getPassword());
        db.insert(USERS, null, values);
        db.close();
    }

    public User getUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        User user = null;
        String query = "SELECT * FROM " + USERS + " WHERE " + COLUMN_NAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
        }
        cursor.close();
        db.close();
        return user;
    }

    public ArrayList<User> getUsers() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<User> userList = new ArrayList<>();
        String query = "SELECT * FROM " + USERS;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            User user = new User(id, name, password);
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PASSWORD, user.getPassword());
        db.update(USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(user.getID())});
        db.close();
    }

    public boolean deleteUser(String username) {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + USERS + " WHERE " + COLUMN_NAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            db.delete(USERS, COLUMN_ID + "=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))});
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public void deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + USERS);
        db.close();
    }

    @Override
    public void close() {
        Log.i("Database Operations", "Database is closed.");
        super.close();
    }
}
