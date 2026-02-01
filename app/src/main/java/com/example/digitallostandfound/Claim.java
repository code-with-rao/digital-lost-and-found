package com.example.digitallostandfound;

public class Claim {
    private int id;
    private int itemId;
    private int claimerId;
    private String itemName;
    private String claimerName;
    private String description;
    private String status;

    public Claim(int id, int itemId, int claimerId, String itemName, String claimerName, String description, String status) {
        this.id = id;
        this.itemId = itemId;
        this.claimerId = claimerId;
        this.itemName = itemName;
        this.claimerName = claimerName;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public int getItemId() { return itemId; }
    public int getClaimerId() { return claimerId; }
    public String getItemName() { return itemName; }
    public String getClaimerName() { return claimerName; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}
