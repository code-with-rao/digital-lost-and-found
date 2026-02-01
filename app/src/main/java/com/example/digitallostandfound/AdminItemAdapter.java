package com.example.digitallostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private DatabaseHelper dbHelper;

    public AdminItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvType.setText("Type: " + item.getType().toUpperCase());
        holder.tvDesc.setText(item.getDescription());

        // Show/hide approve/reject buttons based on status
        if ("pending".equals(item.getStatus())) {
            holder.layoutActions.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        holder.btnApprove.setOnClickListener(v -> updateStatus(item.getId(), "approved", holder.getAdapterPosition()));
        holder.btnReject.setOnClickListener(v -> updateStatus(item.getId(), "rejected", holder.getAdapterPosition()));
        holder.btnDelete.setOnClickListener(v -> confirmDelete(item.getId(), holder.getAdapterPosition()));
    }

    private void updateStatus(int itemId, String status, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ITEM_STATUS, status);
        db.update(DatabaseHelper.TABLE_ITEMS, values, "id = ?", new String[]{String.valueOf(itemId)});
        
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
        Toast.makeText(context, "Item " + status, Toast.LENGTH_SHORT).show();
    }

    private void confirmDelete(int itemId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item post permanently?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem(itemId, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem(int itemId, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_ITEMS, "id = ?", new String[]{String.valueOf(itemId)});
        db.delete(DatabaseHelper.TABLE_ITEM_IMAGES, "item_id = ?", new String[]{String.valueOf(itemId)});
        
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
        Toast.makeText(context, "Item deleted permanently", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDesc;
        Button btnApprove, btnReject;
        ImageButton btnDelete;
        View layoutActions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminItemName);
            tvType = itemView.findViewById(R.id.tvAdminItemType);
            tvDesc = itemView.findViewById(R.id.tvAdminItemDesc);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}
