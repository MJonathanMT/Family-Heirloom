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

/**
 * This class handles all of the applications interactions with the Item collection.
 * */
public class FirebaseItemAdapter extends FirebaseAdapter {
    public static final String TAG = "item";

    // field names
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

    // standardised date format used in Images
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    // privacy levels
    // only viewable by owner
    public static final String PRIVACY_OWNER = "O";
    // only viewable by Family
    public static final String PRIVACY_FAMILY = "F";
    // viewable by everyone
    public static final String PRIVACY_PUBLIC = "P";

    // nested collections
    public static final String OWNERSHIP_RECORD_COLLECTION = "ownership_record";

    /***
     * Create a document in the items collection using a map of field-value pairs
     * @param ctx           : the current context of the application
     * @param listener      : On success listener to call once data has been accessed
     * @param data          : The field-value pairs associated with the item
     */
    public static void createDocument(Context ctx, OnSuccessListener listener, Map data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    /***
     * create a document in the items collection using a Firebase Snapshot
     * @param ctx           : the current context of the application
     * @param listener      : On success listener to call once data has been accessed
     * @param data          : The field-value pairs associated with the item
     */
    public static void createDocument(Context ctx, OnSuccessListener listener, Item data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    /***
     * access a document in the items collection
     * @param ctx           : current context of the application
     * @param documentID    : the document ID of the item to access
     * @param listener      : On success listener to call once the data has been accessed
     */
    public static void getDocument(Context ctx, String documentID, OnSuccessListener listener){
        FirebaseAdapter.getDocument(ctx, TAG, documentID, listener);
    }

    /***
     * Access a document in the items collection
     * @param ctx           : the current context of the application
     * @param documentID    : the document ID of the item to access
     * @return              : A reference to the document
     */
    public static DocumentReference getDocument(Context ctx, String documentID){
        return FirebaseAdapter.getDocument(ctx, TAG, documentID);
    }

    /***
     * Update a field-value pair of a document in the items collection
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the document to access
     * @param listener      : On success listener to call once the data has been accessed
     * @param field         : the field of the document to change
     * @param value         : the new value of the field
     */
    public static void updateDocument(Context ctx, String documentID, OnSuccessListener listener, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, listener, field, value);
    }

    /***
     * Update a field-value pair of a document in the items collection
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the document to access
     * @param field         : the field of the document to change
     * @param value         : the new value of the field
     */
    public static void updateDocument(Context ctx, String documentID, String field, String value){
        FirebaseAdapter.updateDocument(ctx, TAG, documentID, field, value);
    }

    /***
     * Update a set of field-value pairs of a document in the items collection
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the document to access
     * @param data          : map of field-value pairs to update
     */
    public static void updateDocument(Context ctx, String documentID, HashMap<String, String> data){
        for (Map.Entry<String, String> entry : data.entrySet()) {
            FirebaseItemAdapter.updateDocument(ctx, documentID, entry.getKey(), entry.getValue());
        }
    }

    /***
     * Delete all references to an item from the collection
     * @param ctx           : the current contect of the application
     * @param documentID    : the ID of the document to access
     */
    public static void deleteItem(Context ctx, String documentID){
        // delete all references of the item in all previous users' accounts
        OnSuccessListener listener = new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (QueryDocumentSnapshot document : snapshot) {
                    String userID = document.getId();

                    FirebaseUserAdapter.deleteItem(ctx, userID, documentID);
                }
                // delete the reference to the item in the collection
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

    /***
     * Delete an item from the collection
     * @param ctx           : the current contect of the application
     * @param documentID    : the ID of the document to access
     */
    public static void deleteDocument(Context ctx, String documentID){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .delete();
    }

    /***
     * Query to return all items in the collection that belong to a specific family
     * @param ctx        : the current contect of the application
     * @param familyID   : the familyID of the family
     * @param listener   : Event listener to call once the data has been accessed
     */
    public static void queryFamilyItems(Context ctx, String familyID, EventListener<QuerySnapshot> listener){
        getDB(ctx)
                .collection(TAG)
                .whereEqualTo(FAMILY_ID_FIELD, familyID)
                .addSnapshotListener(listener);
    }

    /***
     * Query to return the collection of ownership record associated with an item
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the item to access
     * @param listener      : On success listener to call once the data has been accessed
     */
    public static void getOwnershipRecordCollection(Context ctx, String documentID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .get()
                .addOnSuccessListener(listener)
                .addOnFailureListener(FirebaseAdapter.failureListener);
    }

    /***
     * Query to return the collection of ownership record associated with an item
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the item to access
     * @return              : the results of the query
     */
    public static Query queryOwnershipRecordCollection(Context ctx, String documentID){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .orderBy(START_DATE_FIELD, Query.Direction.DESCENDING);
    }

    /***
     * Create an ownership record for a specified item
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the item to access
     * @param data          : map containing field-value pairs of the ownership record
     */
    public static void createOwnershipRecordDocument(Context ctx, String documentID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .document()
                .set(data)
                .addOnFailureListener(FirebaseAdapter.failureListener);
    }

    /***
     * Query to return an ownership record for a specific user
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the item to access
     * @param userID        : The user ID to query
     * @param startDate     : the start date of the ownership record
     * @return              : the results of the query
     */
    public static Query queryOwnershipRecordDocument(Context ctx, String documentID, String userID, String startDate){
        return getDB(ctx)
                .collection(TAG)
                .document(documentID)
                .collection(OWNERSHIP_RECORD_COLLECTION)
                .whereEqualTo(START_DATE_FIELD, startDate)
                .whereEqualTo(OWNER_ID_FIELD, userID);
    }

    /***
     * Query to return all items owned by a specific user
     * @param ctx           : the current context of the application
     * @param listener      : On success listener to call once the data has been accessed
     * @param userID        : The user ID to query
     */
    public static void queryUserItemsCollection(Context ctx, OnSuccessListener listener, String userID){
        getDB(ctx)
                .collection(TAG)
                .whereEqualTo(OWNER_ID_FIELD, userID)
                .get()
                .addOnSuccessListener(listener);
    }

    /***
     * Access an ownership record for an item
     * @param ctx           : the current context of the application
     * @param documentID    : the ID of the item to access
     * @param userID        : the ID of the user
     * @param startDate     : the start date of the ownership record
     * @param listener      : On success listener to call once the data has been accessed
     */
    public static void getOwnershipRecordDocument(Context ctx, String documentID, String userID, String startDate, OnSuccessListener listener){
        queryOwnershipRecordDocument(ctx, documentID, userID, startDate)
                .get()
                .addOnSuccessListener(listener);
    }

    /***
     * Upload a user profile image to the database
     * @param ctx          : the current context of the application
     * @param uri          : the path of the image in the storage
     * @param imagePath    : the name of the image
     * @param listener     : On success listener to call once the data has been accessed
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
