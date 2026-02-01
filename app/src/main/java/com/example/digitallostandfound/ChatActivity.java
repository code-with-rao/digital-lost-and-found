package com.example.digitallostandfound;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList;
    private DatabaseHelper dbHelper;
    private int currentUserId;
    private int receiverId;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHelper = new DatabaseHelper(this);
        currentUserId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1);
        receiverId = getIntent().getIntExtra("receiver_id", -1);
        itemId = getIntent().getIntExtra("item_id", -1);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList, currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        loadMessages();

        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        messageList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "((" + DatabaseHelper.COLUMN_CHAT_SENDER_ID + " = ? AND " + DatabaseHelper.COLUMN_CHAT_RECEIVER_ID + " = ?) OR ("
                + DatabaseHelper.COLUMN_CHAT_SENDER_ID + " = ? AND " + DatabaseHelper.COLUMN_CHAT_RECEIVER_ID + " = ?)) AND "
                + DatabaseHelper.COLUMN_CHAT_ITEM_ID + " = ?";
        String[] selectionArgs = {
                String.valueOf(currentUserId), String.valueOf(receiverId),
                String.valueOf(receiverId), String.valueOf(currentUserId),
                String.valueOf(itemId)
        };

        Cursor cursor = db.query(DatabaseHelper.TABLE_CHATS, null, selection, selectionArgs, null, null, DatabaseHelper.COLUMN_CREATED_AT + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                int senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHAT_SENDER_ID));
                int recId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHAT_RECEIVER_ID));
                int itmId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHAT_ITEM_ID));
                String msg = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHAT_MESSAGE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT));

                messageList.add(new ChatMessage(id, senderId, recId, itmId, msg, time));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
        if (messageList.size() > 0) {
            rvMessages.scrollToPosition(messageList.size() - 1);
        }
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CHAT_SENDER_ID, currentUserId);
        values.put(DatabaseHelper.COLUMN_CHAT_RECEIVER_ID, receiverId);
        values.put(DatabaseHelper.COLUMN_CHAT_ITEM_ID, itemId);
        values.put(DatabaseHelper.COLUMN_CHAT_MESSAGE, message);

        long id = db.insert(DatabaseHelper.TABLE_CHATS, null, values);
        if (id != -1) {
            etMessage.setText("");
            loadMessages();
        }
    }
}
