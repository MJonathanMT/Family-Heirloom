package com.example.keepsake.database.firebaseAdapter;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.keepsake.database.firebaseSnapshot.Item;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FirebaseItemAdapter extends FirebaseAdapter {
    public static final String TAG = "item";

    public static final String ID_FIELD = "itemID";
    public static final String NAME_FIELD = "name";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String PRIVACY_FIELD = "privacy";
    public static final String FAMILY_ID_FIELD = "familyID";
    public static final String OWNER_ID_FIELD = "ownerID";
    public static final String EXISTS_FIELD = "exists";
    public static final String START_DATE_FIELD = "startDate";
    public static final String MEMORY_FIELD = "memory";
    public static final String URL_FIELD = "url";

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String PRIVACY_OWNER = "O";
    public static final String PRIVACY_FAMILY = "F";
    public static final String PRIVACY_PUBLIC = "P";

    public static final String OWNERSHIP_RECORD_COLLECTION = "ownership_record";

    public static void createDocument(Context ctx, OnSuccessListener listener, Map data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    public static void createDocument(Context ctx, OnSuccessListener listener, Item data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    public static void getDocument(Context ctx, String documentID, OnSuccessListener listener){
        FirebaseAdapter.getDocument(ctx, TAG, documentID, listener);
    }

    public static DocumentReference getDocument(Context ctx, String documentID){
        return FirebaseAdapter.getDocument(ctx, TAG, documentID);
    }

    public static void updateDocument(Context ctx, String documentID, OnSuccessListener listener, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, listener, field, value);
    }

    public static void updateDocument(Context ctx, String documentID, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, field, value);
    }

    public static void updateDocument(Context ctx, String documentID, HashMap<String, String> data){
        for (Map.Entry<String, String> entry : data.entrySet()) {
            FirebaseItemAdapter.updateDocument(ctx, documentID, entry.getKey(), entry.getValue());
        }
    }

    public static void deleteItem(Context ctx, String documentID){
        OnSuccessListener listener = new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (QueryDocumentSnapshot document : snapshot) {
                    String userID = document.getId();

                    FirebaseUserAdapter.deleteItem(ctx, userID, documentID);
                }
                deleteDocument(ctx, documentID);
        }};

        getOwnershipRecordCollection(ctx, documentID, listener);

        getDB(ctx)
            .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .get()
                .addOnSuccessListener(listener);
    }

    public static void deleteDocument(Context ctx, String documentID){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .delete();
    }

    public static void deleteOwnershipRecordDocument(Context ctx, String documentID, String userID){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .document(userID)
                .delete();
    }

    public static void queryFamilyItems(Context ctx, String familyID, EventListener<QuerySnapshot> listener){
        getDB(ctx)
                .collection(TAG)
                .whereEqualTo(FAMILY_ID_FIELD, familyID)
                .addSnapshotListener(listener);
    }

    public static void getOwnershipRecordCollection(Context ctx, String documentID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .get()
                .addOnSuccessListener(listener)
                .addOnFailureListener(FirebaseAdapter.failureListener);
    }

    public static Query queryOwnershipRecordCollection(Context ctx, String documentID){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .orderBy(START_DATE_FIELD, Query.Direction.DESCENDING);
    }

    public static void createOwnershipRecordDocument(Context ctx, String documentID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .document()
                .set(data)
                .addOnFailureListener(FirebaseAdapter.failureListener);
    }

    public static Query queryOwnershipRecordDocument(Context ctx, String documentID, String userID, String startDate){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .whereEqualTo(START_DATE_FIELD, startDate)
                .whereEqualTo(OWNER_ID_FIELD, userID);
    }

    public static void getItemsCollection(Context ctx, OnSuccessListener listener){
         getDB(ctx)
                .collection(TAG)
                .get()
                .addOnSuccessListener(listener);
    }

    public static void queryUserItemsCollection(Context ctx, OnSuccessListener listener, String userID){
        getDB(ctx)
                .collection(TAG)
                .whereEqualTo(OWNER_ID_FIELD, userID)
                .get()
                .addOnSuccessListener(listener);
    }

    public static void getOwnershipRecordDocument(Context ctx, String documentID, String userID, String startDate, OnSuccessListener listener){
        queryOwnershipRecordDocument(ctx, documentID, userID, startDate)
                .get()
                .addOnSuccessListener(listener);
    }

    public static void uploadImage(Context ctx, Uri uri, String imagePath, OnSuccessListener listener){
        final StorageReference imageRef = getStorage()
                .getReference(TAG)
                .child(imagePath);

        imageRef.putFile(uri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return imageRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(listener);
    }

}
