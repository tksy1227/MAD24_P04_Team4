package sg.edu.np.mad.p04_team4.ScreenTime;

public class ScreenTimeEntry {
    private String featureName; // Name of the feature or app
    private long duration; // Duration of screen time in seconds

    // Constructor to initialize ScreenTimeEntry with feature name and duration
    public ScreenTimeEntry(String featureName, long duration) {
        this.featureName = featureName;
        this.duration = duration;
    }

    // Getter method for feature name
    public String getFeatureName() {
        return featureName;
    }

    // Getter method for duration
    public long getDuration() {
        return duration;
    }
}
