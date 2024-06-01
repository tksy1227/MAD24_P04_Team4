package sg.edu.np.mad.p04_team4;

public class Time {
    private String timer;
    private String purpose;
    private String date;

    public Time(String timer, String purpose, String date) {
        this.timer = timer;
        this.purpose = purpose;
        this.date = date;
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

