package com.example.herbal.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.herbal.healer.HealerCallback;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HerbsAdapter extends RecyclerView.Adapter<HerbsAdapter.ViewHolder> {
    ArrayList<Herbal> herbals;

    public HerbsAdapter(ArrayList<Herbal> herbals) {
        this.herbals = herbals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_herbs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Herbal herbal = herbals.get(position);

        getHealerData(holder.itemView.getContext(), herbal.getHealerId(), new HealerCallback() {
            @Override
            public void onHealerReceivedCallback(HealerModel healerModel) {
                String strBuild = herbal.getHerbalName() + "\nby " + healerModel.getHealerName();
                holder.herbalNameAndHealer.setText(strBuild);
                Glide.with(holder.itemView.getContext())
                        .load(herbal.getHerbalImage())
                        .into(holder.herbalImage);
                if (healerModel.isApproved()) {
                    holder.approvedTick.setVisibility(View.VISIBLE);
                } else {
                    holder.approvedTick.setVisibility(View.GONE);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemView.getContext(), HerbalActivity.class);
                i.putExtra("herbal", herbal);
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    private void getHealerData(Context context, String healerId, HealerCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(healerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HealerModel healerModel = documentSnapshot.toObject(HealerModel.class);
                callback.onHealerReceivedCallback(healerModel);
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
        ImageView herbalImage, approvedTick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            herbalNameAndHealer = itemView.findViewById(R.id.herbalNameAndHealer);
            herbalImage = itemView.findViewById(R.id.herbalImage);
            approvedTick = itemView.findViewById(R.id.approvedTick);
        }
    }
}
