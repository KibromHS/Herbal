package com.example.herbal.healer;

import androidx.annotation.NonNull;
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
import com.example.herbal.models.HealerModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class HealerLoginActivity extends AppCompatActivity {
    private EditText edEmail, edPassword;
    private Button loginBtn;
    private TextView toSignUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_login);
        initView();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(HealerLoginActivity.this, "Fill in the Email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(HealerLoginActivity.this, "Fill in the Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(HealerLoginActivity.this, task -> {

                            if (task.isSuccessful()) {
                                FirebaseUtil.getHealerFromChatroom(FirebaseUtil.currentUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        String username = documentSnapshot.getString("healerName");
                                        String email = documentSnapshot.getString("healerEmail");
                                        String healerPic = documentSnapshot.getString("healerPic");
                                        int experience = Math.toIntExact(documentSnapshot.getLong("experience"));
                                        String gender = documentSnapshot.getString("gender");
                                        String address = documentSnapshot.getString("healerAddress");
                                        boolean approved = Boolean.TRUE.equals(documentSnapshot.getBoolean("isApproved"));

                                        HealerModel healerModel = new HealerModel(FirebaseUtil.currentUserId(), username, email, healerPic, experience, address, gender, approved);

                                        UserPreferences.setHealer(healerModel);

                                        progressBar.setVisibility(View.GONE);

                                        Intent i = new Intent(HealerLoginActivity.this, HealerMainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HealerLoginActivity.this, "Couldn't get user data", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(HealerLoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(HealerLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HealerLoginActivity.this, HealerRegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.etEmail);
        edPassword = findViewById(R.id.etPassword);
        loginBtn = findViewById(R.id.loginBtn);
        toSignUpBtn = findViewById(R.id.toLoginBtn);
        progressBar = findViewById(R.id.progressBarLogin);
    }
}