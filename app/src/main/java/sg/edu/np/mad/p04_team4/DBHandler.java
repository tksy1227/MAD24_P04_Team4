package sg.edu.np.mad.p04_team4;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATEBASE_VERSION = 1;
    private static final String DATABASE_NAME = "myuser.db";
    private static final String USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATEBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Database Operations", "Creating a Table.");
        try {
            String CREATE_USERS_TABLE = "CREATE TABLE " + USERS + "(" + COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + "TEXT,"
                    + COLUMN_PASSWORD + "TEXT" + ")";
            db.execSQL(CREATE_USERS_TABLE);

            // Generate 1 default user
            String id = String.valueOf(1);
            String name = "admin";
            String password = "admin";
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_PASSWORD, password);
            db.insert(USERS, null, values);
            Log.i("Datebase Operations", "Table created succesfully.");
        } catch (SQLiteException e) {
            Log.i("Database Operations", "Error creating table", e);
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
        User user = new User(1, "John Doe", "password");
        String query = "SELECT * FROM" + USERS + "WHERE" + COLUMN_NAME + "=\"" + username + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            user.setID(Integer.parseInt(cursor.getString(0)));
            user.setName(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            cursor.close();
        }

        db.close();
        return user;
    }

    public User getUser(int user_id) {
        SQLiteDatabase db = getReadableDatabase();
        User user = new User(1, "John Doe", "password");
        Cursor cursor = db.query(USERS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PASSWORD}, COLUMN_ID + "=?",
                new String[]{String.valueOf(user_id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int id = cursor.getInt((int) cursor.getColumnIndex("id"));
            String name = cursor.getString((int) cursor.getColumnIndex("name"));
            String password = cursor.getString((int) cursor.getColumnIndex("password"));
            user.setID(id);
            user.setName(name);
            user.setPassword(password);
            cursor.close();
        }
        db.close();
        return user;
    }

    public ArrayList<User> getUsers() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<User> userList = new ArrayList<>();
        String query = "SELECT * FROM " + USERS;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt((int) cursor.getColumnIndex("id"));
            String name = cursor.getString((int) cursor.getColumnIndex("name"));
            String password = cursor.getString((int) cursor.getColumnIndex("password"));
            User user = new User(id, name, password);
            userList.add(user);
        }
        cursor.close();
        return userList;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, String.valueOf(user.getID()));
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PASSWORD, user.getPassword());
        String clause = "id=?";
        String[] args = {String.valueOf((user.getID()))};
        db.update(USERS, values, clause, args);
        db.close();
    }

    public boolean deleteUser(String username) {
        boolean result = false;
        String query = "SELECT * FROM " + USERS + "WHERE" + COLUMN_NAME + "=\"" + username + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        User user = new User(1, "John Doe", "passwowrd");
        ;

        if (cursor.moveToFirst()) {
            user.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(USERS, COLUMN_ID + "=?",
                    new String[]{String.valueOf(user.getID())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public void deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM entries");
        db.close();
    }

    @Override
    public void close() {
        Log.i("Database Operations", "Database is closed.");
        super.close();
    }
}