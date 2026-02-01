package com.example.digitallostandfound;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "digital_lost_and_found.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ITEMS = "items";
    public static final String TABLE_ITEM_IMAGES = "item_images";
    public static final String TABLE_CHATS = "chats";
    public static final String TABLE_CLAIMS = "claims";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_ADMIN_LOGS = "admin_logs";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Users table columns
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role"; // user or admin
    public static final String COLUMN_USER_STATUS = "status"; // active or flagged

    // Items table columns
    public static final String COLUMN_ITEM_USER_ID = "user_id";
    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_CATEGORY = "category";
    public static final String COLUMN_ITEM_DESCRIPTION = "description";
    public static final String COLUMN_ITEM_TYPE = "type"; // lost or found
    public static final String COLUMN_ITEM_LATITUDE = "latitude";
    public static final String COLUMN_ITEM_LONGITUDE = "longitude";
    public static final String COLUMN_ITEM_STATUS = "status"; // pending, approved, rejected, resolved
    public static final String COLUMN_ITEM_DATE = "item_date";

    // Chat table columns
    public static final String COLUMN_CHAT_SENDER_ID = "sender_id";
    public static final String COLUMN_CHAT_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_CHAT_ITEM_ID = "item_id";
    public static final String COLUMN_CHAT_MESSAGE = "message";
    public static final String COLUMN_CHAT_IMAGE_PATH = "image_path";

    // Table Create Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_USER_ROLE + " TEXT,"
            + COLUMN_USER_STATUS + " TEXT DEFAULT 'active',"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ITEM_USER_ID + " INTEGER,"
            + COLUMN_ITEM_NAME + " TEXT,"
            + COLUMN_ITEM_CATEGORY + " TEXT,"
            + COLUMN_ITEM_DESCRIPTION + " TEXT,"
            + COLUMN_ITEM_TYPE + " TEXT,"
            + COLUMN_ITEM_LATITUDE + " REAL,"
            + COLUMN_ITEM_LONGITUDE + " REAL,"
            + COLUMN_ITEM_STATUS + " TEXT DEFAULT 'pending',"
            + COLUMN_ITEM_DATE + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static final String CREATE_TABLE_ITEM_IMAGES = "CREATE TABLE " + TABLE_ITEM_IMAGES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "item_id INTEGER,"
            + "image_path TEXT" + ")";

    private static final String CREATE_TABLE_CHATS = "CREATE TABLE " + TABLE_CHATS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CHAT_SENDER_ID + " INTEGER,"
            + COLUMN_CHAT_RECEIVER_ID + " INTEGER,"
            + COLUMN_CHAT_ITEM_ID + " INTEGER,"
            + COLUMN_CHAT_MESSAGE + " TEXT,"
            + COLUMN_CHAT_IMAGE_PATH + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static final String CREATE_TABLE_CLAIMS = "CREATE TABLE " + TABLE_CLAIMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "item_id INTEGER,"
            + "claimer_id INTEGER,"
            + "description TEXT,"
            + "status TEXT DEFAULT 'pending',"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "user_id INTEGER,"
            + "title TEXT,"
            + "message TEXT,"
            + "is_read INTEGER DEFAULT 0,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static final String CREATE_TABLE_ADMIN_LOGS = "CREATE TABLE " + TABLE_ADMIN_LOGS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "admin_id INTEGER,"
            + "action TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ITEMS);
        db.execSQL(CREATE_TABLE_ITEM_IMAGES);
        db.execSQL(CREATE_TABLE_CHATS);
        db.execSQL(CREATE_TABLE_CLAIMS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);
        db.execSQL(CREATE_TABLE_ADMIN_LOGS);

        // Insert default admin
        db.execSQL("INSERT INTO " + TABLE_USERS + " (name, email, password, role) VALUES ('Admin', 'admin@example.com', 'admin123', 'admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN_LOGS);
        onCreate(db);
    }
}
