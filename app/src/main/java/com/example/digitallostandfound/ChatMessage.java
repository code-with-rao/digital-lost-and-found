package com.example.digitallostandfound;

public class ChatMessage {
    private int id;
    private int senderId;
    private int receiverId;
    private int itemId;
    private String message;
    private String timestamp;

    public ChatMessage(int id, int senderId, int receiverId, int itemId, String message, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.itemId = itemId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public int getItemId() { return itemId; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}
