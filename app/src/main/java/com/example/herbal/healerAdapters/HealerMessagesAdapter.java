package com.example.herbal.healerAdapters;

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
import com.example.herbal.ChatActivity;
import com.example.herbal.R;
import com.example.herbal.healer.HealerChatActivity;
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

public class HealerMessagesAdapter extends FirestoreRecyclerAdapter<ChatroomModel, HealerMessagesAdapter.ViewHolder> {

    Context context;

    public HealerMessagesAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getUserFromChatroom(model.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                    UserModel healerModel = task.getResult().toObject(UserModel.class);
                    assert healerModel != null;
                    holder.healerName.setText(healerModel.getUsername());
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
                            Intent i = new Intent(context, HealerChatActivity.class);
                            i.putExtra("user", healerModel);
                            context.startActivity(i);
                        }
                    });
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
        ImageView healerProfilePic;
        TextView healerName, date, numOfUnreadMessages, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            healerProfilePic = itemView.findViewById(R.id.healerProfileImage);
            healerName = itemView.findViewById(R.id.healerName);
            message = itemView.findViewById(R.id.healerMessage);
            date = itemView.findViewById(R.id.messageDate);
        }
    }
}











//    ArrayList<MessageModel> messages;
//
//    public MessagesAdapter(ArrayList<MessageModel> messages) {
//        this.messages = messages;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_message, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        MessageModel message = messages.get(position);
////        holder.healerName.setText(message.getHealerName());
//        holder.healerName.setText("Healer Bekele");
//        holder.numOfUnreadMessages.setText("2");
//        holder.message.setText(message.getMessage());
////        holder.date.setText(message.getDate());
//        holder.date.setText("Jan 10");
//        Glide.with(holder.itemView.getContext()).load("https://w0.peakpx.com/wallpaper/979/89/HD-wallpaper-purple-smile-design-eye-smily-profile-pic-face-thumbnail.jpg").into(holder.healerProfilePic);
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(holder.itemView.getContext(), ChatActivity.class);
////                i.putExtra("healer_name", message.getHealerName());
////                i.putExtra("healer_pic", message.getHealerProfilePic());
////                i.putExtra("healer_id", message.getHealerId());
//                i.putExtra("healer_name", "Healer Bekele");
//                i.putExtra("healer_pic", "https://w0.peakpx.com/wallpaper/979/89/HD-wallpaper-purple-smile-design-eye-smily-profile-pic-face-thumbnail.jpg");
//                i.putExtra("healer_id", "01");
//                holder.itemView.getContext().startActivity(i);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size();
//    }
