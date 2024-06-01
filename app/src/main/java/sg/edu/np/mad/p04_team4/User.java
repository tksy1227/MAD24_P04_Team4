package sg.edu.np.mad.p04_team4;

public class User {
    private int id;
    private String name;
    private String password;
    public void setID(int id) {
        this.id = id;    }
    public void setName(String username) {
        this.name = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getID() {return id; }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
