package com.example.keepsake;

public class User {
    private String firstname, lastname, email, currentFamilyName, username, uuid, image;

    public User(String firstname, String lastname, String username) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
    }

    public User() { }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String name) {
        this.firstname = name;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String name) {
        this.lastname = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String id) {
        this.uuid = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String img) {
        this.uuid = img;
    }

    public String getCurrentFamilyName(){ return currentFamilyName;}

    public void setCurrentFamilyName(String newFamilyName){ this.currentFamilyName = newFamilyName;}

}
