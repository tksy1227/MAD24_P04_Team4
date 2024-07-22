package sg.edu.np.mad.p04_team4.Timer;

public class Time {
    private String timer;
    private String purpose;
    private String date;

    // No-argument constructor required for calls to DataSnapshot.getValue(Time.class)
    public Time() {
    }

    public Time(String timer, String purpose, String date) {
        this.timer = timer;
        this.purpose = purpose;
        this.date = date;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
