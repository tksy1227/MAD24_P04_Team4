package sg.edu.np.mad.p04_team4.Calender;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Event.class}, version = 3)  // Ensure this version matches in both database classes
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ScheduleDao scheduleDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "event_database")
                    .fallbackToDestructiveMigration()  // Use this line to handle schema changes by clearing the data
                    .build();
        }
        return instance;
    }
}
