package com.example.herbal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.herbal.ChatActivity;
import com.example.herbal.R;
import com.example.herbal.models.ChatroomModel;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.MessageModel;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class MessagesAdapter extends FirestoreRecyclerAdapter<ChatroomModel, MessagesAdapter.ViewHolder> {

    Context context;

    public MessagesAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getHealerFromChatroom(model.getHealerId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                    HealerModel healerModel = task.getResult().toObject(HealerModel.class);
                    if (healerModel == null) {
                        holder.healerName.setText("Deleted Account");
                        if (lastMessageSentByMe) {
                            String builder = "You : " + model.getLastMessage();
                            holder.message.setText(builder);
                        } else {
                            holder.message.setText(model.getLastMessage());
                        }
                        holder.date.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "This healer is removed by admins", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        holder.healerName.setText(healerModel.getHealerName());
                        if (lastMessageSentByMe) {
                            String builder = "You : " + model.getLastMessage();
                            holder.message.setText(builder);
                        } else {
                            holder.message.setText(model.getLastMessage());
                        }
                        holder.date.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        if (healerModel.isApproved()) {
                            holder.approvedTick.setVisibility(View.VISIBLE);
                        } else {
                            holder.approvedTick.setVisibility(View.GONE);
                        }


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(context, ChatActivity.class);
                                i.putExtra("healer", healerModel);
                                context.startActivity(i);
                            }
                        });
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_message, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView healerProfilePic, approvedTick;
        TextView healerName, date, numOfUnreadMessages, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            healerProfilePic = itemView.findViewById(R.id.healerProfileImage);
            healerName = itemView.findViewById(R.id.healerName);
            message = itemView.findViewById(R.id.healerMessage);
            date = itemView.findViewById(R.id.messageDate);
            approvedTick = itemView.findViewById(R.id.approvedTick);
        }
    }
}
