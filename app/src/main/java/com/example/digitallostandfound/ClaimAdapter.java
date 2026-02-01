package com.example.digitallostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ViewHolder> {

    private Context context;
    private List<Claim> claimList;
    private DatabaseHelper dbHelper;

    public ClaimAdapter(Context context, List<Claim> claimList) {
        this.context = context;
        this.claimList = claimList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_claim_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Claim claim = claimList.get(position);
        holder.tvItemName.setText(claim.getItemName());
        holder.tvClaimerName.setText("Claimed by: " + claim.getClaimerName());
        holder.tvDescription.setText("Reason: " + claim.getDescription());

        holder.btnApprove.setOnClickListener(v -> updateClaimStatus(claim, "resolved", position));
        holder.btnReject.setOnClickListener(v -> updateClaimStatus(claim, "rejected", position));
    }

    private void updateClaimStatus(Claim claim, String status, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Update claim status
        ContentValues claimValues = new ContentValues();
        claimValues.put("status", status);
        db.update(DatabaseHelper.TABLE_CLAIMS, claimValues, "id = ?", new String[]{String.valueOf(claim.getId())});

        if ("resolved".equals(status)) {
            // Update item status to resolved as well
            ContentValues itemValues = new ContentValues();
            itemValues.put(DatabaseHelper.COLUMN_ITEM_STATUS, "resolved");
            db.update(DatabaseHelper.TABLE_ITEMS, itemValues, "id = ?", new String[]{String.valueOf(claim.getItemId())});
        }

        claimList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, claimList.size());
        Toast.makeText(context, "Claim " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return claimList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvClaimerName, tvDescription;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvClaimItemName);
            tvClaimerName = itemView.findViewById(R.id.tvClaimerName);
            tvDescription = itemView.findViewById(R.id.tvClaimDescription);
            btnApprove = itemView.findViewById(R.id.btnApproveClaim);
            btnReject = itemView.findViewById(R.id.btnRejectClaim);
        }
    }
}
