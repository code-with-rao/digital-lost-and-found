package com.example.digitallostandfound;

public class Item {
    private int id;
    private int userId;
    private String name;
    private String category;
    private String description;
    private String type; // lost or found
    private double latitude;
    private double longitude;
    private String status;
    private String date;
    private String imagePath;

    public Item(int id, int userId, String name, String category, String description, String type, double latitude, double longitude, String status, String date, String imagePath) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.date = date;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getImagePath() { return imagePath; }
}
