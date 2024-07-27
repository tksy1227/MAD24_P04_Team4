package sg.edu.np.mad.p04_team4.Calender;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Event.class}, version = 2)  // Ensure this version matches in both database classes
public abstract class EventDatabase extends RoomDatabase {
    public abstract ScheduleDao eventDao();

    private static volatile EventDatabase INSTANCE;

    static EventDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    EventDatabase.class, "event_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

