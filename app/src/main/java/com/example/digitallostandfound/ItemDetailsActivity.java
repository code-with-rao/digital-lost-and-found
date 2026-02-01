package com.example.digitallostandfound;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ItemDetailsActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvDetailStatus, tvDetailName, tvDetailCategory, tvDetailDate, tvDetailDesc;
    private Button btnContact, btnClaim;
    private DatabaseHelper dbHelper;
    private int itemId;
    private int reporterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        dbHelper = new DatabaseHelper(this);
        itemId = getIntent().getIntExtra("item_id", -1);

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        btnContact = findViewById(R.id.btnContact);
        btnClaim = findViewById(R.id.btnClaim);

        loadItemDetails();

        btnContact.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, ChatActivity.class);
            intent.putExtra("receiver_id", reporterId);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
        });

        btnClaim.setOnClickListener(v -> showClaimDialog());
    }

    private void showClaimDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Claim Item");

        final EditText input = new EditText(this);
        input.setHint("Enter reason for claiming this item");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (!reason.isEmpty()) {
                submitClaim(reason);
            } else {
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void submitClaim(String reason) {
        int currentUserId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("item_id", itemId);
        values.put("claimer_id", currentUserId);
        values.put("description", reason);
        values.put("status", "pending");

        long newRowId = db.insert(DatabaseHelper.TABLE_CLAIMS, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Claim request submitted successfully", Toast.LENGTH_LONG).show();
            btnClaim.setEnabled(false);
            btnClaim.setText("Claim Pending");
        } else {
            Toast.makeText(this, "Failed to submit claim", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadItemDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ITEMS, null, "id = ?", new String[]{String.valueOf(itemId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            reporterId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_CATEGORY));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_DESCRIPTION));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_TYPE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_DATE));

            tvDetailName.setText(name);
            tvDetailCategory.setText("Category: " + category);
            tvDetailDesc.setText(desc);
            tvDetailStatus.setText(type.toUpperCase());
            tvDetailDate.setText("Reported on: " + date);

            // Hide contact button if it's user's own item
            int currentUserId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1);
            if (currentUserId == reporterId) {
                btnContact.setVisibility(View.GONE);
                btnClaim.setVisibility(View.GONE);
            }

            // Check if user already claimed this item
            Cursor claimCursor = db.query(DatabaseHelper.TABLE_CLAIMS, new String[]{"status"},
                    "item_id = ? AND claimer_id = ?", new String[]{String.valueOf(itemId), String.valueOf(currentUserId)}, null, null, null);
            if (claimCursor != null && claimCursor.moveToFirst()) {
                String claimStatus = claimCursor.getString(0);
                btnClaim.setEnabled(false);
                btnClaim.setText("Claim " + claimStatus.substring(0, 1).toUpperCase() + claimStatus.substring(1));
                claimCursor.close();
            }

            // Load image
            Cursor imgCursor = db.query(DatabaseHelper.TABLE_ITEM_IMAGES, new String[]{"image_path"}, 
                    "item_id = ?", new String[]{String.valueOf(itemId)}, null, null, null, "1");
            if (imgCursor != null && imgCursor.moveToFirst()) {
                String imagePath = imgCursor.getString(0);
                Glide.with(this).load(imagePath).placeholder(android.R.drawable.ic_menu_gallery).into(ivDetailImage);
                imgCursor.close();
            }
            cursor.close();
        }
    }
}
