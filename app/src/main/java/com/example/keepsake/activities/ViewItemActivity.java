package com.example.keepsake.activities;

import android.content.Intent;
import android.graphics.Point;
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
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Family;
import com.example.keepsake.database.firebaseSnapshot.Item;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ViewItemActivity extends AppCompatActivity {
    private final String TAG = "View Item";

    private User currentUser;

    private Button buttonEdit;
    private ImageView imageViewItemPhoto;
    private TextView textViewItemName;
    private TextView textViewItemDescription;
    private TextView textViewFamilyName;
    private TextView textViewFamilyID;
    private Button buttonViewTimeline;
    private String itemID;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();

    /**
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        Intent intent = getIntent();
        itemID = intent.getStringExtra(FirebaseItemAdapter.ID_FIELD);

        createUserClass();
        createNavBar();
        bindViews();
        loadItemInfo(itemID);

    }

    /**
     * Loads the information of an item
     * @param itemID the ID of an item
     */
    public void loadItemInfo(final String itemID){
        OnSuccessListener itemListener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                // Checks if document already exist in database
                if (document.exists()){
                    Item item = document.toObject(Item.class);
                    textViewItemName.setText(item.getName());
                    textViewItemDescription.setText(item.getDescription());
                    Picasso.get().load(item.getUrl()).into(imageViewItemPhoto);

                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);
                    float scale = (size.x / (imageViewItemPhoto.getWidth()));
                    imageViewItemPhoto.setScaleX(scale);
                    imageViewItemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    String familyID = item.getFamilyID();

                    if (familyID == null){
                        return;
                    }

                    OnSuccessListener familyGroupListener = new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                Family family = documentSnapshot.toObject(Family.class);
                                textViewFamilyName.setText(family.getFamilyName());
                                textViewFamilyID.setText(documentSnapshot.getId());
                            }
                        }
                    };

                    FirebaseFamilyAdapter.getDocument(ViewItemActivity.this, familyID, familyGroupListener);

                    if (userID.compareTo(document.get(FirebaseItemAdapter.OWNER_ID_FIELD, String.class)) != 0){
                        buttonEdit.setVisibility(View.GONE);
                    }
            }
        }};

        FirebaseItemAdapter.getDocument(this, itemID, itemListener);
    }

    /**
     * This function creates a user class that could be accessed in this activity.
     * It pulls data off the currentUser that is logged-in
     * and creates an instance of a User class of the currentUser
     */
    private void createUserClass(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                // Checks if current user is null
                if (currentUser != null){
                    currentUser.setUserID(documentSnapshot.getId());
                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    /**
     * This function creates a navigation bar on the current activity you are on
     * The purpose of the navigation bar is to be able to
     * access the main pages of the application from this activity.
     */
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.viewItemDrawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                // Checks if current user is null
                if (currentUser != null){
                    currentUser.setUserID(documentSnapshot.getId());
                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);

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
                    FirebaseAuthAdapter.signOut();
                    Toast.makeText(ViewItemActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                return true;
            }
        });
    }

    /**
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        buttonEdit = findViewById(R.id.buttonEdit);
        imageViewItemPhoto = findViewById(R.id.imageViewItemPhoto);
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        textViewFamilyName = findViewById(R.id.textViewFamilyName);
        textViewFamilyID = findViewById(R.id.textViewFamilyID);
        buttonViewTimeline = findViewById(R.id.buttonViewTimeline);

        // Edit button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemActivity(itemID);
            }
        });

        // View timeline button
        buttonViewTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewItemTimelineActivity(itemID);
            }
        });
    }

    /**
     * This function redirects the current Intent to the UserProfileActivity
     * and starts the next activity.
     */
    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    /**
     * This function redirects the current Intent to the ViewFamilyItemsActivity
     * and starts the next activity.
     */
    public void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    /**
     * This function redirects the current Intent to the FamilyMemberPageActivity
     * and starts the next activity.
     */
    public void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }

    /**
     * This function redirects the current Intent to the EditItemActivity
     * and starts the next activity.
     */
    public final void openEditItemActivity(String itemID) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
        startActivity(intent);
    }

    /**
     * This function redirects the current Intent to the ViewItemTimelineActivity
     * and starts the next activity.
     */
    public final void openViewItemTimelineActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemTimelineActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
        startActivity(intent);
    }
}