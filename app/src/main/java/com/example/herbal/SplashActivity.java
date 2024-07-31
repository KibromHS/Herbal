package com.example.herbal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.herbal.healer.HealerChatActivity;
import com.example.herbal.healer.HealerMainActivity;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        UserPreferences.init(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getIntent().getExtras() != null) {
            String userId = getIntent().getExtras().getString("healerId");
            FirebaseUtil.allHealerCollectionRef().document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        HealerModel healerModel = documentSnapshot.toObject(HealerModel.class);
                        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                        intent.putExtra("healer", healerModel);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        FirebaseUtil.allUserCollectionRef().document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                Intent intent = new Intent(SplashActivity.this, HealerChatActivity.class);
                                intent.putExtra("user", userModel);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.currentUserId() == null) {
                        Intent i = new Intent(SplashActivity.this, GuestActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        checkUserType();
                    }
                }
            }, 3000);
        }
    }

    private void checkUserType() {

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Check if the user exists in the 'users' collection
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User exists in the 'users' collection, so it's a user
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // User does not exist in the 'users' collection, check 'healers' collection
                        checkHealerType(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while retrieving user data
                    Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkHealerType(String userId) {
        // Check if the user exists in the 'healers' collection
        db.collection("healers").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User exists in the 'healers' collection, so it's a healer
                        startActivity(new Intent(SplashActivity.this, HealerMainActivity.class));
                        finish();
                    } else {
                        // User does not exist in the 'healers' collection
                        mAuth.signOut(); // Sign out the user as something unexpected occurred
                        startActivity(new Intent(SplashActivity.this, GuestActivity.class)); // Redirect to login screen
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while retrieving user data
                    Toast.makeText(SplashActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}