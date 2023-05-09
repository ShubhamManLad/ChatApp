package com.example.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyNewViewHolder> {

    private Context context;
    private List<MessageModel> messageList;

    public MessageAdapter(Context context) {
        this.context = context;
        messageList = new ArrayList<>();
    }

    public void add(MessageModel messageModel) {
        messageList.add(messageModel);
        notifyDataSetChanged();
    }

    public void clear() {
        messageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyNewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages, parent, false);
        return new MyNewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewViewHolder holder, int position) {
        MessageModel messageModel = messageList.get(position);
        holder.message_textView.setText(messageModel.getMessage());
        if (messageModel.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.message_textView.setBackgroundColor(context.getResources().getColor(R.color.teal_200));
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.message_textView.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyNewViewHolder extends RecyclerView.ViewHolder {

        private TextView message_textView;
        private CardView message_cardView;

        public MyNewViewHolder(@NonNull View itemView) {
            super(itemView);
            message_textView = itemView.findViewById(R.id.message_textView);
            message_cardView = itemView.findViewById(R.id.message_cardView);
        }
    }
}