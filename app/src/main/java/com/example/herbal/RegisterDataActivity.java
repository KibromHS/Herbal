package com.example.herbal;

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

public class RegisterDataActivity extends AppCompatActivity {
    ImageView profilePic;
    EditText etAge, etWeight;
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
        setContentView(R.layout.activity_register_data);
        initView();

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.genderMale:
                        gender = "M";
                        break;
                    case R.id.genderFemale:
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
                Intent i = new Intent(RegisterDataActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String ageS = etAge.getText().toString();
                String weightS = etWeight.getText().toString();

                if (TextUtils.isEmpty(ageS)) {
                    Toast.makeText(RegisterDataActivity.this, "Fill in your age", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(weightS)) {
                    Toast.makeText(RegisterDataActivity.this, "Fill in your weight", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (gender == null) {
                    Toast.makeText(RegisterDataActivity.this, "Select your gender", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(RegisterDataActivity.this, "Select profile image", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(ageS);
                double weight = Double.parseDouble(weightS);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterDataActivity.this, task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                assert currentUser != null;
                                DocumentReference docRef = db.collection("users").document(currentUser.getUid());



                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                                Date now = new Date();
                                String fileName = formatter.format(now);

                                storageReference = FirebaseStorage.getInstance().getReference("Images/" + fileName);
                                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressBar.setVisibility(View.GONE);
                                        StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images");
                                        StorageReference ref = fileRef.child(fileName);

                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                UserModel userModel = new UserModel(FirebaseUtil.currentUserId(), fullName, email, uri.toString(), weight, age, gender);

                                                docRef.set(userModel);

                                                UserPreferences.setUser(userModel);

                                                Toast.makeText(RegisterDataActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                                Intent i = new Intent(RegisterDataActivity.this, MainActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterDataActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterDataActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void initView() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profilePic = findViewById(R.id.selectPicture);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etEmail);
        genderGroup = findViewById(R.id.genderGroup);
        finishBtn = findViewById(R.id.finishBtn);
        progressBar = findViewById(R.id.progressBar);
        toLoginBtn = findViewById(R.id.toLoginBtn);
        fullName = getIntent().getStringExtra("fullName");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
    }
}