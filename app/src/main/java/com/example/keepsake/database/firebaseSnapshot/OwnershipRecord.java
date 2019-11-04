package com.example.keepsake.database.firebaseSnapshot;

public class OwnershipRecord {
    private String startDate, endDate, ownerID, familyID, privacy, memory;

    /**
     * Constructor of a ownership record class.
     */
    public OwnershipRecord() { }

    /**
     * Ownership record class to be called to create an instance of the class.
     * @param startDate ownership of an item start date
     * @param endDate ownership of an item end date
     * @param ownerID the owner ID of an item
     * @param familyID family ID of a group
     * @param privacy settings of a privacy of an item
     * @param memory period of ownership of an item
     */
    public OwnershipRecord(String startDate, String endDate, String ownerID, String familyID, String privacy, String memory) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.ownerID = ownerID;
        this.familyID = familyID;
        this.privacy = privacy;
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