package com.example.keepsake.database.firebaseAdapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Map;

public abstract class FirebaseAdapter {
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static FirebaseAuth auth;


    protected static OnFailureListener failureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.d("Error:", e.getMessage());
        }
    };

    private static void initialiseDB(Context ctx) {
        FirebaseApp.initializeApp(ctx);
        db = FirebaseFirestore.getInstance();
    }

    protected static FirebaseFirestore getDB(Context ctx) {
        if (db == null) {
            initialiseDB(ctx);
        }

        return db;
    }

    private static void initialiseStorage() {
        storage = FirebaseStorage.getInstance();
    }

    protected static FirebaseStorage getStorage() {
        if (storage == null) {
            initialiseStorage();
        }

        return storage;
    }

    private static void initialiseAuth() {
        auth = FirebaseAuth.getInstance();
    }

    protected static FirebaseAuth getAuth() {
        if (auth == null) {
            initialiseAuth();
        }

        return auth;
    }

    protected static void createDocument(Context ctx, String collection, OnSuccessListener listener, Map data){
        getDB(ctx)
                .collection(collection)
                .add(data)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    protected static void createDocument(Context ctx, String collection, OnSuccessListener listener, Object data){
        getDB(ctx)
                .collection(collection)
                .add(data)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    protected static void createDocument(Context ctx, String collection, String documentID, Object data){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .set(data)
                .addOnFailureListener(failureListener);
    }

    protected static void getDocument(Context ctx, String collection, String documentID, OnSuccessListener listener){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .get()
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    protected static DocumentReference getDocument(Context ctx, String collection, String documentID){
        return getDB(ctx)
                .collection(collection)
                .document(documentID);
    }

    protected static void updateDocument(Context ctx, String collection, String documentID, String field, String value){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .update(field, value)
                .addOnFailureListener(failureListener);
    }

    protected static void updateDocument(Context ctx, String collection, String documentID, OnSuccessListener listener, String field, String value){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .update(field, value)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }
}