package com.example.herbal.healer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.herbal.R;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HealerEditProfileActivity extends AppCompatActivity {
    private ImageView doneBtn, backBtn, editProfilePic;
    private EditText editUserName, editUserAge, editUserWeight;
    private TextView editUserEmail;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private Uri imageUri;
    private HealerModel healerModel;

    ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        editProfilePic.setImageURI(imageUri);
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healer_edit_profile);
        initView();

        editUserName.setText(healerModel.getHealerName());
        Glide.with(this).load(healerModel.getHealerPic()).into(editProfilePic);
        editUserWeight.setText(healerModel.getHealerAddress());
        editUserAge.setText(String.valueOf(healerModel.getExperience()));
        editUserEmail.setText(healerModel.getHealerEmail());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageActivityResultLauncher.launch(intent);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(HealerEditProfileActivity.this);
                progressDialog.setTitle("Updating Profile...");
                progressDialog.show();

                String username = editUserName.getText().toString();
                String experienceS = editUserAge.getText().toString();
                String address = editUserWeight.getText().toString();
                int experience;

                try {
                    experience = Integer.parseInt(experienceS);
                } catch (NumberFormatException e) {
                    Toast.makeText(HealerEditProfileActivity.this, "Use numeric value for age and weight", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                Date now = new Date();
                String fileName = formatter.format(now);

                if (imageUri == null) {
                    uploadUpdatedData(username, address, experience);
                } else {
                    uploadUpdatedDataWithImage(fileName, username, address, experience);
                }
            }
        });
    }

    private void uploadUpdatedData(String username, String address, int experience) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("experience", experience);
        updates.put("healerName", username);
        updates.put("healerAddress", address);

        FirebaseFirestore.getInstance().collection("healers").document(FirebaseUtil.currentUserId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HealerModel u = UserPreferences.getHealer();
                        assert u != null;
                        u.setExperience(experience);
                        u.setHealerName(username);
                        u.setHealerAddress(address);
                        UserPreferences.setHealer(u);
                        Toast.makeText(HealerEditProfileActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(HealerEditProfileActivity.this, HealerMainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HealerEditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadUpdatedDataWithImage(String fileName, String username, String address, int experience) {
        storageReference = FirebaseStorage.getInstance().getReference("HealerImages/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StorageReference fileRef = FirebaseStorage.getInstance().getReference("HealerImages");
                StorageReference ref = fileRef.child(fileName);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (username.isEmpty()) {
                            Toast.makeText(HealerEditProfileActivity.this, "Please Fill in the empty fields", Toast.LENGTH_SHORT).show();
                        } else {
                            HealerModel healer = new HealerModel(FirebaseUtil.currentUserId(), username, healerModel.getHealerEmail(), uri.toString(), experience, address, "M", healerModel.isApproved());
                            FirebaseFirestore.getInstance().collection("healers").document(FirebaseUtil.currentUserId()).set(healer);
                            UserPreferences.setHealer(healer);
                            Toast.makeText(HealerEditProfileActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(HealerEditProfileActivity.this, HealerMainActivity.class);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HealerEditProfileActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HealerEditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        doneBtn = findViewById(R.id.doneEditProfileBtn);
        backBtn = findViewById(R.id.backEditProfileBtn);
        editProfilePic = findViewById(R.id.editHealerProfileImage);
        editUserName = findViewById(R.id.editHealerName);
        editUserEmail = findViewById(R.id.editHealerEmail);
        editUserAge = findViewById(R.id.editHealerExperience);
        editUserWeight = findViewById(R.id.editHealerAddress);
        healerModel = (HealerModel) getIntent().getSerializableExtra("user");
    }
}