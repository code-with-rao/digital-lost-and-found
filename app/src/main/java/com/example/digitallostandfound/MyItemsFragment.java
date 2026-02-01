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
import java.util.List;

public class MyItemsFragment extends Fragment {

    private RecyclerView rvMyItems;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_items, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rvMyItems = view.findViewById(R.id.rvMyItems);
        rvMyItems.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(getContext(), itemList);
        rvMyItems.setAdapter(adapter);

        loadMyItems();

        return view;
    }

    private void loadMyItems() {
        itemList.clear();
        int userId = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE).getInt("user_id", -1);
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ITEMS, null, 
                DatabaseHelper.COLUMN_ITEM_USER_ID + " = ?", new String[]{String.valueOf(userId)}, 
                null, null, DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_CATEGORY));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_DESCRIPTION));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_TYPE));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_LATITUDE));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_LONGITUDE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_STATUS));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_DATE));

                String imagePath = null;
                Cursor imgCursor = db.query(DatabaseHelper.TABLE_ITEM_IMAGES, new String[]{"image_path"}, 
                        "item_id = ?", new String[]{String.valueOf(id)}, null, null, null, "1");
                if (imgCursor != null && imgCursor.moveToFirst()) {
                    imagePath = imgCursor.getString(0);
                    imgCursor.close();
                }

                itemList.add(new Item(id, userId, name, category, desc, type, lat, lon, status, date, imagePath));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}
