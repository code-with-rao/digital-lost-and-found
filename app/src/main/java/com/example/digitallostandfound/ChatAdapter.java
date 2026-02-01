package com.example.digitallostandfound;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> messages;
    private int currentUserId;

    public ChatAdapter(List<ChatMessage> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getMessage());
        holder.tvTimestamp.setText(message.getTimestamp());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageBubble.getLayoutParams();
        if (message.getSenderId() == currentUserId) {
            holder.messageContainer.setGravity(Gravity.END);
            holder.messageBubble.setBackgroundResource(R.drawable.chat_bubble_sent);
            params.setMargins(100, 0, 0, 0);
        } else {
            holder.messageContainer.setGravity(Gravity.START);
            holder.messageBubble.setBackgroundResource(R.drawable.chat_bubble_received);
            params.setMargins(0, 0, 100, 0);
        }
        holder.messageBubble.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        LinearLayout messageBubble, messageContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            messageBubble = itemView.findViewById(R.id.messageBubble);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }
    }
}
