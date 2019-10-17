package com.example.keepsake;

public class Item {
    private String name;
    private String description;
    private String privacy;
    private String owner;
    private String familyId;
    private String url;

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
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    private String itemId;

    public Item(){}

    public Item(String name, String description, String privacy, String owner, String familyId, String url) {
        this.name = name;
        this.description = description;
        this.privacy = privacy;
        this.owner = owner;
        this.familyId = familyId;
        this.url = url;
    }
}
