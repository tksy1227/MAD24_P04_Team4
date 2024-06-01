package sg.edu.np.mad.p04_team4;

import java.io.Serializable;

public class User_events implements Serializable {
    public int id;
    public boolean challange_e;
    public boolean milestone_e;
    public boolean goals_e;
    String challange;
    String milestone;
    String goals;


    public User_events(int id,String challange_info, String milestone_info,String goals_info,boolean challange,boolean milestone,boolean goals) {
        this.id=id;
        this.challange_e=challange;
        this.milestone_e = milestone;
        this.goals_e=goals;
        this.challange=challange_info;
        this.milestone=milestone_info;
        this.goals=goals_info;
    }
    public User_events(int id) {
        this.id=id;
        this.challange_e=false;
        this.milestone_e = false;
        this.goals_e=false;
    }
    public User_events(int id,boolean challange,boolean milestone,boolean goals) {
        this.id=id;
        this.challange_e=challange;
        this.milestone_e = milestone;
        this.goals_e=goals;
    }
    public int getId() {
        return id;
    }

    public boolean isChallange_e() {
        return challange_e;
    }

    public boolean isMilestone_e() {
        return milestone_e;
    }

    public boolean isGoals_e() {
        return goals_e;
    }

    public String getChallange() {
        return challange;
    }

    public String getMilestone() {
        return milestone;
    }

    public String getGoals() {
        return goals;
    }


}
