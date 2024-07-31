package com.example.herbal.healer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herbal.R;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealerRegisterDataActivity extends AppCompatActivity {
    ImageView profilePic;
    EditText etExperience, etAddress;
    RadioGroup genderGroup;
    Button finishBtn;
    ProgressBar progressBar;
    TextView toLoginBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String fullName, email, password;
    String gender;
    Uri imageUri;
    StorageReference storageReference;

    ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        profilePic.setImageURI(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_register_data);
        initView();

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.healerGenderMale:
                        gender = "M";
                        break;
                    case R.id.healerGenderFemale:
                        gender = "F";
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageActivityResultLauncher.launch(intent);
            }
        });

        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HealerRegisterDataActivity.this, HealerLoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String experienceS = etExperience.getText().toString();
                String address = etAddress.getText().toString();

                if (TextUtils.isEmpty(experienceS)) {
                    Toast.makeText(HealerRegisterDataActivity.this, "Fill in your age", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(HealerRegisterDataActivity.this, "Fill in your weight", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (gender == null) {
                    Toast.makeText(HealerRegisterDataActivity.this, "Select your gender", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(HealerRegisterDataActivity.this, "Select profile image", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                int experience = Integer.parseInt(experienceS);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(HealerRegisterDataActivity.this, task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                assert currentUser != null;
                                DocumentReference docRef = db.collection("healers").document(currentUser.getUid());



                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                                Date now = new Date();
                                String fileName = formatter.format(now);

                                storageReference = FirebaseStorage.getInstance().getReference("HealerImages/" + fileName);
                                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressBar.setVisibility(View.GONE);
                                        StorageReference fileRef = FirebaseStorage.getInstance().getReference("HealerImages");
                                        StorageReference ref = fileRef.child(fileName);

                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                HealerModel healerModel = new HealerModel(FirebaseUtil.currentUserId(), fullName, email, uri.toString(), experience, address, gender, false);

                                                docRef.set(healerModel);

                                                UserPreferences.setHealer(healerModel);

                                                Toast.makeText(HealerRegisterDataActivity.this, "Healer Registered Successfully", Toast.LENGTH_SHORT).show();

                                                Intent i = new Intent(HealerRegisterDataActivity.this, HealerMainActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(HealerRegisterDataActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(HealerRegisterDataActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(HealerRegisterDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void initView() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profilePic = findViewById(R.id.selectPictureHealer);
        etExperience = findViewById(R.id.etExperience);
        etAddress = findViewById(R.id.etAddress);
        genderGroup = findViewById(R.id.genderGroup);
        finishBtn = findViewById(R.id.healerFinishBtn);
        progressBar = findViewById(R.id.progressBar);
        toLoginBtn = findViewById(R.id.toHealerLoginBtn);
        fullName = getIntent().getStringExtra("fullName");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
    }
}