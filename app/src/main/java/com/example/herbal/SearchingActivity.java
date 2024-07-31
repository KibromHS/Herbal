package com.example.herbal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.herbal.adapters.SearchHerbalAdapter;
import com.example.herbal.healer.HealerCallback;
import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.example.herbal.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchingActivity extends AppCompatActivity {
    private ImageView backBtn;
    private EditText searchHerbs;
    private RecyclerView rcSearchResults;
    private SearchHerbalAdapter adapter;
    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        initView();

        searchHerbs.requestFocus();

        searchHerbs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    term = s.toString();
                    setupSearchRc(term);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupSearchRc(String searchTerm) {
//        FirebaseFirestore.getInstance().collection("healer")
        Query query = FirebaseUtil.allHerbalsCollectionRef().whereGreaterThanOrEqualTo("herbalName", searchTerm);
        FirestoreRecyclerOptions<Herbal> options = new FirestoreRecyclerOptions.Builder<Herbal>().setQuery(query, Herbal.class).build();
        adapter = new SearchHerbalAdapter(options, this);
        rcSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rcSearchResults.setAdapter(adapter);
        adapter.startListening();
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

    private void initView() {
        backBtn = findViewById(R.id.backBtn);
        searchHerbs = findViewById(R.id.searchingHerbs);
        rcSearchResults = findViewById(R.id.recyclerViewSearchResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
            setupSearchRc(term);
        }
    }
}