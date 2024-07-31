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
import com.example.herbal.HealerNameCallback;
import com.example.herbal.HerbalActivity;
import com.example.herbal.R;
import com.example.herbal.healer.HealerCallback;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchHerbalAdapter extends FirestoreRecyclerAdapter<Herbal, SearchHerbalAdapter.ViewHolder> {

    Context context;

    public SearchHerbalAdapter(@NonNull FirestoreRecyclerOptions<Herbal> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Herbal model) {
        getHealerData(model.getHealerId(), new HealerCallback() {
            @Override
            public void onHealerReceivedCallback(HealerModel healerModel) {
                String builder = "By " + healerModel.getHealerName();
                holder.healerName.setText(builder);
                if (healerModel.isApproved()) {
                    holder.approvedTick.setVisibility(View.VISIBLE);
                } else {
                    holder.approvedTick.setVisibility(View.GONE);
                }
            }
        });
        holder.herbalName.setText(model.getHerbalName());
        Glide.with(holder.itemView.getContext()).load(model.getHerbalImage()).into(holder.herbalPic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, HerbalActivity.class);
                i.putExtra("herbal", model);
                context.startActivity(i);
            }
        });
    }

    private void getHealerData(String healerId, HealerCallback callback) {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_herbal_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView herbalPic, approvedTick;
        TextView herbalName, healerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            herbalPic = itemView.findViewById(R.id.herbalPicSearch);
            herbalName = itemView.findViewById(R.id.herbalNameSearch);
            healerName = itemView.findViewById(R.id.healerNameSearch);
            approvedTick = itemView.findViewById(R.id.approvedTick);
        }
    }
}
