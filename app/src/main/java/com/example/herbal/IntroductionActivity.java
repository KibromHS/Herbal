package com.example.herbal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class IntroductionActivity extends AppCompatActivity {
    private TextView toSignUp, toLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        initView();

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntroductionActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntroductionActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void initView() {
        toSignUp = findViewById(R.id.introSignupBtn);
        toLogin = findViewById(R.id.introLoginBtn);
    }
}