package sg.edu.np.mad.p04_team4;

public class Chat {
    private String name;
    private String lastMessage;
    private String time;

    public Chat(String name, String lastMessage, String time) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTime() {
        return time;
    }
}
