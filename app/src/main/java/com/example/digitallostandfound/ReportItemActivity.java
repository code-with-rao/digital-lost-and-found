package com.example.digitallostandfound;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etItemName, etCategory, etDescription;
    private MaterialButtonToggleGroup toggleGroup;
    private ImageView ivPreview;
    private Uri selectedImageUri;
    private DatabaseHelper dbHelper;
    private String itemType = "lost";
    
    private MapView mapPicker;
    private Marker selectedMarker;
    private double selectedLat = 0.0;
    private double selectedLon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_report_item);

        dbHelper = new DatabaseHelper(this);

        etItemName = findViewById(R.id.etItemName);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        toggleGroup = findViewById(R.id.toggleGroup);
        ivPreview = findViewById(R.id.ivPreview);
        mapPicker = findViewById(R.id.mapPicker);
        Button btnAddPhoto = findViewById(R.id.btnAddPhoto);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        setupMap();

        String type = getIntent().getStringExtra("type");
        if (type != null) {
            itemType = type;
            if (type.equals("found")) {
                toggleGroup.check(R.id.btnFound);
            } else {
                toggleGroup.check(R.id.btnLost);
            }
        }

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnLost) {
                    itemType = "lost";
                } else if (checkedId == R.id.btnFound) {
                    itemType = "found";
                }
            }
        });

        btnAddPhoto.setOnClickListener(v -> openGallery());
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void setupMap() {
        mapPicker.setMultiTouchControls(true);
        mapPicker.getController().setZoom(13.0);
        GeoPoint startPoint = new GeoPoint(0.0, 0.0);
        mapPicker.getController().setCenter(startPoint);

        selectedMarker = new Marker(mapPicker);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        selectedMarker.setTitle("Selected Location");

        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                selectedLat = p.getLatitude();
                selectedLon = p.getLongitude();

                selectedMarker.setPosition(p);
                if (!mapPicker.getOverlays().contains(selectedMarker)) {
                    mapPicker.getOverlays().add(selectedMarker);
                }
                mapPicker.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(receiver);
        mapPicker.getOverlays().add(0, eventsOverlay);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ivPreview.setVisibility(View.VISIBLE);
            ivPreview.setImageURI(selectedImageUri);
        }
    }

    private void submitReport() {
        String name = etItemName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (name.isEmpty() || category.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLat == 0.0 && selectedLon == 0.0) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ITEM_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_ITEM_NAME, name);
        values.put(DatabaseHelper.COLUMN_ITEM_CATEGORY, category);
        values.put(DatabaseHelper.COLUMN_ITEM_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_ITEM_TYPE, itemType);
        values.put(DatabaseHelper.COLUMN_ITEM_LATITUDE, selectedLat);
        values.put(DatabaseHelper.COLUMN_ITEM_LONGITUDE, selectedLon);
        values.put(DatabaseHelper.COLUMN_ITEM_DATE, date);
        values.put(DatabaseHelper.COLUMN_ITEM_STATUS, "pending");

        long itemId = db.insert(DatabaseHelper.TABLE_ITEMS, null, values);

        if (itemId != -1) {
            if (selectedImageUri != null) {
                ContentValues imgValues = new ContentValues();
                imgValues.put("item_id", itemId);
                imgValues.put("image_path", selectedImageUri.toString());
                db.insert(DatabaseHelper.TABLE_ITEM_IMAGES, null, imgValues);
            }
            Toast.makeText(this, "Report submitted successfully.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to submit report", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapPicker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapPicker.onPause();
    }
}
