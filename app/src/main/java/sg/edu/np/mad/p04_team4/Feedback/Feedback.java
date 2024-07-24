package sg.edu.np.mad.p04_team4.Feedback;

public class Feedback {
    private String id;
    private String content;
    private long timestamp;

    public Feedback() {
        // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
    }

    public Feedback(String id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}