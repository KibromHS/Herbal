package com.example.herbal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;

public class ProfileFragment extends Fragment {
    private ImageView logoutBtn, editProfileBtn, userProfileImage;
    private TextView userName, userEmail, userAge, userWeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initVew(view);

        UserModel userModel = UserPreferences.getUser();
        assert userModel != null;
        Glide.with(view.getContext()).load(userModel.getImgUrl()).into(userProfileImage);
        userName.setText(userModel.getUsername());
        userEmail.setText(userModel.getEmail());
        userAge.setText(String.valueOf(userModel.getAge()));
        String weightStr = userModel.getWeight() + " Kg";
        userWeight.setText(weightStr);

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
                Intent i = new Intent(view.getContext(), EditProfileActivity.class);
                i.putExtra("user", userModel);
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
        userAge = view.findViewById(R.id.userAge);
        userWeight = view.findViewById(R.id.userWeight);
    }
}