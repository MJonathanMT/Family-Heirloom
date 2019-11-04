package com.example.keepsake.database.firebaseAdapter;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * This class handles all of the applications interactions with the Family collection.
 * */
public class FirebaseFamilyAdapter extends FirebaseAdapter{
    // collection reference
    public static final String TAG = "family_group";

    // field names
    public static final String NAME_FIELD = "familyName";
    public static final String EXISTS_FIELD = "exists";

    // nested collection names
    public static final String MEMBERS_COLLECTION = "members";
    public static final String ADMIN_COLLECTION = "admin";
    public static final String JOIN_REQUEST_COLLECTION = "joinRequests";


    /**
     * Creates a Family document
     * @param ctx              : The current context of the application
     * @param listener         : On success listener to call once data has been uploaded
     * @param data             : Map containing the field-value pairs of the document
     * */
    public static void createDocument(Context ctx, OnSuccessListener listener, Map data){
        FirebaseAdapter.createDocument(ctx, TAG, listener, data);
    }

    /**
     * Gets a Family document
     * @param ctx              : The current context of the application
     * @param documentID       : the ID of the document to be accessed
     * @param listener         : On success listener to call once data has been uploaded
     * */
    public static void getDocument(Context ctx, String documentID, OnSuccessListener listener) {
        FirebaseAdapter.getDocument(ctx, TAG, documentID, listener);
    }

    /**
     * Create a member document in family collection
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param userID           : the ID of a user to add as a member to the family
     * @param data             : The field-value pairs associated with the family member
     * */
    public static void createMemberDocument(Context ctx, String familyID, String userID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(MEMBERS_COLLECTION)
                .document(userID)
                .set(data);
    }

    /**
     * Create an admin document in family collection
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param userID           : the ID of a user to add as an admin to the family
     * @param data             : The field-value pairs associated with the family admin
     * */
    public static void createAdminDocument(Context ctx, String familyID, String userID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(ADMIN_COLLECTION)
                .document(userID)
                .set(data);
    }

    /**
     * Create an document for a user requesting to join the family
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param userID           : the ID of a user requesting to join the family
     * @param data             : The field-value pairs associated with the user
     * */
    public static void createJoinRequestDocument(Context ctx, String familyID, String userID, Map data){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(JOIN_REQUEST_COLLECTION)
                .document(userID)
                .set(data);
    }

    /**
     * Create an document for a user requesting to join the family
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param userID           : the ID of a user requesting to join the family
     * */
    public static void deleteJoinRequestDocument(Context ctx, String familyID, String userID){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(JOIN_REQUEST_COLLECTION)
                .document(userID)
                .delete();
    }

    /**
     * access an admin document in family collection
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param userID           : the ID of a user to add as an admin to the family
     * @param listener         : On success listener to call once data has been accessed
     * */
    public static void getAdminDocument(Context ctx, String familyID, String userID, OnSuccessListener listener){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(ADMIN_COLLECTION)
                .document(userID)
                .get()
                .addOnSuccessListener(listener);
    }

    /**
     * access the collection of members in the family collection
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param listener         : On success listener to call once data has been accessed
     * */
    public static void getMembersCollection(Context ctx, String familyID, EventListener<QuerySnapshot> listener){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(MEMBERS_COLLECTION)
                .addSnapshotListener(listener);
    }

    /**
     * access the collection of join requests in the family collection
     * @param ctx              : The current context of the application
     * @param familyID         : the ID of the family  document to be accessed
     * @param listener         : On success listener to call once data has been accessed
     * */
    public static void getJoinRequestCollection(Context ctx, String familyID, EventListener<QuerySnapshot> listener){
        getDB(ctx)
                .collection(TAG)
                .document(familyID)
                .collection(JOIN_REQUEST_COLLECTION)
                .addSnapshotListener(listener);
    }

    /**
     * access the collection of family in the family collection
     * @param ctx              : The current context of the application
     * @param familyName       : name of the family to query
     * */
    public static Query queryFamilyName(Context ctx, String familyName){
        return getDB(ctx)
                .collection(TAG)
                .whereEqualTo(NAME_FIELD, familyName);
    }
}
