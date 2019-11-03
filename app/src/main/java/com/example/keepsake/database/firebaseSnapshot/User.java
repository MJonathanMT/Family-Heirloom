package com.example.keepsake.database.firebaseSnapshot;

public class User {
    private String firstName, lastName, email, userSession, username, userID, url;

    public User(String firstname, String lastname, String email, String url, String username, String userSession) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.url = url;
        this.username = username;
        this.userSession = userSession;
    }

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

    public String getUserID() { return userID; }

    public void setUserID(String userID) {
        this.userID = userID;
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
