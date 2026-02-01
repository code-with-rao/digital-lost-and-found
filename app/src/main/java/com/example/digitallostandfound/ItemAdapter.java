package com.example.digitallostandfound;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemCategory.setText(item.getCategory());
        holder.tvItemStatus.setText(item.getType().toUpperCase());
        holder.tvItemDate.setText("Date: " + item.getDate());

        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Glide.with(context).load(item.getImagePath()).into(holder.ivItemImage);
        } else {
            holder.ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("item_id", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemStatus, tvItemName, tvItemCategory, tvItemDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
        }
    }
}
