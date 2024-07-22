package sg.edu.np.mad.p04_team4.Login;

import java.io.Serializable;

import sg.edu.np.mad.p04_team4.Friendship_Event.User_events;

public class User implements Serializable {
    private Long id;
    private String name;
    private String password;
    private User_events events;

    // Default constructor
    public User() {
        this.id = 0L;
        this.name = "";
        this.password = "";
        this.events = new User_events(0L, "", "", "", true, true, true);
    }

    // Constructor with all parameters
    public User(Long id, String name, String password, User_events events) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.events = events;
    }

    // Constructor with name and password only
    public User(String name, String password) {
        this.id = 0L; // Default value
        this.name = name;
        this.password = password;
        this.events = new User_events(0L, "", "", "", true, true, true);
    }

    // Constructor for id, name, and password
    public User(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.events = new User_events(id, "", "", "", true, true, true); // Initialize events
    }

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User_events getEvents() {
        return events;
    }

    public void setEvents(User_events events) {
        this.events = events;
    }
}
