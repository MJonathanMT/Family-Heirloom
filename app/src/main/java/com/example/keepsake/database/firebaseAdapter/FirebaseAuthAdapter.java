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

/**
 * This class that handles all of the applications calls that involve user authentication. This class
 * stores the references to the firebase authentication portal and handles all calls
 * to the FirebaseFirestore instance. This is used to standardise the authentication process across
 * the application.
 * */
public abstract class FirebaseAuthAdapter extends FirebaseAdapter{
    /**
     * Function to verify and sign a user into the application using a previously created user account
     * @param email       : The users registered email account
     * @param password    : The users input password
     * @param listener         : On success listener to call once data has been uploaded
     * */
    public static void signIn(String email, String password, OnSuccessListener listener){
        getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Function to sign a user out of the application
     * */
    public static void signOut(){
        getAuth().signOut();
    }

    /**
     * Function to send an email to the user notifying them of a reset email request
     * @param email       : The users registered email account
     * @param listener         : On success listener to call once data has been uploaded
     * */
    public static void notifyResetPassword(String email, OnSuccessListener listener){
        getAuth()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Function to create a user account
     * @param email       : The users registered email account
     * @param password    : The users input password
     * @param listener         : On success listener to call once data has been uploaded
     * */
    public static void createUser(String email, String password, OnSuccessListener listener){
        getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Function to delete user account
     * @param listener         : On success listener to call once data has been uploaded
     * */
    public static void deleteUser(OnSuccessListener listener){
        getAuth()
                .getCurrentUser()
                .delete()
                .addOnSuccessListener(listener);
    }

    /**
     * Function to access the Firebase reference to the current user account
     * @return  : reference to the Firebase User account
     * */
    public static FirebaseUser getCurrentUser(){
        return getAuth().getCurrentUser();
    }

    /**
     * Function to access the ID of the current user account
     * @return  : ID of the current user
     * */
    public static String getCurrentUserID(){
        return getAuth().getCurrentUser()
                .getUid();
    }
}