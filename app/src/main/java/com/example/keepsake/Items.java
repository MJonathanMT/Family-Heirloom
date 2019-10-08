package com.example.keepsake;

public class Items {

    String name, description, privacy, owner, familyName, url;


    public Items(String name, String description, String owner, String familyName, String privacy) {
        this.name = name;
        this.description= description;
        this.privacy = privacy;
        this.owner = owner;
        this.familyName = familyName;

    }
    public Items(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }




}

