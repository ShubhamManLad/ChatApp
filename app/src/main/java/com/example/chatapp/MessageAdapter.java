package com.example.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public void delete(MessageModel messageModel){
        messageList.remove(messageModel);
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
                holder.message_cardView.setCardBackgroundColor(context.getResources().getColor(R.color.teal_700));
                holder.message_layout.setGravity(Gravity.RIGHT);
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.message_cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                holder.message_layout.setGravity(Gravity.LEFT);
            }
        }

        if (messageModel.isChecked()){
            holder.message_check.setVisibility(View.VISIBLE);
        }
        else{
            holder.message_check.setVisibility(View.INVISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()){
                    @Override
                    public void setOnDoubleTapListener(@Nullable OnDoubleTapListener onDoubleTapListener) {
                        super.setOnDoubleTapListener(onDoubleTapListener);

                        String senderRoom = messageModel.getSenderId() + messageModel.getReceiverId();
                        String receiverRoom = messageModel.getReceiverId() + messageModel.getSenderId();

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReferenceSender = firebaseDatabase.getReference("chats").child(senderRoom);
                        DatabaseReference databaseReferenceReceiver = firebaseDatabase.getReference("chats").child(receiverRoom);

                        if (messageModel.isChecked()){
                            messageModel.setChecked(false);
                        }
                        else{
                            messageModel.setChecked(true);
                        }
                        databaseReferenceReceiver.child(messageModel.getMessageId()).setValue(messageModel);
                        databaseReferenceSender.child(messageModel.getMessageId()).setValue(messageModel);
                    }
                };
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                if (user.getUid().equals(messageModel.getSenderId())){


                    String senderRoom = messageModel.getSenderId() + messageModel.getReceiverId();
                    String receiverRoom = messageModel.getReceiverId() + messageModel.getSenderId();

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReferenceSender = firebaseDatabase.getReference("chats").child(senderRoom);
                    DatabaseReference databaseReferenceReceiver = firebaseDatabase.getReference("chats").child(receiverRoom);

                    delete(messageModel);

                    databaseReferenceReceiver.child(messageModel.getMessageId()).removeValue();
                    databaseReferenceSender.child(messageModel.getMessageId()).removeValue();
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyNewViewHolder extends RecyclerView.ViewHolder {

        private TextView message_textView;

        private ImageView message_check;
        private CardView message_cardView;
        private LinearLayout message_layout;

        public MyNewViewHolder(@NonNull View itemView) {
            super(itemView);
            message_textView = itemView.findViewById(R.id.message_textView);
            message_cardView = itemView.findViewById(R.id.message_cardView);
            message_check = itemView.findViewById(R.id.check_imageview);
            message_layout = itemView.findViewById(R.id.message_layout);
        }
    }
}