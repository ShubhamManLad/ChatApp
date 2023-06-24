package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    LinearLayout chat_layout;

    private boolean getPic = false;
    private Uri profilepic;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chat_layout = findViewById(R.id.chat_layout);
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

        loadBackgroundFromPreferences();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        senderRoom = currentUser.getUid() + receiverId;
        receiverRoom = receiverId + currentUser.getUid();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceSender = firebaseDatabase.getReference("chats").child(senderRoom);
        databaseReferenceReceiver = firebaseDatabase.getReference("chats").child(receiverRoom);


        chat_recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = datasnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                    chat_recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
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
                chat_recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
            }
        });

    }

    private void sendMessage(String message) {
        Date date = new Date();
        //This method returns the time in millis
        long time = date.getTime();
        String timestamp = String.valueOf(time);
        String messageID = timestamp + UUID.randomUUID().toString();
        MessageModel messageModel = new MessageModel(message,messageID,mAuth.getUid(),receiverId);
        messageAdapter.add(messageModel);
        databaseReferenceSender.child(messageID).setValue(messageModel);
        databaseReferenceReceiver.child(messageID).setValue(messageModel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.background_change:
                Intent profile_picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(profile_picIntent,10);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                profilepic = data.getData();
                getPic = true;

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(profilepic, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                if(bmp != null && !bmp.isRecycled())
                {
                    bmp = null;
                }

                bmp = BitmapFactory.decodeFile(filePath);
                Drawable d = new BitmapDrawable(getResources(), bmp);
                chat_layout.setBackground(d);
                SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit=shre.edit();
                edit.putString("image_data",filePath);
                edit.commit();
            }
        }
    }

    private void loadBackgroundFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String imagePath = sharedPreferences.getString("image_data", null);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Drawable d = new BitmapDrawable(getResources(), bmp);
            chat_layout.setBackground(d);
        }
    }


}