package com.example.herbal.utils;

import androidx.annotation.NonNull;

import com.example.herbal.models.HealerModel;
import com.example.herbal.models.Herbal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference getUser(String userId) {
        return FirebaseFirestore.getInstance().collection("users").document(userId);
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static CollectionReference allHerbalsCollectionRef() {
        return FirebaseFirestore.getInstance().collection("herbals");
    }

    public static CollectionReference allChatRoomCollectionRef() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static CollectionReference allUserCollectionRef() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference allHealerCollectionRef() {
        return FirebaseFirestore.getInstance().collection("healers");
    }

    public static DocumentReference getHealerFromChatroom(String healerId) {
        return allHealerCollectionRef().document(healerId);
    }

    public static DocumentReference getUserFromChatroom(String userId) {
        return allUserCollectionRef().document(userId);
    }

    public static String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return dateFormat.format(timestamp.toDate());
    }

    public static DocumentReference getHealerReference(String herbalId) {
        final DocumentReference[] docRef = new DocumentReference[1];
        FirebaseFirestore.getInstance().collection("herbals").document(herbalId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String healerId = task.getResult().getString("healerId");
                    assert healerId != null;
                    docRef[0] = FirebaseFirestore.getInstance().collection("healers").document(healerId);
                }
            }
        });
        return docRef[0];
    }
}
