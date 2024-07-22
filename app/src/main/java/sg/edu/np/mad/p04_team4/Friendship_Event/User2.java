package sg.edu.np.mad.p04_team4.Friendship_Event;
import java.io.Serializable;

public class User2 implements Serializable {
    public String name;
    public String description;
    public int id;
    public boolean followed;

    public User2() {}

    public User2(String name, String description, int id, boolean followed) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.followed = followed;
    }
}
