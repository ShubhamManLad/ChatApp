package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    String receiverId;
    String receiverName;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceSender, databaseReferenceReceiver;

    String senderRoom, receiverRoom;

    EditText message_text;
    ImageView send_image;

    MessageAdapter messageAdapter;
    RecyclerView chat_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        message_text = findViewById(R.id.message_editText);
        send_image = findViewById(R.id.send_image);

        messageAdapter = new MessageAdapter(this);
        chat_recyclerView = findViewById(R.id.chat_recyclerView);
        chat_recyclerView.setAdapter(messageAdapter);
        chat_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        receiverId = getIntent().getStringExtra("ID");
        receiverName = getIntent().getStringExtra("NAME");
        getSupportActionBar().setTitle(receiverName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        senderRoom = currentUser.getUid() + receiverId;
        receiverRoom = receiverId + currentUser.getUid();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceSender = firebaseDatabase.getReference("chats").child(senderRoom);
        databaseReferenceReceiver = firebaseDatabase.getReference("chats").child(receiverRoom);


        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = datasnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                message = message_text.getText().toString();
                if (!TextUtils.isEmpty(message.trim())){
                    sendMessage(message);
                }
                message_text.getText().clear();
            }
        });

    }

    private void sendMessage(String message) {
        Date date = new Date();
        //This method returns the time in millis
        long time = date.getTime();
        String timestamp = String.valueOf(time);
        String messageID = timestamp + UUID.randomUUID().toString();
        MessageModel messageModel = new MessageModel(message,messageID,mAuth.getUid());
        messageAdapter.add(messageModel);
        databaseReferenceSender.child(messageID).setValue(messageModel);
        databaseReferenceReceiver.child(messageID).setValue(messageModel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}