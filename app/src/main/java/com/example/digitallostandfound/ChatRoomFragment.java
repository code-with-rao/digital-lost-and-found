package com.example.digitallostandfound;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoomFragment extends Fragment {

    private RecyclerView rvChatRooms;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> chatRoomList;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rvChatRooms = view.findViewById(R.id.rvChatRooms);
        rvChatRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        
        chatRoomList = new ArrayList<>();
        int currentUserId = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE).getInt("user_id", -1);
        adapter = new ChatRoomAdapter(getContext(), chatRoomList, currentUserId);
        rvChatRooms.setAdapter(adapter);

        loadChatRooms();

        return view;
    }

    private void loadChatRooms() {
        chatRoomList.clear();
        int currentUserId = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE).getInt("user_id", -1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to find unique conversations for the current user
        String query = "SELECT DISTINCT " + DatabaseHelper.COLUMN_CHAT_ITEM_ID + ", " +
                DatabaseHelper.COLUMN_CHAT_SENDER_ID + ", " +
                DatabaseHelper.COLUMN_CHAT_RECEIVER_ID + " FROM " + DatabaseHelper.TABLE_CHATS +
                " WHERE " + DatabaseHelper.COLUMN_CHAT_SENDER_ID + " = ? OR " +
                DatabaseHelper.COLUMN_CHAT_RECEIVER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUserId), String.valueOf(currentUserId)});

        Set<String> processedRooms = new HashSet<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int itemId = cursor.getInt(0);
                int senderId = cursor.getInt(1);
                int receiverId = cursor.getInt(2);

                int otherUserId = (senderId == currentUserId) ? receiverId : senderId;
                String roomKey = itemId + "_" + Math.min(currentUserId, otherUserId) + "_" + Math.max(currentUserId, otherUserId);

                if (!processedRooms.contains(roomKey)) {
                    processedRooms.add(roomKey);
                    
                    // Fetch details
                    String otherUserName = "User";
                    Cursor userCursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COLUMN_USER_NAME}, 
                            "id = ?", new String[]{String.valueOf(otherUserId)}, null, null, null);
                    if (userCursor != null && userCursor.moveToFirst()) {
                        otherUserName = userCursor.getString(0);
                        userCursor.close();
                    }

                    String itemName = "Item";
                    Cursor itemCursor = db.query(DatabaseHelper.TABLE_ITEMS, new String[]{DatabaseHelper.COLUMN_ITEM_NAME}, 
                            "id = ?", new String[]{String.valueOf(itemId)}, null, null, null);
                    if (itemCursor != null && itemCursor.moveToFirst()) {
                        itemName = itemCursor.getString(0);
                        itemCursor.close();
                    }

                    chatRoomList.add(new ChatRoom(itemId, otherUserId, otherUserName, itemName));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}
