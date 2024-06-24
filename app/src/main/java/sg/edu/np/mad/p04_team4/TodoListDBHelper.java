package sg.edu.np.mad.p04_team4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Class to handle database operations for the To-Do list
public class TodoListDBHelper extends SQLiteOpenHelper {

    // Database version and name constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todolist.db";
    private static final String TABLE_NAME = "todo_items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ITEM = "item";

    // Constructor to initialize the database helper
    public TodoListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method to create the database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    // Method to upgrade the database (called when the version number changes)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to add a new item to the database
    public void addItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, item);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Method to delete an item from the database
    public void deleteItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ITEM + " = ?", new String[]{item});
        db.close();
    }

    // Method to retrieve all items from the database
    public ArrayList<String> getAllItems() {
        ArrayList<String> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                itemList.add(cursor.getString(1)); // Add each item to the list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }
}
