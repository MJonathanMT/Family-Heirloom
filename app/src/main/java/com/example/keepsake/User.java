package com.example.keepsake;

public class User {
    private String firstName, lastName, email, userSession, username, uuid, url;

    public User(String firstname, String lastname, String email, String url, String username) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.url = url;
        this.username = username;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getUserSession() {
        return userSession;
    }
    public String getUUID() {
        return uuid;
    }

    public void setUUID(String id) {
        this.uuid = id;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
