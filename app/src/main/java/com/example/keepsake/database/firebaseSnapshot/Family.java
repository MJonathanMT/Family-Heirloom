package com.example.keepsake.database.firebaseSnapshot;

public class Family {
    private String familyName, familyID;

    /**
     * Constructor of a family class.
     */
    public Family() { }

    /**
     * Family class to be called to create an instance of the class.
     * @param name family name group
     * @param familyID family ID of a group
     */
    public Family(String name, String familyID) {
        this.familyName = name;
        this.familyID = familyID;
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