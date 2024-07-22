package sg.edu.np.mad.p04_team4.Friendship_Event;

import java.io.Serializable;

public class User_events implements Serializable {
    private Long id; // Change to Long to match the type in Firebase
    private boolean challange_e;
    private boolean milestone_e;
    private boolean goals_e;
    private String challange;
    private String milestones;
    private boolean milestones_empty; // Change to boolean to match the logic
    private String goals;

    public User_events() {
        // Default constructor required for calls to DataSnapshot.getValue(User_events.class)
    }

    public User_events(Long id, String challange_info, String milestones_info, String goals_info, boolean challange, boolean milestone, boolean goals) {
        this.id = id;
        this.challange_e = challange;
        this.milestone_e = milestone;
        this.goals_e = goals;
        this.challange = challange_info;
        this.milestones = milestones_info;
        this.goals = goals_info;
    }

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isChallange_e() {
        return challange_e;
    }

    public void setChallange_e(boolean challange_e) {
        this.challange_e = challange_e;
    }

    public boolean isMilestone_e() {
        return milestone_e;
    }

    public void setMilestone_e(boolean milestone_e) {
        this.milestone_e = milestone_e;
    }

    public boolean isGoals_e() {
        return goals_e;
    }

    public void setGoals_e(boolean goals_e) {
        this.goals_e = goals_e;
    }

    public String getChallange() {
        return challange;
    }

    public void setChallange(String challange) {
        this.challange = challange;
    }

    public String getMilestones() {
        return milestones;
    }

    public void setMilestones(String milestones) {
        this.milestones = milestones;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }
}
