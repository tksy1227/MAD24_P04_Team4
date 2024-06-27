package sg.edu.np.mad.p04_team4;

public class Message {
    private long id;
    private long timestamp;
    private String userId;
    private String type;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(long id, long timestamp, String userId, String type) {
        this.id = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }
}