package sg.edu.np.mad.p04_team4;

public class TextMessage extends Message {
    private String text;

    public TextMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(TextMessage.class)
    }

    public TextMessage(long id, long timestamp, String text, String userId) {
        super(id, timestamp, userId, "text");
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
