package sg.edu.np.mad.p04_team4.Chat;

public class TextMessage extends Message {
    private String text; // The text content of the message

    public TextMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(TextMessage.class)
    }

    public TextMessage(long id, long timestamp, String text, String userId) {
        super(id, timestamp, userId, "text"); // Call the superclass constructor
        this.text = text; // Set the text content of the message
    }

    public String getText() {
        return text; // Get the text content of the message
    }

    public void setText(String text) {
        this.text = text; // Set the text content of the message
    }
}
