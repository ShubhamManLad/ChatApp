package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{

    private Context context;
    private List<UserModel> userList;

    public UserAdapter(Context context) {
        this.context = context;
        userList = new ArrayList<>();
    }

    public void add(UserModel userModel){
        userList.add(userModel);
        notifyDataSetChanged();
    }
    public void clear(){
        userList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users,parent,false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel userModel = userList.get(position);
        holder.user_name_textView.setText(userModel.getName());
        holder.user_email_textView.setText(userModel.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context,ChatActivity.class);
                intent.putExtra("ID", userModel.getUserId());
                intent.putExtra("NAME", userModel.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView user_name_textView, user_email_textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name_textView = itemView.findViewById(R.id.user_name_textView);
            user_email_textView = itemView.findViewById(R.id.user_email_textView);
        }
    }
}
