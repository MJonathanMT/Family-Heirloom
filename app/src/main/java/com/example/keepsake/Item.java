package com.example.keepsake;

public class Item {
    private String name;
    private String description;
    private String privacy;
    private String owner;
    private String familyID;
    private String url;
    private String startDate;
    private String itemID;

    public Item(String name, String description, String privacy, String owner, String familyId, String url, String startDate) {
        this.name = name;
        this.description = description;
        this.privacy = privacy;
        this.owner = owner;
        this.familyID = familyId;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFamilyId() {
        return familyID;
    }

    public void setFamilyId(String familyId) {
        this.familyID = familyID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getItemId() {
        return itemID;
    }

    public void setItemId(String itemId) {
        this.itemID = itemId;
    }

    public Item(){}

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
