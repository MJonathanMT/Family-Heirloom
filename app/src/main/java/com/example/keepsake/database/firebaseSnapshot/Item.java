package com.example.keepsake.database.firebaseSnapshot;

public class Item {
    private String name;
    private String description;
    private String privacy;
    private String ownerID;
    private String familyID;
    private String url;
    private String startDate;
    private String itemID;

    public Item(){}

    public Item(String name, String description, String privacy, String ownerID, String familyID, String url, String startDate) {
        this.name = name;
        this.description = description;
        this.privacy = privacy;
        this.ownerID = ownerID;
        this.familyID = familyID;
        this.url = url;
        this.startDate = startDate;
    }

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

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String owner) {
        this.ownerID = owner;
    }

    public String getFamilyID() {
        return familyID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
