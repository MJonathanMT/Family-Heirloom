package com.example.keepsake.database.firebaseAdapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseUserAdapter extends FirebaseAdapter {
    public static final String TAG = "user";

    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String USERNAME_FIELD = "username";
    public static final String USER_SESSION_FIELD = "userSession";
    public static final String EMAIL_FIELD = "email";
    public static final String ACCEPTED_FIELD= "accepted";
    public static final String URL_FIELD= "url";

    public static final String FAMILY_GROUPS_COLLECTION = "familyGroups";
    public static final String ITEMS_COLLECTION = "items";

    public static void createDocument(Context ctx, OnSuccessListener listener, Map data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    public static void createDocument(Context ctx, String userID, User data){
        FirebaseAdapter.createDocument(ctx, TAG, userID, data);
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

    public static void updateDocument(Context ctx, String documentID, HashMap<String, String> data){
        for (Map.Entry<String, String> entry : data.entrySet()) {
            FirebaseUserAdapter.updateDocument(ctx, documentID, entry.getKey(), entry.getValue());
        }
    }

    public static void updateDocument(Context ctx, String documentID, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, field, value);
    }

    public static Query queryUserFirstName(Context ctx, String firstName){
        return getDB(ctx)
                .collection(TAG)
                .whereEqualTo(FIRST_NAME_FIELD, firstName);
    }

    public static Query queryUserFullName(Context ctx, String firstName, String lastName){
        return getDB(ctx)
                .collection(TAG)
                .whereEqualTo(FIRST_NAME_FIELD, firstName)
                .whereEqualTo(LAST_NAME_FIELD, lastName);
    }

    public static void createFamilyDocument(Context ctx, String documentID, String familyID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION)
                .document(familyID)
                .set(data);
    }

    public static void createItemDocument(Context ctx, String documentID, String itemID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(ITEMS_COLLECTION)
                .document(itemID)
                .set(data);
    }

    public static void deleteItemDocument(Context ctx, String documentID, String itemID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(ITEMS_COLLECTION)
                .document(itemID)
                .delete()
                .addOnSuccessListener(listener);
    }

    public static void getFamilyDocument(Context ctx, String documentID, String familyID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION)
                .document(familyID)
                .get()
                .addOnSuccessListener(listener);
    }

    public static Query queryAcceptedFamilies(Context ctx, String documentID){
        return queryFamiliesOnStatus(ctx, documentID, "1");
    }

    public static void getAcceptedFamilies(Context ctx, String documentID, OnSuccessListener<QuerySnapshot> listener){
          queryFamiliesOnStatus(ctx, documentID, "1")
                .get()
                .addOnSuccessListener(listener);
    }

    public static Query queryFamiliesOnStatus(Context ctx, String documentID, String value){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION)
                .whereEqualTo(ACCEPTED_FIELD, value);
    }

    public static Query queryFamilies(Context ctx, String documentID){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION);
    }

    public static void deleteItem(Context ctx, String documentID, String itemID){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(ITEMS_COLLECTION)
                .document(itemID)
                .delete();
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
