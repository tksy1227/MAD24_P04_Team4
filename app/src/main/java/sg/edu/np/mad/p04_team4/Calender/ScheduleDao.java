package sg.edu.np.mad.p04_team4.Calender;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insert(Event event);

    @Delete
    void delete(Event event);

    @Update
    void update(Event event);

    @Query("SELECT * FROM events WHERE startTime <= :end AND endTime >= :start")
    List<Event> getEventsForDate(long start, long end);

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(long id);
    @Query("SELECT * FROM events WHERE date >= :monthStart AND date < :monthEnd")
    List<Event> getEventsForMonth(long monthStart, long monthEnd);

}
