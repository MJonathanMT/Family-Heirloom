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

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/***
 * This activity page is the page in the application
 * where you can view 3 possible settings pages on the account.
 * It could redirect you to "ChangeFamilyActivity", "FamilySetupActivity"
 * and "UpdateDetailsActivity"
 */
public class AccountSettingsActivity extends AppCompatActivity {
    private final String TAG = "Settings";
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
        setContentView(R.layout.activity_account_settings);
        createUserClass();
        createNavBar();
        bindViews();
    }

    /***
     * This function creates a navigation bar on the current activity you are on
     * The purpose of the navigation bar is to be able to
     * access the main pages of the application from this activity.
     */
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.accountSettingsDrawerLayout);
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
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(AccountSettingsActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
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
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    private void bindViews(){
        Button buttonUpdateDetails = findViewById(R.id.buttonUpdateDetails);
        Button buttonChangeFamily = findViewById(R.id.buttonChangeCurrentFamily);
        Button buttonAddFamily = findViewById(R.id.buttonAddFamily);

        buttonUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDetailsActivity();
            }
        });

        buttonChangeFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeFamilyActivity();
            }
        });


        buttonAddFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFamilySetupActivity();
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
     * This function redirects the current Intent to the UpdateDetailsActivity
     * and starts the next activity.
     */
    public void openUpdateDetailsActivity(){
        Intent intent = new Intent(this, UpdateDetailsActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the FamilySetupActivity
     * and starts the next activity.
     */
    public void openFamilySetupActivity(){
        Intent intent = new Intent(this, FamilySetupActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the ChangeFamilyActivity
     * and starts the next activity.
     */
    public void openChangeFamilyActivity(){
        Intent intent = new Intent(this, ChangeFamilyActivity.class);
        startActivity(intent);
    }
}
