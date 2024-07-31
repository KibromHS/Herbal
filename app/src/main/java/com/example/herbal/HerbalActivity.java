package com.example.herbal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class HerbalActivity extends AppCompatActivity {
    private Herbal herbal;
    private ImageView herbalPic, backBtn, approvedTick;
    private TextView herbalName, tvHealerName, herbalDescription, sendMessageBtn, txtNumOfViews, dosage, sideEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herbal);
        initView();

        updateNumOfViews();

        Glide.with(this).load(herbal.getHerbalImage()).into(herbalPic);
        herbalName.setText(herbal.getHerbalName());
        herbalDescription.setText(herbal.getHerbalDescription());
        String builder = "Viewed by " + herbal.getNumOfViews() + " people";
        txtNumOfViews.setText(builder);

        dosage.setText(herbal.getDosage());
        sideEffect.setText(herbal.getSideEffect());

        getHealerName(herbal.getHealerId(), new HealerNameCallback() {
            @Override
            public void onHealerNameReceived(String healerName) {
                tvHealerName.setText(healerName);
            }
        });

        getHealerApproved(herbal.getHealerId(), new HealerApprovedCallback() {
            @Override
            public void onHealerApprovedReceived(boolean approved) {
                if (approved) {
                    approvedTick.setVisibility(View.VISIBLE);
                } else {
                    approvedTick.setVisibility(View.GONE);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.getHealerFromChatroom(herbal.getHealerId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        HealerModel healerModel = documentSnapshot.toObject(HealerModel.class);

                        Intent i = new Intent(HerbalActivity.this, ChatActivity.class);
                        i.putExtra("healer", healerModel);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HerbalActivity.this, "Couldn't get healer data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateNumOfViews() {
        FirebaseFirestore.getInstance().collection("herbals").document(herbal.getHerbalId()).update("numOfViews", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                String builder = "Viewed by " + herbal.getNumOfViews() + " people";
                txtNumOfViews.setText(builder);
            }
        });
    }

    private void getHealerName(String healerId, HealerNameCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(healerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String healerName = documentSnapshot.getString("healerName");
                callback.onHealerNameReceived(healerName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HerbalActivity.this, "Couldn't get healer data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHealerApproved(String healerId, HealerApprovedCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(healerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean approved = Boolean.TRUE.equals(documentSnapshot.getBoolean("isApproved"));
                callback.onHealerApprovedReceived(approved);
            }
        });
    }

    private void initView() {
        herbal = (Herbal) getIntent().getSerializableExtra("herbal");
        herbalPic = findViewById(R.id.herbalHerbalPic);
        herbalName = findViewById(R.id.herbalHerbalName);
        tvHealerName = findViewById(R.id.herbalHealerName);
        herbalDescription = findViewById(R.id.herbalDescription);
        sendMessageBtn = findViewById(R.id.herbalSendMessageBtn);
        backBtn = findViewById(R.id.herbalBackBtn);
        approvedTick = findViewById(R.id.approvedTick);
        txtNumOfViews = findViewById(R.id.txtNumOfViews);
        dosage = findViewById(R.id.herbalDosage);
        sideEffect = findViewById(R.id.herbalSideEffect);
    }
}