package com.example.herbal.healer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herbal.R;

public class HealerRegisterActivity extends AppCompatActivity {
    private EditText edFullName, edEmail, edPassword, edConfirm;
    private Button signupBtn;
    private TextView toLoginBtn;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_register);
        initView();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String fullName = edFullName.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                String confirmPassword = edConfirm.getText().toString();

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(HealerRegisterActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(HealerRegisterActivity.this, "Fill in your name", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(HealerRegisterActivity.this, "Fill in the Email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(HealerRegisterActivity.this, "Fill in the Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                Intent i = new Intent(HealerRegisterActivity.this, HealerRegisterDataActivity.class);
                i.putExtra("fullName", fullName);
                i.putExtra("email", email);
                i.putExtra("password", password);
                startActivity(i);
                progressBar.setVisibility(View.GONE);
            }
        });

        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HealerRegisterActivity.this, HealerLoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void initView() {
        edFullName = findViewById(R.id.etFullName);
        edEmail = findViewById(R.id.etEmail);
        edPassword = findViewById(R.id.etPassword);
        edConfirm = findViewById(R.id.etConfirmPassword);
        signupBtn = findViewById(R.id.finishBtn);
        toLoginBtn = findViewById(R.id.toLoginBtn);
        progressBar = findViewById(R.id.progressBar);
    }
}