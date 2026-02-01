package com.example.digitallostandfound;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private Context context;
    private List<ChatRoom> chatRoomList;
    private int currentUserId;

    public ChatRoomAdapter(Context context, List<ChatRoom> chatRoomList, int currentUserId) {
        this.context = context;
        this.chatRoomList = chatRoomList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom room = chatRoomList.get(position);
        holder.tvUserName.setText(room.getOtherUserName());
        holder.tvItemName.setText("Item: " + room.getItemName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiver_id", room.getOtherUserId());
            intent.putExtra("item_id", room.getItemId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvItemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvOtherUserName);
            tvItemName = itemView.findViewById(R.id.tvChatRoomItemName);
        }
    }
}
