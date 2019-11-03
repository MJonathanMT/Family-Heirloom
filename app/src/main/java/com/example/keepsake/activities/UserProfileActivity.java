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

public class UserProfileActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {
    private static final String TAG = "My Profile";
    private RecyclerView posts;
    private ItemsListAdapter itemsListAdapter;
    private List<Item> itemList;
    private ActionBarDrawerToggle drawerToggle;
    private User currentUser;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        createUserClass();
        loadItems();
        createNavBar();
        bindViews();
    }

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

    @Override
    public void onNoteClick(int position) {
        String nextItemView = itemList.get(position).getItemID();
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, nextItemView);
        startActivity(intent);
    }

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

    private void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    private void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

    private void openNewItemUploadActivity() {
        Intent intent = new Intent(this, NewItemUploadActivity.class);
        startActivity(intent);
    }

    private void openMainActivity(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
