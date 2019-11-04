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
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.example.keepsake.utils.adapter.UserListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
 * This activity page views all the
 * members of the current userSession the user is in
 * You would be able to search who is in the family group through the search bar
 * If you're an admin of the group would be able to manage the
 * joinRequests of the familyGroup or invite people to the group.
 */
public class FamilyMemberPageActivity extends AppCompatActivity {
    private final String TAG = "Family Members";

    private ActionBarDrawerToggle drawerToggle;

    private String userID = FirebaseAuthAdapter.getCurrentUserID();
    private List<User> userList;
    private UserListAdapter userListAdapter;
    private Button buttonMemberRequestPage;
    private RecyclerView posts;
    private User user;
    private String familyID;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_page);

        bingViews();
        createUserClass();
        createNavBar();
        createMemberView();

    }

    /***
     * This function creates an empty arrayList for
     * all the users to be filled in and also connects
     * the member_list to the posts
     */
    private void createMemberView(){
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userList);

        posts = findViewById(R.id.member_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(userListAdapter);
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
                    user = documentSnapshot.toObject(User.class);
                    user.setUserID(userID);
                    familyID = user.getUserSession();

                    if(familyID!=null){
                        memberViewUpdate();
                        checkAdmin(familyID);
                    }

                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    /***
     * This function checks if the current user is the admin of the current familyGroup(familyID)
     * @param familyID the current familyGroup the user is in
     */
    private void checkAdmin(String familyID){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    buttonMemberRequestPage.setVisibility(View.INVISIBLE);
                }
            }
        };

        FirebaseFamilyAdapter.getAdminDocument(this, familyID, userID, listener);
    }

    /***
     * This function updates the changes made on the memberList
     * by calling the loadMembers function.
     */
    private void memberViewUpdate(){
        final ArrayList<String> acceptedFamilyGroups = new ArrayList<>();
        // get all the items relevant to the current user

        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String accepted = documentSnapshot.get(FirebaseUserAdapter.ACCEPTED_FIELD, String.class);
                    if (accepted.compareTo("1") ==  0){
                        loadMembers();

                    }
                }
            }
        };

        FirebaseUserAdapter.getFamilyDocument(this, user.getUserID(), familyID, listener);
    }

    /***
     * This function loads all the members of the current familyGroup
     * and calling addUserToView to load the view
     */
    public void loadMembers(){
        EventListener<QuerySnapshot> listener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", "Error: " + e.getMessage());
                }

                if (queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            if (doc.getDocument().exists()){
                                addUserToView(doc.getDocument().getId());
                            }
                        }
                    }
                }


            }
        };

        FirebaseFamilyAdapter.getMembersCollection(this, familyID, listener);
    }

    /***
     * Adds the userID to the view of this activity.
     * @param userID userID of a user
     */
    public void addUserToView(String userID){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    userList.add(user);
                    userListAdapter.notifyDataSetChanged();
                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    /***
     * This function creates a navigation bar on the current activity you are on
     * The purpose of the navigation bar is to be able to
     * access the main pages of the application from this activity.
     */
    public void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.familyMembersLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView displayMessage = findViewById(R.id.user_header_welcome_message);
                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_picture);

                // Prints the name of the user session base on id of the view
                displayMessage.setText(user.getFirstName()+ " " + user.getLastName());
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


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
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(FamilyMemberPageActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                return true;
            }
        });
    }

    /***
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    private void bingViews(){
        buttonMemberRequestPage = findViewById(R.id.memberRequestPage);

        buttonMemberRequestPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMemberRequestActivity();
            }
        });
        Button buttonSettings = findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openAccountSettingsActivity();
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
     * This function redirects the current Intent to the userProfileActivity
     * and starts the next activity.
     */
    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the MemberRequestActivity
     * and starts the next activity.
     */
    public void openMemberRequestActivity(){
        Intent intent = new Intent(this, MemberRequestActivity.class);
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
}

