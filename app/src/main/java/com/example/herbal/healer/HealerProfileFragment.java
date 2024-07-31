package com.example.herbal.healer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herbal.GuestActivity;
import com.example.herbal.R;
import com.example.herbal.models.HealerModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;

public class HealerProfileFragment extends Fragment {
    private ImageView logoutBtn, editProfileBtn, userProfileImage, approvedTick;
    private TextView userName, userEmail, healerExperience, healerAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healer_profile, container, false);
        initVew(view);

        // TODO: set data from database
        // "https://w0.peakpx.com/wallpaper/979/89/HD-wallpaper-purple-smile-design-eye-smily-profile-pic-face-thumbnail.jpg"
        HealerModel healerModel = UserPreferences.getHealer();
        assert healerModel != null;
        Glide.with(view.getContext()).load(healerModel.getHealerPic()).into(userProfileImage);
        userName.setText(healerModel.getHealerName());
        userEmail.setText(healerModel.getHealerEmail());
        String experienceString = healerModel.getExperience() + " Years";
        healerExperience.setText(experienceString);
        healerAddress.setText(healerModel.getHealerAddress());

        if (healerModel.isApproved()) {
            approvedTick.setVisibility(View.VISIBLE);
        } else {
            approvedTick.setVisibility(View.GONE);
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.signOut();
                Intent i = new Intent(view.getContext(), GuestActivity.class);
                startActivity(i);
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), HealerEditProfileActivity.class);
                i.putExtra("user", healerModel);
                startActivity(i);
            }
        });
        return view;
    }

    private void initVew(View view) {
        logoutBtn = view.findViewById(R.id.logoutBtn);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        userProfileImage = view.findViewById(R.id.editHealerProfileImage);
        userName = view.findViewById(R.id.editHealerName);
        userEmail = view.findViewById(R.id.editHealerEmail);
        healerExperience = view.findViewById(R.id.editHealerExperience);
        healerAddress = view.findViewById(R.id.editHealerAddress);
        approvedTick = view.findViewById(R.id.approvedTick);
    }
}