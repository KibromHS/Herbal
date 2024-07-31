package com.example.herbal;

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

public class EditProfileActivity extends AppCompatActivity {
    private ImageView doneBtn, backBtn, editProfilePic;
    private EditText editUserName, editUserAge, editUserWeight;
    private TextView editUserEmail;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private Uri imageUri;
    private UserModel userModel;

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
        setContentView(R.layout.activity_edit_profile);
        initView();

        editUserName.setText(userModel.getUsername());
        Glide.with(this).load(userModel.getImgUrl()).into(editProfilePic);
        editUserWeight.setText(String.valueOf(userModel.getWeight()));
        editUserAge.setText(String.valueOf(userModel.getAge()));
        editUserEmail.setText(userModel.getEmail());

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
                progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setTitle("Updating Profile...");
                progressDialog.show();

                String username = editUserName.getText().toString();
                String ageS = editUserAge.getText().toString();
                String weightS = editUserWeight.getText().toString();
                int age;
                double weight;

                try {
                    age = Integer.parseInt(ageS);
                    weight = Double.parseDouble(weightS);
                } catch (NumberFormatException e) {
                    Toast.makeText(EditProfileActivity.this, "Use numeric value for age and weight", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                Date now = new Date();
                String fileName = formatter.format(now);

                if (imageUri == null) {
                    uploadUpdatedData(username, weight, age);
                } else {
                    uploadUpdatedDataWithImage(fileName, username, weight, age);
                }
            }
        });
    }

    private void uploadUpdatedData(String username, double weight, int age) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("age", age); // Update fieldName1 with value1
        updates.put("username", username);
        updates.put("weight", weight);

        FirebaseFirestore.getInstance().collection("users").document(FirebaseUtil.currentUserId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UserModel u = UserPreferences.getUser();
                        assert u != null;
                        u.setAge(age);
                        u.setUsername(username);
                        u.setWeight(weight);
                        UserPreferences.setUser(u);
                        Toast.makeText(EditProfileActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadUpdatedDataWithImage(String fileName, String username, double weight, int age) {
        storageReference = FirebaseStorage.getInstance().getReference("Images/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images");
                StorageReference ref = fileRef.child(fileName);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (username.isEmpty()) {
                            Toast.makeText(EditProfileActivity.this, "Please Fill in the empty fields", Toast.LENGTH_SHORT).show();
                        } else {
                            UserModel user = new UserModel(FirebaseUtil.currentUserId(), username, userModel.getEmail(), uri.toString(), weight, age, "M");
                            FirebaseFirestore.getInstance().collection("users").document(FirebaseUtil.currentUserId()).set(user);
                            UserPreferences.setUser(user);
                            Toast.makeText(EditProfileActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        doneBtn = findViewById(R.id.doneEditProfileBtn);
        backBtn = findViewById(R.id.backEditProfileBtn);
        editProfilePic = findViewById(R.id.editUserProfileImage);
        editUserName = findViewById(R.id.editUsername);
        editUserEmail = findViewById(R.id.editUserEmail);
        editUserAge = findViewById(R.id.editUserAge);
        editUserWeight = findViewById(R.id.editUserWeight);
        userModel = (UserModel) getIntent().getSerializableExtra("user");
    }
}