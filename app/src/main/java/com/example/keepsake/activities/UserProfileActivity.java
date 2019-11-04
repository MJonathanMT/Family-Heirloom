package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/***
 * This activity is our homepage activity where we would be
 * redirected most of the time after finishing an action.
 * This activity will display our profilePicture, username
 * and a list of all the items that we posted.
 */
public class UserProfileActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {
    private static final String TAG = "My Profile";
    private RecyclerView posts;
    private ItemsListAdapter itemsListAdapter;
    private List<Item> itemList;
    private ActionBarDrawerToggle drawerToggle;
    private User currentUser;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        createUserClass();
        loadItems();
        createNavBar();
        bindViews();
    }

    /***
     * This function loads all the item that the currentUser owns
     */
    private void loadItems(){
        itemList = new ArrayList<>();
        itemsListAdapter = new ItemsListAdapter(itemList, this);

        posts = findViewById(R.id.profile_page_recycleView);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(itemsListAdapter);

        OnSuccessListener listener = new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@Nullable QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() ==  DocumentChange.Type.ADDED) {
                            if (doc.getDocument().exists()){

                                Item item = doc.getDocument().toObject(Item.class);
                                item.setItemID(doc.getDocument().getId());
                                itemList.add(item);
                                itemsListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        };

        // get all the items relevant to the current user
        FirebaseItemAdapter.queryUserItemsCollection(this, listener, userID);
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
                currentUser = documentSnapshot.toObject(User.class);
                if (currentUser != null){
                    currentUser.setUserID(documentSnapshot.getId());
                }

            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
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

    /***
     * This function creates a navigation bar on the current activity you are on
     * The purpose of the navigation bar is to be able to
     * access the main pages of the application from this activity.
     */
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.userProfileDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView profileName = findViewById(R.id.user_profile_name);
                ImageView profilePicture = findViewById(R.id.user_profile_picture);
                TextView displayMessage = findViewById(R.id.user_header_welcome_message);
                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_picture);


                // Prints the name of the user session base on id of the view
                displayMessage.setText(currentUser.getFirstName()+ " " + currentUser.getLastName());
                profileName.setText(currentUser.getFirstName() +" "+ currentUser.getLastName());
                Picasso.get().load(user.getUrl()).into(profilePicture);
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);

        Toolbar toolbar = findViewById(R.id.userProfileToolbar);
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
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(UserProfileActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    openMainActivity();

                }
                return true;
            }
        });
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
     * This function redirects the current Intent to the ViewFamilyItemsActivity
     * and starts the next activity.
     */
    private void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the FamilyMemberPageActivity
     * and starts the next activity.
     */
    private void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
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
     * This function connects the functionality of a button to access the navigation bar.
     * @param item top right button on the action bar to access view
     * @return the view of the navigation bar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /***
     * This function redirects the current Intent to the AccountSettingActivity
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
     * This function redirects the current Intent to the MainActivity
     * and starts the next activity.
     */
    private void openMainActivity(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
