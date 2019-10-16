package com.example.keepsake;

public class Family {
    private String familyName, UUID;

    public Family(String name, String uuid) {
        this.familyName = name;
        this.UUID = uuid;
    }

    public Family() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
