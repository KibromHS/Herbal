package com.example.herbal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.herbal.adapters.HerbsAdapter;
import com.example.herbal.healer.HealerCallback;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView rcHerbs;
    private RecyclerView rcTea;
    private EditText searchHerbs;
    private ImageView herbalImage, herbalType1Underline, herbalType2Underline;
    private TextView herbalNameAndHealer, herbalType1, herbalType2, noHerbalsText;
    private ConstraintLayout adSection;
    private ArrayList<Herbal> herbs, teas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);

        herbs = new ArrayList<>();
        teas = new ArrayList<>();

        ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        RecyclerView.Adapter<HerbsAdapter.ViewHolder> herbsAdapter = new HerbsAdapter(herbs);
        RecyclerView.Adapter<HerbsAdapter.ViewHolder> teasAdapter = new HerbsAdapter(teas);

        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcHerbs.setLayoutManager(manager);
        rcHerbs.setAdapter(herbsAdapter);
        rcTea.setLayoutManager(layoutManager);
        rcTea.setAdapter(teasAdapter);

        FirebaseFirestore.getInstance().collection("herbals").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Herbal herbal = documentChange.getDocument().toObject(Herbal.class);
                        if (herbal.getHerbalType().equals("Internal Use")) {
                            herbs.add(herbal);
                        } else if (herbal.getHerbalType().equals("External Use")) {
                            teas.add(herbal);
                        }
                    }
                }

                if (herbs.size() == 0 && teas.size() == 0) {
                    herbalType1.setVisibility(View.GONE);
                    herbalType1Underline.setVisibility(View.GONE);
                    herbalType2.setVisibility(View.GONE);
                    herbalType2Underline.setVisibility(View.GONE);
                    adSection.setVisibility(View.GONE);
                    noHerbalsText.setVisibility(View.VISIBLE);
                } else if (herbs.size() == 0) {
                    herbalType1.setVisibility(View.GONE);
                    herbalType1Underline.setVisibility(View.GONE);
                    noHerbalsText.setVisibility(View.GONE);
                    herbalType2.setVisibility(View.VISIBLE);
                    herbalType2Underline.setVisibility(View.VISIBLE);

                    getHealerName(teas.get(0).getHealerId(), new HealerNameCallback() {
                        @Override
                        public void onHealerNameReceived(String healerName) {
                            Glide.with(view.getContext()).load(teas.get(0).getHerbalImage()).into(herbalImage);
                            String builder = teas.get(0).getHerbalName() + " by " + healerName;
                            herbalNameAndHealer.setText(builder);

                            adSection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(view.getContext(), HerbalActivity.class);
                                    i.putExtra("herbal", teas.get(0));
                                    startActivity(i);
                                }
                            });
                        }
                    });
                } else if (teas.size() == 0) {
                    herbalType2.setVisibility(View.GONE);
                    herbalType2Underline.setVisibility(View.GONE);
                    noHerbalsText.setVisibility(View.GONE);
                    herbalType1.setVisibility(View.VISIBLE);
                    herbalType1Underline.setVisibility(View.VISIBLE);

                    getHealerName(herbs.get(0).getHealerId(), new HealerNameCallback() {
                        @Override
                        public void onHealerNameReceived(String healerName) {
                            Glide.with(view.getContext()).load(herbs.get(0).getHerbalImage()).into(herbalImage);
                            String builder = herbs.get(0).getHerbalName() + " by " + healerName;
                            herbalNameAndHealer.setText(builder);

                            adSection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(view.getContext(), HerbalActivity.class);
                                    i.putExtra("herbal", herbs.get(0));
                                    startActivity(i);
                                }
                            });
                        }
                    });
                } else {
                    herbalType1.setVisibility(View.VISIBLE);
                    herbalType1Underline.setVisibility(View.VISIBLE);
                    herbalType2.setVisibility(View.VISIBLE);
                    herbalType2Underline.setVisibility(View.VISIBLE);
                    adSection.setVisibility(View.VISIBLE);
                    noHerbalsText.setVisibility(View.GONE);

                    getHealerName(teas.get(0).getHealerId(), new HealerNameCallback() {
                        @Override
                        public void onHealerNameReceived(String healerName) {
                            Glide.with(view.getContext()).load(teas.get(0).getHerbalImage()).into(herbalImage);
                            String builder = teas.get(0).getHerbalName() + " by " + healerName;
                            herbalNameAndHealer.setText(builder);

                            adSection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(view.getContext(), HerbalActivity.class);
                                    i.putExtra("herbal", teas.get(0));
                                    startActivity(i);
                                }
                            });
                        }
                    });

                    getHealerName(herbs.get(0).getHealerId(), new HealerNameCallback() {
                        @Override
                        public void onHealerNameReceived(String healerName) {
                            Glide.with(view.getContext()).load(herbs.get(0).getHerbalImage()).into(herbalImage);
                            String builder = herbs.get(0).getHerbalName() + " by " + healerName;
                            herbalNameAndHealer.setText(builder);

                            adSection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(view.getContext(), HerbalActivity.class);
                                    i.putExtra("herbal", herbs.get(0));
                                    startActivity(i);
                                }
                            });
                        }
                    });
                }

                herbsAdapter.notifyDataSetChanged();
                teasAdapter.notifyDataSetChanged();
            }
        });

        if (progressDialog.isShowing()) progressDialog.dismiss();

        searchHerbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), SearchingActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void getHealerName(String healerId, HealerNameCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(healerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String healerName = documentSnapshot.getString("healerName");
                callback.onHealerNameReceived(healerName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Couldn't get healer data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHealer(String healerId, HealerCallback callback) {
        FirebaseFirestore.getInstance().collection("healers").document(healerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HealerModel healer = documentSnapshot.toObject(HealerModel.class);
                callback.onHealerReceivedCallback(healer);
            }
        });
    }

    private void initView(View view) {
        rcHerbs = view.findViewById(R.id.recyclerViewHerbs);
        rcTea = view.findViewById(R.id.recyclerViewTea);
        searchHerbs= view.findViewById(R.id.searchHerbs);
        herbalImage = view.findViewById(R.id.herbalImageHome);
        herbalNameAndHealer = view.findViewById(R.id.homeHerbalNameAndHealer);
        herbalType1 = view.findViewById(R.id.herbalType1);
        herbalType2 = view.findViewById(R.id.herbalType2);
        herbalType1Underline = view.findViewById(R.id.herbalType1Underline);
        herbalType2Underline = view.findViewById(R.id.herbalType2Underline);
        adSection = view.findViewById(R.id.adSection);
        noHerbalsText = view.findViewById(R.id.noHerbalsText);
    }
}