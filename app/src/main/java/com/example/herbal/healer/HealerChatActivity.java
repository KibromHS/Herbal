package com.example.herbal.healer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herbal.ChatActivity;
import com.example.herbal.HealerProfileActivity;
import com.example.herbal.R;
import com.example.herbal.adapters.ChatAdapter;
import com.example.herbal.models.ChatroomModel;
import com.example.herbal.models.MessageModel;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class HealerChatActivity extends AppCompatActivity {
    private ImageView backBtn, chatProfilePic;
    private TextView chatHealerName;
    private EditText edMessage;
    private ImageButton sendMessageBtn;
    private RecyclerView rcChat;
    private String chatroomId;
    private ChatroomModel chatroomModel;
    private ChatAdapter adapter;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_chat);

        initView();

        setupChat();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edMessage.getText().toString();
                if (message.isEmpty()) return;
                sendMessage(message);
            }
        });

        chatHealerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HealerChatActivity.this, UserProfileActivity.class);
                i.putExtra("user", userModel);
                startActivity(i);
            }
        });

        chatProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HealerChatActivity.this, UserProfileActivity.class);
                i.putExtra("user", userModel);
                startActivity(i);
            }
        });
    }

    private void setupChat() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>().setQuery(query, MessageModel.class).build();
        adapter = new ChatAdapter(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rcChat.setLayoutManager(manager);
        rcChat.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rcChat.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessage(String message) {
        Timestamp timestamp = Timestamp.now();
        chatroomModel.setLastMessageTimestamp(timestamp);
        chatroomModel.setLastMessage(message);
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        MessageModel messageModel = new MessageModel(FirebaseUtil.currentUserId(), timestamp, message);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    edMessage.setText("");
                }
            }
        });
    }

    private void initView() {
        backBtn = findViewById(R.id.back_btn);
        userModel = (UserModel) getIntent().getSerializableExtra("user");
        String chatProfile = userModel.getImgUrl();
        String chatHealer = userModel.getUsername();
        String healerId = userModel.getUserId();

        chatHealerName = findViewById(R.id.chatHealerName);
        chatProfilePic = findViewById(R.id.chatProfilePic);
        edMessage = findViewById(R.id.edMessageText);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        rcChat = findViewById(R.id.recyclerViewChat);

        chatHealerName.setText(chatHealer);
        Glide.with(this).load(chatProfile).into(chatProfilePic);
        chatroomId = FirebaseUtil.getChatroomId(healerId, FirebaseUtil.currentUserId());

        getChatroomModel(healerId);
    }

    void getChatroomModel(String healerId) {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    if (chatroomModel == null) {
                        chatroomModel = new ChatroomModel(chatroomId, FirebaseUtil.currentUserId(), healerId, Timestamp.now(), "");
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupChat();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}