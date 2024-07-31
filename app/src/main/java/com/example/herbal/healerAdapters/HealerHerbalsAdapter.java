package com.example.herbal.healerAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.herbal.HealerNameCallback;
import com.example.herbal.HerbalActivity;
import com.example.herbal.R;
import com.example.herbal.healer.UpdateHerbalActivity;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HealerHerbalsAdapter extends RecyclerView.Adapter<HealerHerbalsAdapter.ViewHolder> {
    ArrayList<Herbal> herbals;

    public HealerHerbalsAdapter(ArrayList<Herbal> herbals) {
        this.herbals = herbals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_healer_herbal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Herbal herbal = herbals.get(position);
        getHealerName(holder.itemView.getContext(), herbal.getHealerId(), new HealerNameCallback() {
            @Override
            public void onHealerNameReceived(String healerName) {
                String strBuild = herbal.getHerbalName() + " by " + healerName;
                holder.herbalNameAndHealer.setText(strBuild);

                Glide.with(holder.itemView.getContext())
                        .load(herbal.getHerbalImage())
                        .into(holder.herbalImage);
            }
        });

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(), UpdateHerbalActivity.class);
                i.putExtra("herbal", herbal);
                holder.itemView.getContext().startActivity(i);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("herbals").document(herbal.getHerbalId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            herbals.remove(herbal);
                            notifyItemRemoved(holder.getBindingAdapterPosition());
                            Toast.makeText(v.getContext(), "Herbal removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void getHealerName(Context context, String healerId, HealerNameCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(healerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String healerName = documentSnapshot.getString("healerName");
                callback.onHealerNameReceived(healerName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Couldn't get healer data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return herbals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView herbalNameAndHealer;
        ImageView herbalImage;
        Button updateBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            herbalNameAndHealer = itemView.findViewById(R.id.herbalNameAndHealer);
            herbalImage = itemView.findViewById(R.id.herbalImage);
            updateBtn = itemView.findViewById(R.id.updateHerbBtn);
            deleteBtn = itemView.findViewById(R.id.deleteHerbBtn);
        }
    }
}
