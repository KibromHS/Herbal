package com.example.herbal.healer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herbal.R;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNewActivity extends AppCompatActivity {
    private EditText edHerbDesc, edHerbName, edHerbSideEffect, edHerbDosage;
    private ImageView newHerbPic, backBtn;
    private TextView saveBtn;
    private Uri imageUri;
    private StorageReference storageReference;
    private RadioGroup radioGroup;
    private String herbType;

    ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        newHerbPic.setImageURI(imageUri);
                    }
                }
            });
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        initView();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getHealerData(healerModel -> {
            if (healerModel.isApproved()) {
                saveBtn.setEnabled(true);
            } else {
                saveBtn.setEnabled(false);
                Toast.makeText(AddNewActivity.this, "You're unable to post herbals. Contact admins", Toast.LENGTH_SHORT).show();
            }
        });

        newHerbPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageActivityResultLauncher.launch(intent);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbInternalUse:
                        herbType = "Internal Use";
                        break;
                    case R.id.rbExternalUse:
                        herbType = "External Use";
                        break;
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(AddNewActivity.this);
                progressDialog.setTitle("Uploading Herbal Info...");
                progressDialog.show();

                String herbalName = edHerbName.getText().toString();
                String healerId = FirebaseUtil.currentUserId();
                String herbalDescription = edHerbDesc.getText().toString();
                String herbalDosage = edHerbDosage.getText().toString();
                String herbalSideEffect = edHerbSideEffect.getText().toString();

                if (TextUtils.isEmpty(herbalName)) {
                    Toast.makeText(AddNewActivity.this, "Fill in the herbal name", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(herbalDescription)) {
                    Toast.makeText(AddNewActivity.this, "Fill in the herbal description", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(herbType)) {
                    Toast.makeText(AddNewActivity.this, "Fill in the herbal type", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(herbalDosage)) {
                    Toast.makeText(AddNewActivity.this, "Fill in the herbal dosage recommendation", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(herbalSideEffect)) {
                    Toast.makeText(AddNewActivity.this, "Fill in the herbal side-effect", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(AddNewActivity.this, "Select herbal image", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (herbalName.length() < 3 || !herbalName.matches("[a-zA-Z]+[0-9]*")) {
                    edHerbName.setError("Incorrect herbal name");
                    progressDialog.dismiss();
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                Date now = new Date();
                String fileName = formatter.format(now);

                storageReference = FirebaseStorage.getInstance().getReference("HerbalImages/" + fileName);
                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference("HerbalImages");
                        StorageReference ref = fileRef.child(fileName);

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> herbalData = new HashMap<>();
                                herbalData.put("herbalName", herbalName);
                                herbalData.put("healerId", healerId);
                                herbalData.put("herbalDescription", herbalDescription);
                                herbalData.put("herbalType", herbType);
                                herbalData.put("herbalImage", uri.toString());
                                herbalData.put("numOfViews", 0);
                                herbalData.put("dosage", herbalDosage);
                                herbalData.put("sideEffect", herbalSideEffect);

                                db.collection("herbals").add(herbalData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String herbalId = documentReference.getId();

                                        db.collection("herbals").document(herbalId).update("herbalId", herbalId);

                                        Toast.makeText(AddNewActivity.this, "Herbal added successfully!", Toast.LENGTH_SHORT).show();

                                        Intent i = new Intent(AddNewActivity.this, HealerMainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
    }

    private void getHealerData(HealerCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(FirebaseUtil.currentUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HealerModel healerModel = documentSnapshot.toObject(HealerModel.class);
                callback.onHealerReceivedCallback(healerModel);
            }
        });
    }

    private void initView() {
        edHerbName = findViewById(R.id.edHerbName);
        edHerbDesc = findViewById(R.id.edHerbDescription);
        newHerbPic = findViewById(R.id.addNewHerbPicture);
        backBtn = findViewById(R.id.addNewBackBtn);
        saveBtn = findViewById(R.id.addNewSaveBtn);
        radioGroup = findViewById(R.id.radioGroup);
        edHerbDosage = findViewById(R.id.edHerbDosage);
        edHerbSideEffect = findViewById(R.id.edHerbSideEffect);
    }
}