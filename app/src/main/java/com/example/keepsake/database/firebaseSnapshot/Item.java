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

    /**
     * Constructor of an item class.
     */
    public Item(){}

    /**
     * Item class to be called to create an instance of the class.
     * @param name an item name
     * @param description an item description
     * @param privacy settings of a privacy of an item
     * @param ownerID the owner ID of an item
     * @param familyID the family ID of an item to be posted
     * @param url image url of an item
     * @param startDate ownership of an item start date
     */
    public Item(String name, String description, String privacy, String ownerID, String familyID, String url, String startDate) {
        this.name = name;
        this.description = description;
        this.privacy = privacy;
        this.ownerID = ownerID;
        this.familyID = familyID;
        this.url = url;
        this.startDate = startDate;
    }

    /**
     * Gets the name of a item
     * @return current name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of an item
     * @param name item name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of an item
     * @return current description of an item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of an item
     * @param description item description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the privacy of an item
     * @return current privacy of an item
     */
    public String getPrivacy() {
        return privacy;
    }

    /**
     * Sets the privacy of an item
     * @param privacy item privacy
     */
    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    /**
     * Gets the owner ID of an item
     * @return current owner ID of an item
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Sets the owner ID of an item
     * @param owner item owner
     */
    public void setOwnerID(String owner) {
        this.ownerID = owner;
    }

    /**
     * Gets the family ID of an item to be posted
     * @return current family ID selected to the item to be posted
     */
    public String getFamilyID() {
        return familyID;
    }

    /**
     * Sets the family ID of an item to be posted
     * @param familyID item posted to the family ID
     */
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