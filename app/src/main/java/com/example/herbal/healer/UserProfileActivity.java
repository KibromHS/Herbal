package com.example.herbal.healer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herbal.R;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.UserModel;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView healerImage, backBtn;
    private TextView healerName, healerEmail, healerExperience, healerAddress;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initView();
        healerName.setText(userModel.getUsername());
        healerEmail.setText(userModel.getEmail());
        healerExperience.setText(String.valueOf(userModel.getAge()));
        String builder = userModel.getWeight() + " Kg";
        healerAddress.setText(builder);
        Glide.with(this).load(userModel.getImgUrl()).into(healerImage);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        userModel = (UserModel) getIntent().getSerializableExtra("user");
        healerImage = findViewById(R.id.healerProfileImage);
        healerName = findViewById(R.id.healerName);
        healerEmail = findViewById(R.id.healerEmail);
        healerExperience = findViewById(R.id.healerProfileExperience);
        healerAddress = findViewById(R.id.healerAddress);
        backBtn = findViewById(R.id.userProfileBackBtn);
    }
}