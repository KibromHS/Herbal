package com.example.herbal.healer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.herbal.HealerNameCallback;
import com.example.herbal.R;
import com.example.herbal.healerAdapters.HealerHerbalsAdapter;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.example.herbal.utils.UserPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HealerHomeFragment extends Fragment {
    private Button addNewHerbBtn;
    private RecyclerView rcMyHerbs;
    private RecyclerView.Adapter<HealerHerbalsAdapter.ViewHolder> adapter;
    private TextView noHerbals, approvedText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healer_home, container, false);
        initView(view);

        addNewHerbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), AddNewActivity.class);
                startActivity(i);
            }
        });

        getHealerData(new HealerCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onHealerReceivedCallback(HealerModel healerModel) {
                if (healerModel.isApproved()) {
                    approvedText.setText("Approved");
                    approvedText.setTextColor(0xFFFFFFFF);
                } else {
                    approvedText.setText("Not Approved\nherbal@gmail.com");
                    approvedText.setTextColor(0xFF990000);
                }
             }
        });

        ArrayList<Herbal> myHerbs = new ArrayList<>();

        adapter = new HealerHerbalsAdapter(myHerbs);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcMyHerbs.setLayoutManager(manager);
        rcMyHerbs.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("herbals").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Herbal herbal = documentChange.getDocument().toObject(Herbal.class);
                        if (herbal.getHealerId().equals(FirebaseUtil.currentUserId()))
                            myHerbs.add(herbal);
                    }
                }

                if (myHerbs.size() == 0) {
                    noHerbals.setVisibility(View.VISIBLE);
                } else {
                    noHerbals.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }
        });

        return view;
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

    private void initView(View view) {
        addNewHerbBtn = view.findViewById(R.id.addNewHerbBtn);
        rcMyHerbs = view.findViewById(R.id.rcMyHerbs);
        noHerbals = view.findViewById(R.id.noHerbals);
        approvedText = view.findViewById(R.id.approvedText);
    }
}