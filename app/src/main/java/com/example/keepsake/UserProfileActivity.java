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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UserProfileActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private static final String TAG = "FireLog";
    private RecyclerView posts;
    private FirebaseFirestore db;
    private ItemsListAdapter itemsListAdapter;
    private List<Item> itemList;
    private ActionBarDrawerToggle drawerToggle;
    private User currentUser;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();

        getUserId();
        createUserClass();
        loadItems();
        createNavBar();
        manageButtons();
    }

    private void loadItems(){
        itemList = new ArrayList<>();
        itemsListAdapter = new ItemsListAdapter(itemList, this);

        posts = findViewById(R.id.profile_page_recycleView);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(itemsListAdapter);

        // get all the items relevant to the current user
        db.collection("item")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.d(TAG, "Error: " + e.getMessage());
                        }

                        if (queryDocumentSnapshots != null){
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() ==  DocumentChange.Type.ADDED) {
                                    if (doc.getDocument().exists()){

                                        Item item = doc.getDocument().toObject(Item.class);
                                        item.setItemID(doc.getDocument().getId());
                                        Log.d("Item owner", " " + item.getOwner());
                                        Log.d("User ID", " " + userId);
                                        if(item.getOwner() != null && userId.compareTo(item.getOwner()) == 0) {
                                            itemList.add(item);
                                            itemsListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
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

    private void getUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    @Override
    public void onNoteClick(int position) {
        String nextItemView = itemList.get(position).getItemID();
        Log.d("this item is:", nextItemView);
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", nextItemView);
        startActivity(intent);
    }
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.userProfileDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        db.collection("user")
                .document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        });

        Toolbar toolbar = findViewById(R.id.userProfileToolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
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
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    private void openHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
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
    private void manageButtons(){

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
    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

    private void openNewItemUploadActivity() {
        Intent intent = new Intent(this, NewItemUploadActivity.class);
        startActivity(intent);
    }
}
