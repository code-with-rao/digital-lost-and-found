package com.example.digitallostandfound;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvUserName;
    private EditText etSearch;
    private RecyclerView rvNearbyItems;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tvUserName = view.findViewById(R.id.tvUserName);
        etSearch = view.findViewById(R.id.etSearch);
        rvNearbyItems = view.findViewById(R.id.rvNearbyItems);

        String name = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE).getString("user_name", "User");
        tvUserName.setText(name);

        view.findViewById(R.id.btnReportLost).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportItemActivity.class);
            intent.putExtra("type", "lost");
            startActivity(intent);
        });

        view.findViewById(R.id.btnReportFound).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportItemActivity.class);
            intent.putExtra("type", "found");
            startActivity(intent);
        });

        rvNearbyItems.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(getContext(), itemList);
        rvNearbyItems.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadItems("");

        return view;
    }

    private void loadItems(String query) {
        itemList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_ITEM_STATUS + " = ?";
        List<String> selectionArgsList = new ArrayList<>();
        selectionArgsList.add("approved");

        if (!query.isEmpty()) {
            selection += " AND (" + DatabaseHelper.COLUMN_ITEM_NAME + " LIKE ? OR " + DatabaseHelper.COLUMN_ITEM_CATEGORY + " LIKE ?)";
            selectionArgsList.add("%" + query + "%");
            selectionArgsList.add("%" + query + "%");
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = db.query(DatabaseHelper.TABLE_ITEMS, null,
                selection, selectionArgs,
                null, null, DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_USER_ID));
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

    @Override
    public void onResume() {
        super.onResume();
        loadItems(etSearch.getText().toString());
    }
}
