package com.example.keepsake;

public class OwnershipRecord {
    private String startDate, endDate, ownerID, familyID, privacy, memory;

    public OwnershipRecord(String startDate, String endDate, String ownerID, String familyID, String privacy, String memory) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.ownerID = ownerID;
        this.familyID = familyID;
        this.privacy = privacy;
    }

    public OwnershipRecord() {
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getFamilyID() {
        return familyID;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
