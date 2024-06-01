package sg.edu.np.mad.p04_team4;

import java.io.Serializable;

public class User_events implements Serializable {
    public boolean challange_e;
    String challange;
    public boolean milestone_e;
    public boolean goals_e;


    public User_events(boolean challange,boolean milestone,boolean goals,String challange_info) {
        this.challange_e=challange;
        this.milestone_e = milestone;
        this.goals_e=goals;
        this.challange=challange_info;
    }
    public User_events() {
        this.challange_e=false;
        this.milestone_e = false;
        this.goals_e=false;
        this.challange="nuhuh";
    }
    public User_events(boolean challange,boolean milestone,boolean goals) {
        this.challange_e=challange;
        this.milestone_e = milestone;
        this.goals_e=goals;
    }

}
