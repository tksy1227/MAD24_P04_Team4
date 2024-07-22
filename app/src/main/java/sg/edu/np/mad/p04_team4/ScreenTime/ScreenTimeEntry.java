package sg.edu.np.mad.p04_team4.ScreenTime;

public class ScreenTimeEntry {
    private String featureName;
    private long duration;

    public ScreenTimeEntry(String featureName, long duration) {
        this.featureName = featureName;
        this.duration = duration;
    }

    public String getFeatureName() {
        return featureName;
    }

    public long getDuration() {
        return duration;
    }
}

