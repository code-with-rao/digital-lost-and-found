package com.example.digitallostandfound;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView rvAdminList;
    private TabLayout adminTabLayout;
    private DatabaseHelper dbHelper;
    private List<Item> itemList;
    private List<Claim> claimList;
    private AdminItemAdapter itemAdapter;
    private ClaimAdapter claimAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        rvAdminList = findViewById(R.id.rvAdminList);
        adminTabLayout = findViewById(R.id.adminTabLayout);
        Button btnLogout = findViewById(R.id.btnLogoutAdmin);

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });

        rvAdminList.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        claimList = new ArrayList<>();
        itemAdapter = new AdminItemAdapter(this, itemList);
        claimAdapter = new ClaimAdapter(this, claimList);

        adminTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadPendingItems();
                } else {
                    loadClaimRequests();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Default load
        loadPendingItems();
    }

    private void loadPendingItems() {
        itemList.clear();
        rvAdminList.setAdapter(itemAdapter);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ITEMS, null, 
                DatabaseHelper.COLUMN_ITEM_STATUS + " = ?", new String[]{"pending"}, 
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
        itemAdapter.notifyDataSetChanged();
    }

    private void loadClaimRequests() {
        claimList.clear();
        rvAdminList.setAdapter(claimAdapter);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT c.*, i.name as item_name, u.name as claimer_name " +
                "FROM " + DatabaseHelper.TABLE_CLAIMS + " c " +
                "JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON c.item_id = i.id " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON c.claimer_id = u.id " +
                "WHERE c.status = 'pending'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("item_id"));
                int claimerId = cursor.getInt(cursor.getColumnIndexOrThrow("claimer_id"));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                String claimerName = cursor.getString(cursor.getColumnIndexOrThrow("claimer_name"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                claimList.add(new Claim(id, itemId, claimerId, itemName, claimerName, desc, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
        claimAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adminTabLayout.getSelectedTabPosition() == 0) {
            loadPendingItems();
        } else {
            loadClaimRequests();
        }
    }
}
