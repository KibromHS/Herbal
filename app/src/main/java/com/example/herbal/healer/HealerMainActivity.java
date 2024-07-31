package com.example.herbal.healer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.herbal.GuestActivity;
import com.example.herbal.LoginActivity;
import com.example.herbal.R;
import com.example.herbal.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

public class HealerMainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_main);
        initView();
        if (FirebaseUtil.currentUserId() == null) {
            Intent i = new Intent(this, GuestActivity.class);
            startActivity(i);
        }
        replaceFragment(new HealerHomeFragment());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(new HealerHomeFragment());
                        break;
                    case R.id.message:
                        replaceFragment(new HealerMessageFragment());
                        break;
                    case R.id.profile:
                        replaceFragment(new HealerProfileFragment());
                        break;
                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.navView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseUtil.currentUserId() == null) {
            Intent i = new Intent(this, GuestActivity.class);
            startActivity(i);
        }
    }
}