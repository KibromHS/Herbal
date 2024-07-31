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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.herbal.EditProfileActivity;
import com.example.herbal.MainActivity;
import com.example.herbal.R;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.models.UserModel;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateHerbalActivity extends AppCompatActivity {
    private EditText edHerbDesc, edHerbName, edHerbDosage, edHerbSideEffect;
    private ImageView newHerbPic, backBtn;
    private TextView saveBtn;
    private Uri imageUri;
    private RadioGroup radioGroup;
    private int numOfViews;

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
    private StorageReference storageReference;
    private Herbal herbal;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_herbal);
        initView();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        newHerbPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageActivityResultLauncher.launch(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(UpdateHerbalActivity.this);
                progressDialog.setTitle("Updating Herbal Information...");
                progressDialog.show();

                String herbType = null;
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.rbInternalUse:
                        herbType = "Internal Use";
                        break;
                    case R.id.rbExternalUse:
                        herbType = "External Use";
                        break;
                }
                String herbDescription = edHerbDesc.getText().toString();
                String herbName = edHerbName.getText().toString();
                String dosage = edHerbDosage.getText().toString();
                String sideEffect = edHerbSideEffect.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                Date now = new Date();
                String fileName = formatter.format(now);

                if (imageUri == null) {
                    uploadUpdatedData(herbType, herbDescription, herbName, dosage, sideEffect);
                } else {
                    uploadUpdatedDataWithImage(fileName, herbType, herbDescription, herbName, dosage, sideEffect);
                }
            }
        });

    }

    private void uploadUpdatedData(String herbType, String herbDescription, String herbName, String dosage, String sideEffect) {

        Map<String, Object> updates = new HashMap<>();

        updates.put("herbalType", herbType);
        updates.put("herbalDescription", herbDescription);
        updates.put("herbalName", herbName);
        updates.put("dosage", dosage);
        updates.put("sideEffect", sideEffect);

        FirebaseFirestore.getInstance().collection("herbals").document(herbal.getHerbalId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateHerbalActivity.this, "Herbal Updated", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(UpdateHerbalActivity.this, HealerMainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateHerbalActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadUpdatedDataWithImage(String fileName, String herbType, String herbDescription, String herbName, String dosage, String sideEffect) {
        storageReference = FirebaseStorage.getInstance().getReference("HerbalImages/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StorageReference fileRef = FirebaseStorage.getInstance().getReference("HerbalImages");
                StorageReference ref = fileRef.child(fileName);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (herbType.isEmpty() || herbDescription.isEmpty() || herbName.isEmpty()) {
                            Toast.makeText(UpdateHerbalActivity.this, "Please Fill in the empty fields", Toast.LENGTH_SHORT).show();
                        } else {
                            Herbal newHerbal = new Herbal(herbal.getHerbalId(), herbName, uri.toString(), FirebaseUtil.currentUserId(), herbDescription, herbType, numOfViews, dosage, sideEffect);
                            FirebaseFirestore.getInstance().collection("herbals").document(herbal.getHerbalId()).set(newHerbal);
                            Toast.makeText(UpdateHerbalActivity.this, "Herbal Updated", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UpdateHerbalActivity.this, HealerMainActivity.class);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateHerbalActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateHerbalActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        edHerbName = findViewById(R.id.edHerbName);
        edHerbDesc = findViewById(R.id.edHerbDescription);
        edHerbDosage = findViewById(R.id.edHerbDosage);
        edHerbSideEffect = findViewById(R.id.edHerbSideEffect);
        newHerbPic = findViewById(R.id.addNewHerbPicture);
        backBtn = findViewById(R.id.addNewBackBtn);
        saveBtn = findViewById(R.id.addNewSaveBtn);
        radioGroup = findViewById(R.id.radioGroup);

        herbal = (Herbal) getIntent().getSerializableExtra("herbal");
        edHerbName.setText(herbal.getHerbalName());
        edHerbDesc.setText(herbal.getHerbalDescription());
        edHerbDosage.setText(herbal.getDosage());
        edHerbSideEffect.setText(herbal.getSideEffect());
        numOfViews = herbal.getNumOfViews();
        Glide.with(this).load(herbal.getHerbalImage()).into(newHerbPic);
        if (herbal.getHerbalType().equals("Internal Use")) {
            radioGroup.check(R.id.rbInternalUse);
        } else if (herbal.getHerbalType().equals("External Use")) {
            radioGroup.check(R.id.rbExternalUse);
        }
    }
}