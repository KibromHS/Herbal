package com.example.herbal.healer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.herbal.R;
import com.example.herbal.adapters.ChatAdapter;
import com.example.herbal.adapters.MessagesAdapter;
import com.example.herbal.healerAdapters.HealerMessagesAdapter;
import com.example.herbal.models.ChatroomModel;
import com.example.herbal.models.MessageModel;
import com.example.herbal.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class HealerMessageFragment extends Fragment {
    private RecyclerView rcMessages;
    private HealerMessagesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healer_message, container, false);
        initView(view);

        setupMessages(view);

        return view;
    }

    private void initView(View view) {
        rcMessages = view.findViewById(R.id.recyclerViewMessages);
    }

    private void setupMessages(View view) {
        Query query = FirebaseUtil.allChatRoomCollectionRef()
                .whereEqualTo("healerId", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>().setQuery(query, ChatroomModel.class).build();
        adapter = new HealerMessagesAdapter(options, view.getContext());
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcMessages.setLayoutManager(manager);
        rcMessages.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}