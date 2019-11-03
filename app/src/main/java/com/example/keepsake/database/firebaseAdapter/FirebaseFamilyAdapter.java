package com.example.keepsake.database.firebaseAdapter;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirebaseFamilyAdapter extends FirebaseAdapter{
    public static final String TAG = "family_group";

    public static final String NAME_FIELD = "familyName";
    public static final String EXISTS_FIELD = "exists";

    public static final String MEMBERS_COLLECTION = "members";
    public static final String ADMIN_COLLECTION = "admin";
    public static final String JOIN_REQUEST_COLLECTION = "joinRequests";


    public static void createDocument(Context ctx, OnSuccessListener listener, Map data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    public static void getDocument(Context ctx, String documentID, OnSuccessListener listener) {
        FirebaseAdapter.getDocument(ctx, TAG, documentID, listener);
    }

    public static void createMemberDocument(Context ctx, String familyID, String userID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(MEMBERS_COLLECTION)
                .document(userID)
                .set(data);
    }

    public static void createAdminDocument(Context ctx, String familyID, String userID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(ADMIN_COLLECTION)
                .document(userID)
                .set(data);
    }

    public static void createJoinRequestDocument(Context ctx, String familyID, String userID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(JOIN_REQUEST_COLLECTION)
                .document(userID)
                .set(data);
    }

    public static void deleteJoinRequestDocument(Context ctx, String familyID, String userID){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(JOIN_REQUEST_COLLECTION)
                .document(userID)
                .delete();
    }

    public static void getAdminDocument(Context ctx, String familyID, String userID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(ADMIN_COLLECTION)
                .document(userID)
                .get()
                .addOnSuccessListener(listener);
    }

    public static void getMembersCollection(Context ctx, String familyID, EventListener<QuerySnapshot> listener){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(MEMBERS_COLLECTION)
                .addSnapshotListener(listener);
    }

    public static void getJoinRequestCollection(Context ctx, String familyID, EventListener<QuerySnapshot> listener){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(JOIN_REQUEST_COLLECTION)
                .addSnapshotListener(listener);
    }

    public static Query queryFamilyName(Context ctx, String familyName){
        return getDB(ctx)
                .collection(TAG)
                .whereEqualTo(NAME_FIELD, familyName);
    }
}
