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
 * This class handles all of the applications interactions with the database. This class
 * stores the references to the database, storage and authentication portal and handles all calls
 * to the FirebaseFirestore instance. This is used to standardise access to the database across the
 *  application.
 *
 * */
public abstract class FirebaseAdapter {
    //reference to the database instance
    private static FirebaseFirestore db;
    // reference to the storage portal for images
    private static FirebaseStorage storage;
    // reference to the authentication portal
    private static FirebaseAuth auth;


    // Failure listener =to log all failed calls to the database
    protected static OnFailureListener failureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.d("Error:", e.getMessage());
        }
    };

    /**
     * Function to initialise the database
     * @param ctx       : The current conntext of the application
     * */
    private static void initialiseDB(Context ctx) {
        FirebaseApp.initializeApp(ctx);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Function to standardise access to the database
     * @param ctx       : The current context of the application
     * @return          : instance of the database
     * */
    protected static FirebaseFirestore getDB(Context ctx) {
        // initialise database
        if (db == null) {
            initialiseDB(ctx);
        }

        return db;
    }

    /**
     * Function to initialise image storage
     * */
    private static void initialiseStorage() {
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Function to standardise access to storage
     * @return      : instance of the storage
     * */
    protected static FirebaseStorage getStorage() {
        // initialise storage
        if (storage == null) {
            initialiseStorage();
        }

        return storage;
    }

    /**
     * Function to initialise firebase authentication
     * */
    private static void initialiseAuth() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Function to standardise access to  firebase authentication
     * @return      : instance of the firebase authentication
     * */
    protected static FirebaseAuth getAuth() {
        // intialise authentication
        if (auth == null) {
            initialiseAuth();
        }

        return auth;
    }

    /**
     * Creates a document in a specified collection
     * @param ctx              : The current conntext of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param listener         : On success listener to call once data has been uploaded
     * @param data             : Map containing the field-value pairs of the document
     * */
    protected static void createDocument(Context ctx, String collection, OnSuccessListener listener, Map data){
        getDB(ctx)
                .collection(collection)
                .add(data)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Creates a document in a collection using a Firebase Snapshot
     * @param ctx              : The current conntext of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param listener         : On success listener to call once data has been uploaded
     * @param data             : Firebase Snapshot storing the values of the document
     * */
    protected static void createDocument(Context ctx, String collection, OnSuccessListener listener, Object data){
        getDB(ctx)
                .collection(collection)
                .add(data)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Creates a document in a specified collection with a specified ID using a Firebase Snapshot
     * @param ctx              : The current conntext of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param documentID       : Document ID to used to referene the snapshot in the database
     * @param data             : Firebase Snapshot storing the values of the document
     * */
    protected static void createDocument(Context ctx, String collection, String documentID, Object data){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .set(data)
                .addOnFailureListener(failureListener);
    }

    /**
     * Gets a document in a specified collection
     * @param ctx              : The current context of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param documentID       : Document ID to used to referene the snapshot in the database
     * @param listener         : On success listener to call once data has been accessed
     * */
    protected static void getDocument(Context ctx, String collection, String documentID, OnSuccessListener listener){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .get()
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Gets a document in a specified collection
     * @param ctx              : The current context of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param documentID       : Document ID to used to referene the snapshot in the database
     * @return                 : reference to the document
     * */
    protected static DocumentReference getDocument(Context ctx, String collection, String documentID){
        return getDB(ctx)
                .collection(collection)
                .document(documentID);
    }

    /**
     * Updates a field of a document in a specified collection
     * @param ctx              : The current context of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param documentID       : Document ID to used to referene the snapshot in the database
     * @param field            : The field of the document to change
     * @param value            : The updated value of the field
     * */
    protected static void updateDocument(Context ctx, String collection, String documentID, String field, String value){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .update(field, value)
                .addOnFailureListener(failureListener);
    }

    /**
     * Updates a field of a document in a specified collection
     * @param ctx              : The current context of the application
     * @param collection       : string indicating the collection to upload the document to
     * @param documentID       : Document ID to used to referene the snapshot in the database
     * @param listener         : On success listener to call once data has been accessed
     * @param field            : The field of the document to change
     * @param value            : The updated value of the field
     * */
    protected static void updateDocument(Context ctx, String collection, String documentID, OnSuccessListener listener, String field, String value){
        getDB(ctx)
                .collection(collection)
                .document(documentID)
                .update(field, value)
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener);
    }
}