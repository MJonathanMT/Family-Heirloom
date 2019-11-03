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

public abstract class FirebaseAuthAdapter extends FirebaseAdapter{

    public static void signIn(String email, String password, OnSuccessListener listener){
        getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    public static void signOut(){
        getAuth().signOut();
    }

    public static void notifyResetPassword(String email, OnSuccessListener listener){
        getAuth()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    public static void createUser(String email, String password, OnSuccessListener listener){
        getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    public static void deleteUser(OnSuccessListener listener){
        getAuth()
                .getCurrentUser()
                .delete()
                .addOnSuccessListener(listener);
    }

    public static FirebaseUser getCurrentUser(){
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserID(){
        return getAuth().getCurrentUser()
                .getUid();
    }
}