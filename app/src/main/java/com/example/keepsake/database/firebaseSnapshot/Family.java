package com.example.keepsake.database.firebaseSnapshot;

public class Family {
    private String familyName, familyID;

    public Family(String name, String familyID) {
        this.familyName = name;
        this.familyID = familyID;
    }

    public Family() {
    }

    public String getFamilyID() {
        return familyID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
