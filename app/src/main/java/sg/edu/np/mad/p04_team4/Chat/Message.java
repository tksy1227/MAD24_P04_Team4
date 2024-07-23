package sg.edu.np.mad.p04_team4.Chat;

public class Message {
    private long id;
    private long timestamp;
    private String userId;
    private String type;
    private String content; // New field to store text or sticker path

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(long id, long timestamp, String userId, String type, String content) {
        this.id = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.type = type;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
