package sg.edu.np.mad.p04_team4;
import java.io.Serializable;
public class User implements Serializable{
    public String name;
    public String description;
    public int id;
    public boolean followed;
    public boolean challange_e;
    public boolean milestone_e;
    public boolean goals_e;


    public User(String name, String description, int id, boolean followed,boolean challange,boolean milestone,boolean goals) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.followed = followed;
        this.challange_e=challange;
        this.milestone_e = milestone;
        this.goals_e=goals;
    }
}
