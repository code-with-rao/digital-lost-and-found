package com.example.digitallostandfound;

public class ChatRoom {
    private int itemId;
    private int otherUserId;
    private String otherUserName;
    private String itemName;

    public ChatRoom(int itemId, int otherUserId, String otherUserName, String itemName) {
        this.itemId = itemId;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.itemName = itemName;
    }

    public int getItemId() { return itemId; }
    public int getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getItemName() { return itemName; }
}
