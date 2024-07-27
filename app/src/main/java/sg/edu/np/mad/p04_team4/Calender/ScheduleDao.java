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
    void insert(sg.edu.np.mad.p04_team4.Calender.Event event);

    @Delete
    void delete(sg.edu.np.mad.p04_team4.Calender.Event event);

    @Update
    void update(sg.edu.np.mad.p04_team4.Calender.Event event);

    @Query("SELECT * FROM events WHERE startTime <= :end AND endTime >= :start")
    List<sg.edu.np.mad.p04_team4.Calender.Event> getEventsForDate(long start, long end);

    @Query("SELECT * FROM events WHERE id = :id")
    sg.edu.np.mad.p04_team4.Calender.Event getEventById(long id);
    @Query("SELECT * FROM events WHERE date >= :monthStart AND date < :monthEnd")
    List<sg.edu.np.mad.p04_team4.Calender.Event> getEventsForMonth(long monthStart, long monthEnd);

}
