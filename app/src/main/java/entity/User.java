package entity;

import constants.UserType;

public class User {
    private UserType position;
    private String email, name;

    public User(String email, String name, UserType position) {
        this.position = position;
        this.email = email;
        this.name = name;
    }

    public void set(String email, String name, UserType position) {
        this.position = position;
        this.email = email;
        this.name = name;
    }

    public UserType getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
