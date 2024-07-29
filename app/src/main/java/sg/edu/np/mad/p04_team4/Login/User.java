package sg.edu.np.mad.p04_team4.Login;

import java.io.Serializable;

import sg.edu.np.mad.p04_team4.Friendship_Event.User_events;

public class User implements Serializable {
    private String id;
    private String name;
    private String password;
    private String phone;
    private User_events events;

    // Default constructor
    public User() {
        this.id = "";
        this.name = "";
        this.password = "";
        this.phone = "";
        this.events = new User_events(0L, "", "", "", true, true, true);
    }

    // Constructor with all parameters
    public User(String id, String name, String password, String phone, User_events events) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.events = events;
    }

    // Constructor with name and password only
    public User(String name, String password) {
        this.id = ""; // Default value
        this.name = name;
        this.password = password;
        this.events = new User_events(0L, "", "", "", true, true, true);
    }

    // Constructor for id, name, password, and phone
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.events = new User_events(0L, "", "", "", true, true, true); // Initialize events
    }

    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    // Method to compare passwords
    public boolean comparePassword(String passwordToCompare) {
        return this.password.equals(passwordToCompare);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User_events getEvents() {
        return events;
    }

    public void setEvents(User_events events) {
        this.events = events;
    }
}