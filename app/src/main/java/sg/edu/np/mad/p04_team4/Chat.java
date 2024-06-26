package sg.edu.np.mad.p04_team4;

public class Chat {
    private String key; // Unique identifier for the chat
    private String name; // Name of the chat or participant
    private String lastMessage; // Last message sent in the chat
    private String time; // Time of the last message

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    }

    public Chat(String name, String lastMessage, String time) {
        this.name = name; // Set the name of the chat
        this.lastMessage = lastMessage; // Set the last message of the chat
        this.time = time; // Set the time of the last message
    }

    public String getKey() {
        return key; // Get the key of the chat
    }

    public void setKey(String key) {
        this.key = key; // Set the key of the chat
    }

    public String getName() {
        return name; // Get the name of the chat
    }

    public String getLastMessage() {
        return lastMessage; // Get the last message of the chat
    }

    public String getTime() {
        return time; // Get the time of the last message
    }

    public void setTime(String time) {
        this.time = time;
    }
}
