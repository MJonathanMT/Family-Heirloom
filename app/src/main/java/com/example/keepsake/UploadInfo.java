package com.example.keepsake;

public class UploadInfo {

    public String name, familyID, privacy, owner, description, url, startDate;

    public UploadInfo(){}

    public UploadInfo(String name, String familyID, String privacy, String owner,
                      String description, String url, String date) {
        this.name = name;
        this.familyID = familyID;
        this.privacy = privacy;
        this.owner = owner;
        this.description = description;
        this.url = url;
        this.startDate = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyID() {
        return familyID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }
}