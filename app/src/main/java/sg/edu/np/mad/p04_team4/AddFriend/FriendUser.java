package sg.edu.np.mad.p04_team4.AddFriend;

public class FriendUser {

    private int id;

    private String name;

    public void setId(int id) {this.id = id; }
    public void setName(String username) {
        this.name = username;
    }

    public int getId() {return id; }
    public String getName() {
        return name;
    }

    public FriendUser(String name) {
        this.name=name;}
}
