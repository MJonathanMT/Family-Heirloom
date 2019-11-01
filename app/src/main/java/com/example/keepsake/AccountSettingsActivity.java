package com.example.keepsake;

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

import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AccountSettingsActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseFirestore db;
    private User currentUser;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        db = FirebaseFirestore.getInstance();

        getUserId();
        createUserClass();
        createNavBar();
        manageButtons();
    }
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.accountSettingsDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        db.collection("user")
                .document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView displayMessage = findViewById(R.id.user_header_welcome_message);
                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_picture);

                // Prints the name of the user session base on id of the view
                displayMessage.setText(currentUser.getFirstName()+ " " + currentUser.getLastName());
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void getUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    private void createUserClass(){
        // create a user class for the current user
        db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null){
                            currentUser.setUserID(documentSnapshot.getId());
                            Log.d("Current Id", documentSnapshot.getId());
                        }

                    }
                });
    }




    private void manageButtons(){
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

    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }
    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
    public void openHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
    public void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }
    public void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }
    public void openUpdateDetailsActivity(){
        Intent intent = new Intent(this, UpdateDetailsActivity.class);
        startActivity(intent);
    }
    public void openFamilySetupActivity(){
        Intent intent = new Intent(this, FamilySetupActivity.class);
        startActivity(intent);
    }
    public void openChangeFamilyActivity(){
        Intent intent = new Intent(this, ChangeFamilyActivity.class);
        startActivity(intent);
    }
}
