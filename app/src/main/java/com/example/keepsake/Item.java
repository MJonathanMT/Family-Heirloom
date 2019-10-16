package com.example.keepsake;

public class Item {

    private String name, description, privacy, owner, familyName, url, uuid;


    public Item(String name, String description, String owner, String familyName, String privacy, String url) {
        this.name = name;
        this.description= description;
        this.privacy = privacy;
        this.owner = owner;
        this.familyName = familyName;
        this.url = url;

    }
    public Item(){}

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

    public String getUrl() { return url; }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setUUID(String id){
        this.uuid = id;
    }

    public String getUUID(){
        return uuid;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setUrl(String url) { this.url = url; }

}

