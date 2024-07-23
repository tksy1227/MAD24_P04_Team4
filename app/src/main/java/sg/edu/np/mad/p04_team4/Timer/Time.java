package sg.edu.np.mad.p04_team4.Timer;

public class Time {
    private int id;
    private String timer;
    private String purpose;
    private String date;

    public Time(int id, String timer, String purpose, String date) {
        this.id = id;
        this.timer = timer;
        this.purpose = purpose;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTimer() {
        return timer;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDate() {
        return date;
    }
}
