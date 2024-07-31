package com.example.herbal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.herbal.healer.HealerRegisterActivity;

public class GuestActivity extends AppCompatActivity {
    private LinearLayout userAccount, healerAccount;
    private Button nextBtn;
    private String checkedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        initView();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedAccount == null) {
                    Toast.makeText(GuestActivity.this, "Please select the account type", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i;

                if (checkedAccount.equals("Healer")) {
                    i = new Intent(GuestActivity.this, HealerRegisterActivity.class);
                } else {
                    i = new Intent(GuestActivity.this, RegisterActivity.class);
                }
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.userAccount:
                checkedAccount = "User";
                userAccount.setBackground(ContextCompat.getDrawable(GuestActivity.this, R.drawable.bg_profile));
                healerAccount.setBackground(null);
                break;
            case R.id.healerAccount:
                checkedAccount = "Healer";
                healerAccount.setBackground(ContextCompat.getDrawable(GuestActivity.this, R.drawable.bg_profile));
                userAccount.setBackground(null);
                break;
        }
    }

    private void initView() {
        userAccount = findViewById(R.id.userAccount);
        healerAccount = findViewById(R.id.healerAccount);
        nextBtn = findViewById(R.id.nextBtn);
    }
}