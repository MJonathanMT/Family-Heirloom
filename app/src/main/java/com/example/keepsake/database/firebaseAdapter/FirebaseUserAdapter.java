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

/**
 * This class handles all of the applications interactions with the User collection.
 * */
public class FirebaseUserAdapter extends FirebaseAdapter {
    public static final String TAG = "user";

    // field names
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String USERNAME_FIELD = "username";
    public static final String USER_SESSION_FIELD = "userSession";
    public static final String EMAIL_FIELD = "email";
    public static final String ACCEPTED_FIELD= "accepted";
    public static final String URL_FIELD= "url";

    // nested collections
    public static final String FAMILY_GROUPS_COLLECTION = "familyGroups";
    public static final String ITEMS_COLLECTION = "items";

    /***
     * Create a document in the user collection using a map of field-value pairs
     * @param ctx           : the current context of the application
     * @param listener      : On success listener to call once data has been accessed
     * @param data          : The field-value pairs associated with the user
     */
    public static void createDocument(Context ctx, OnSuccessListener listener, Map data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    /***
     * Create a document in the user collection using a map of field-value pairs
     * @param ctx           : the current context of the application
     * @param userID        : the ID of the user to create
     * @param data          : Firebase snapshot storing the data of the document
     */
    public static void createDocument(Context ctx, String userID, User data){
        FirebaseAdapter.createDocument(ctx, TAG, userID, data);
    }

    /***
     * Access a user document in the database
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user to access
     * @param listener      : On success listener to call ocne the data has been accessed
     */
    public static void getDocument(Context ctx, String documentID, OnSuccessListener listener){
        FirebaseAdapter.getDocument(ctx, TAG, documentID, listener);
    }

    /***
     * Access a user document in the database
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user to access
     * @return              : a reference to the document
     */
    public static DocumentReference getDocument(Context ctx, String documentID){
         return FirebaseAdapter.getDocument(ctx, TAG, documentID);
    }

    /***
     * Update a field-value pair of a user document in the database
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user to access
     * @param listener      : On success listener to call once the data has been accessed
     * @param field         : the field of the document to change
     * @param value         : the new value of the field
     **/
    public static void updateDocument(Context ctx, String documentID, OnSuccessListener listener, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, listener, field, value);
    }

    /***
     * Update a set of field-value pairs of a user document in the database
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user to access
     * @param data          : a map containing the field-value pairs to update
     **/
    public static void updateDocument(Context ctx, String documentID, HashMap<String, String> data){
        for (Map.Entry<String, String> entry : data.entrySet()) {
            FirebaseUserAdapter.updateDocument(ctx, documentID, entry.getKey(), entry.getValue());
        }
    }

    /***
     * Update a field-value pair of a user document in the database
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user to access
     * @param field         : the field of the document to change
     * @param value         : the new value of the field
     **/
    public static void updateDocument(Context ctx, String documentID, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, field, value);
    }

    /***
     * Query returning all users with a specified first name
     * @param ctx           : the current context of the application
     * @param firstName     : the name to search
     * @return              : the results of the query
     */
    public static Query queryUserFirstName(Context ctx, String firstName){
        return getDB(ctx)
                .collection(TAG)
                .whereEqualTo(FIRST_NAME_FIELD, firstName);
    }

    /***
     * Query returning all users with a specified first and last
     * @param ctx           : the current context of the application
     * @param firstName     : the first name to search
     * @param lastName      : the last name to search
     * @return              : the results of the query
     */
    public static Query queryUserFullName(Context ctx, String firstName, String lastName){
        return getDB(ctx)
                .collection(TAG)
                .whereEqualTo(FIRST_NAME_FIELD, firstName)
                .whereEqualTo(LAST_NAME_FIELD, lastName);
    }

    /***
     * Create a reference to a family that the user is a member of
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param familyID      : the ID of the family group to add
     * @param data          : A map containg the field-value pairs of the document
     */
    public static void createFamilyDocument(Context ctx, String documentID, String familyID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION)
                .document(familyID)
                .set(data);
    }

    /***
     * Create a reference to an item that the user owns
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param itemID        : the ID of the item to add
     * @param data          : A map containg the field-value pairs of the document
     */
    public static void createItemDocument(Context ctx, String documentID, String itemID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(ITEMS_COLLECTION)
                .document(itemID)
                .set(data);
    }

    /***
     * Delete a reference to an item that the user owns
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param itemID        : the ID of the item to add
     * @param listener      : On success listener to call once the data has been accessed
     */
    public static void deleteItemDocument(Context ctx, String documentID, String itemID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(ITEMS_COLLECTION)
                .document(itemID)
                .delete()
                .addOnSuccessListener(listener);
    }

    /***
     * Access a reference to a family that the user is a member of
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param familyID        : the ID of the item to add
     * @param listener      : On success listener to call once the data has been accessed
     */
    public static void getFamilyDocument(Context ctx, String documentID, String familyID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION)
                .document(familyID)
                .get()
                .addOnSuccessListener(listener);
    }

    /***
     * Query all families that the user is a member of
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @return              : the results of the query
     */
    public static Query queryAcceptedFamilies(Context ctx, String documentID){
        return queryFamiliesOnStatus(ctx, documentID, "1");
    }

    /***
     * Query all families that the user is a member of
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param listener      : On success listener to call once the data has been accessed
     */
    public static void getAcceptedFamilies(Context ctx, String documentID, OnSuccessListener<QuerySnapshot> listener){
          queryFamiliesOnStatus(ctx, documentID, "1")
                .get()
                .addOnSuccessListener(listener);
    }

    /***
     * Query all families based on acceptance status
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param value         : The acceptance value (1 = accepted, 0 = not accepted)
     */
    public static Query queryFamiliesOnStatus(Context ctx, String documentID, String value){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION)
                .whereEqualTo(ACCEPTED_FIELD, value);
    }

    /***
     * Query all families
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @return              : the results of the query
     */
    public static Query queryFamilies(Context ctx, String documentID){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(FAMILY_GROUPS_COLLECTION);
    }

    /***
     * Delete the document of an item that the user owns
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the user
     * @param itemID        : the ID of the item to delete
     */
    public static void deleteItem(Context ctx, String documentID, String itemID){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(ITEMS_COLLECTION)
                .document(itemID)
                .delete();
    }

    /***
     * Upload a user profile image to the storage
     * @param ctx       : the current context of the user
     * @param uri       : the path to the image
     * @param imagePath : the name of the image
     * @param listener  : On success listener to call once the data has been accessed
     */
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
