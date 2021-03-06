package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.example.keepsake.database.firebaseSnapshot.Item;
import com.example.keepsake.utils.adapter.ItemsListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/***
 * This activity displays all the items
 * in the currentUser's userSession(familyGroup)
 */
public class ViewFamilyItemsActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {
    private final String TAG = "Family Items";

    private ActionBarDrawerToggle drawerToggle;
    private String userID  = FirebaseAuthAdapter.getCurrentUserID();
    private List<Item> itemList;
    private ItemsListAdapter itemsListAdapter;
    private RecyclerView posts;
    private User currentUser;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_family_items);

        itemList = new ArrayList<>();
        createUserClass();

        createNavBar();
        bindViews();
    }

    /***
     * This function creates a user class that could be accessed in this activity.
     * It pulls data off the currentUser that is logged-in
     * and creates an instance of a User class of the currentUser
     */
    private void createUserClass(){
        // create a user class for the current user
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    currentUser = documentSnapshot.toObject(User.class);

                    if (currentUser != null){
                        currentUser.setUserID(documentSnapshot.getId());

                        if (!currentUser.getUserID().isEmpty()){
                            loadFamilyItemViews();
                        }
                    }
                }

            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    /***
     * This function checks whether the currentUser is a member of the familyGroup
     * and is already accepted by an admin.
     * Only then, it will call loadItems function
     */
    private void loadFamilyItemViews(){
        // get all the items relevant to the current user
        if(currentUser.getUserSession() != null) {
            OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String acceptedString = documentSnapshot.get(FirebaseUserAdapter.ACCEPTED_FIELD, String.class);

                        if (acceptedString.compareTo("1") == 0){
                            loadItems();

                        }
                    }
                }
            };

            FirebaseUserAdapter.getFamilyDocument(this, userID, currentUser.getUserSession(), listener);
        }
    }

    /***
     * this function loads all the items that is
     * under the currentUser's userSession(familyID)
     */
    private void loadItems(){
        EventListener<QuerySnapshot> listener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", "Error: " + e.getMessage());
                }

                Log.d("userID", " " + userID);
                Log.d("userSession", " " + currentUser.getUserSession());

                if (queryDocumentSnapshots != null) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getDocument().exists()){
                            Item item = doc.getDocument().toObject(Item.class);
                            item.setItemID(doc.getDocument().getId());

                            Log.d("Item", " " + item.getItemID());
                            Log.d("Family", " " + item.getFamilyID());
                            Log.d("Owner", " " + item.getOwnerID());


                            // temporary guard until database is cleaned Up
                            if (item.getFamilyID() == null || item.getOwnerID() == null) {
                                continue;
                            }

                            if (userID.compareTo(item.getOwnerID()) == 0){
                                itemList.add(item);
                            }

                            else if (currentUser.getUserSession().compareTo(item.getFamilyID()) == 0){
                                if(item.getPrivacy().compareTo("O") != 0){
                                    itemList.add(item);
                                }
                            }

                            if (itemList.size() == 1){
                                createFamilyItemView();
                            }
                        }


                    }
                }
            }
        };

        FirebaseItemAdapter.queryFamilyItems(this, currentUser.getUserSession(), listener);
    }

    /***
     * This function creates the adapter
     * and connects the posts with the recycler viewer
     * It also connects the adapter with the recyclerViewer.
     */
    private void createFamilyItemView(){
        itemsListAdapter = new ItemsListAdapter(itemList, this);

        posts = findViewById(R.id.main_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(itemsListAdapter);
        itemsListAdapter.notifyDataSetChanged();
    }

    /***
     * This function creates a navigation bar on the current activity you are on
     * The purpose of the navigation bar is to be able to
     * access the main pages of the application from this activity.
     */
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.viewFamilyItemsDrawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView displayMessage = findViewById(R.id.user_header_welcome_message);
                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_picture);

                // Prints the name of the user session base on id of the view
                displayMessage.setText(currentUser.getFirstName()+ " " + currentUser.getLastName());
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonFamilyItemsAccess) {
                    openViewFamilyItemsActivity();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openFamilyMemberPageActivity();
                } else if (id == R.id.ButtonProfileAccess) {
                    openProfileActivity();
                } else if (id == R.id.ButtonLogOutAccess) {
                    FirebaseAuthAdapter.signOut();
                    Toast.makeText(ViewFamilyItemsActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    /***
     * This function redirects the current Intent to the userProfileActivity
     * and starts the next activity.
     */
    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the ViewFamilyItemsActivity
     * and starts the next activity.
     */
    public void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the FamilyMemberPageActivity
     * and starts the next activity.
     */
    public void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }

    /***
     * This function connects the functionality of a button to access the navigation bar.
     * @param item top right button on the action bar to access view
     * @return the view of the navigation bar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /***
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    private void bindViews(){

        Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonUpload = findViewById(R.id.buttonUpload);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openAccountSettingsActivity();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openNewItemUploadActivity();
            }
        });
    }

    /***
     * This function redirects the current Intent to the AccountSettingsActivity
     * and starts the next activity.
     */
    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the NewItemUploadActivity
     * and starts the next activity.
     */
    private void openNewItemUploadActivity() {
        Intent intent = new Intent(this, NewItemUploadActivity.class);
        startActivity(intent);
    }

    /***
     * This function will redirect the user to
     * the view item page of the item on the position in the adapter
     * @param position index position in the adapter
     */
    @Override
    public void onNoteClick(int position) {
        String nextItemView = itemList.get(position).getItemID();
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, nextItemView);
        startActivity(intent);
    }
}
