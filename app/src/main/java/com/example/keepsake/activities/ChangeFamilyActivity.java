package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Family;
import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
/***
 * This activity page is the page in the application
 * where you can change the current user's userSession.
 * the currentUser's userSession will change
 * what you will be able to see in the ViewFamilyItemActivity
 */
public class ChangeFamilyActivity extends AppCompatActivity {
    private final String TAG = "Change Family";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User currentUser;

    private Spinner spinnerFamilyGroup;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();
    private String familyID;
    private Button buttonChange;
    private ActionBarDrawerToggle drawerToggle;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_family);

        bindViews();
        createUserClass();
        createNavBar();
        populateFamilyGroupSpinner();
    }

    /***
     * This function connects the data from fireStore to the application spinner.
     * It takes in the current user's family groups and populates the familyGroupSpinner data
     */
    public void populateFamilyGroupSpinner(){
        final ArrayList<Family> familyList = new ArrayList<>();

        OnSuccessListener listener = new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot docRef : queryDocumentSnapshots){
                    String familyID = docRef.getId();

                    OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Family family = documentSnapshot.toObject(Family.class);
                            family.setFamilyID((documentSnapshot.getId()));
                            familyList.add(family);

                            if (familyList.size() == 1){
                                createFamilySpinner(familyList);
                            }
                        }
                    };

                    FirebaseFamilyAdapter.getDocument(ChangeFamilyActivity.this, familyID, listener);
                }
            }
        };

        FirebaseUserAdapter.getAcceptedFamilies(this, userID, listener);

    }

    /***
     * This function creates the spinner adapter for the application.
     * It connects the data produced by the populateFamilyGroupSpinner to the spinner in the xml page.
     * @param familyList data produced by the populateFamilyGroupSpinner function
     */
    public void createFamilySpinner(ArrayList<Family> familyList){

        ArrayAdapter<Family> familyAdapter = new ArrayAdapter<Family>(this, R.layout.family_list_layout, familyList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            public View getCustomView(int position, View convertView, ViewGroup parent){
                View spinner = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_list_layout, parent, false);
                TextView familyName = spinner.findViewById(R.id.textViewFamilyName);
                TextView familyID = spinner.findViewById(R.id.textViewFamilyID);
                TextView buttonJoin = spinner.findViewById(R.id.buttonJoin);

                buttonJoin.setVisibility(View.GONE);

                familyName.setText(getItem(position).getFamilyName());
                familyID.setText(getItem(position).getFamilyID());
                spinner.setScaleX((float)0.75);
                spinner.setScaleY((float)0.75);
                return spinner;
            }

            @Override
            public int getPosition(Family item) {
                int i;
                for (i=0; i < getCount(); i++){
                    if (getItem(i).getFamilyID().compareTo(item.getFamilyID()) == 0){
                        return i;
                    }
                }
                return -1;
            }
        };

        familyAdapter.setDropDownViewResource(R.layout.family_list_layout);

        spinnerFamilyGroup.setAdapter(familyAdapter);
        spinnerFamilyGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              String selectedFamilyID = ((Family) spinnerFamilyGroup.getSelectedItem()).getFamilyID();
              setFamilyID(selectedFamilyID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setCurrentUserSession();
    }

    private void setCurrentUserSession(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String userSession = documentSnapshot.get(FirebaseUserAdapter.USER_SESSION_FIELD, String.class);

                    int position = ((ArrayAdapter)spinnerFamilyGroup.getAdapter()).getPosition(new Family("", userSession));

                    spinnerFamilyGroup.setSelection(position, true);
                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    /***
     * Set the this activity's familyID to the new familyID
     * @param familyID the new familyID
     */
    private void setFamilyID(String familyID){
        this.familyID = familyID;
    }

    /***
     * This function updates the current user's
     * userSession to the selected familyID within the spinner.
     */
    public void updateUserSession(){
        int index = ((ArrayAdapter) spinnerFamilyGroup.getAdapter()).getPosition(new Family("", familyID));
        spinnerFamilyGroup.setSelection(index, true);

        if (!familyID.isEmpty()){
            OnSuccessListener listener = new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            openMyProfileActivity();
                        }
                    };
                }
            };

            FirebaseUserAdapter.updateDocument(this, userID, listener, FirebaseUserAdapter.USER_SESSION_FIELD, familyID);
        }

        openMyProfileActivity();

    }

    /***
     * This function redirects the current Intent to the userProfileActivity
     * and starts the next activity.
     */
    public void openMyProfileActivity(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    /***
     * This function creates a navigation bar on the current activity you are on
     * The purpose of the navigation bar is to be able to
     * access the main pages of the application from this activity.
     */
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.changeFamilyDrawerLayout);
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
                    Toast.makeText(ChangeFamilyActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    /***
     * This function creates a user class that could be accessed in this activity.
     * It pulls data off the currentUser that is logged-in
     * and creates an instance of a User class of the currentUser
     */
    private void createUserClass(){
        // create a user class for the current user
        OnSuccessListener listener  = new OnSuccessListener<DocumentSnapshot>() {
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
        spinnerFamilyGroup = findViewById(R.id.changeCurrentFamilySpinner);

        buttonChange = findViewById(R.id.buttonChange);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUserSession();
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
     * This function connects the functionality of a button to access the navigation bar.
     * @param item top right button on the action bar to access view
     * @return the view of the navigation bar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
     * This function redirects the current Intent to the AccountSettingsActivity
     * and starts the next activity.
     */
    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }
}
