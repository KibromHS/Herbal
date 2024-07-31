package com.example.herbal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herbal.models.HealerModel;

public class HealerProfileActivity extends AppCompatActivity {
    private ImageView healerImage, backBtn, approvedTick;
    private TextView healerName, healerEmail, healerExperience, healerAddress;
    private HealerModel healerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_profile);
        initView();
        if (healerModel.isApproved()) {
            approvedTick.setVisibility(View.VISIBLE);
        } else {
            approvedTick.setVisibility(View.GONE);
        }
        healerName.setText(healerModel.getHealerName());
        healerEmail.setText(healerModel.getHealerEmail());
        String builder = healerModel.getExperience() + " Years";
        healerExperience.setText(builder);
        healerAddress.setText(healerModel.getHealerAddress());
        Glide.with(this).load(healerModel.getHealerPic()).into(healerImage);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        healerModel = (HealerModel) getIntent().getSerializableExtra("healer");
        healerImage = findViewById(R.id.healerProfileImage);
        healerName = findViewById(R.id.healerName);
        healerEmail = findViewById(R.id.healerEmail);
        healerExperience = findViewById(R.id.healerProfileExperience);
        healerAddress = findViewById(R.id.healerAddress);
        backBtn = findViewById(R.id.healerProfileBackBtn);
        approvedTick = findViewById(R.id.approvedTick);
    }
}