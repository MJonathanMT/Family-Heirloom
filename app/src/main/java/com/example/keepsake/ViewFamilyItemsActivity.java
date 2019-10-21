package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ViewFamilyItemsActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {

    private ActionBarDrawerToggle drawerToggle;
    private FirebaseFirestore db;
    private String userId;
    private List<Item> itemList;
    private ItemsListAdapter itemsListAdapter;
    private RecyclerView posts;
    private User currentUser;
    private ArrayList<String> userFamilyIDList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_family_items);
        db = FirebaseFirestore.getInstance();

        getUserId();
        createUserClass();

//        FirebaseFirestore.getInstance()
//                .collection("user")
//                .document(currentUser.getUUID())
//                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                User user = documentSnapshot.toObject(User.class);
//                TextView displayName = findViewById(R.id.user_header_welcome_message);
//                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_image);
//
//                // Prints the name of the user session base on id of the view
//                displayName.setText("Welcome "+currentUser.getFirstName() +" "+ currentUser.getLastName());
//                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
//            }
//        });
        createFamilyItemView();
        createNavBar();
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
                            currentUser.setUUID(documentSnapshot.getId());

                            if (!currentUser.getUUID().isEmpty()){
                                loadFamilyItemViews();
                            }
                        }

                    }
                });
    }

    private void createFamilyItemView(){
        itemList = new ArrayList<>();
        itemsListAdapter = new ItemsListAdapter(itemList, this);

        posts = findViewById(R.id.main_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(itemsListAdapter);
    }

    private void loadFamilyItemViews(){
        // get all the items relevant to the current user
        if(currentUser.getUserSession() != null) {
            db.collection("user")
                    .document(userId)
                    .collection("familyGroups")
                    .document(currentUser.getUserSession())
                    .get()
                    .addOnSuccessListener(
                            new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        String acceptedString = documentSnapshot.get("accepted", String.class);

                                        if (acceptedString.compareTo("1") == 0){
                                            loadItems();
                                        }
                                    }
                                }
                            }
                    );
        }
    }

    private void loadItems(){
        db.collection("item")
                .whereEqualTo("familyID", currentUser.getUserSession())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("ERROR", "Error: " + e.getMessage());
                        }
                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    if (doc.getDocument().exists()){
                                        Item item = doc.getDocument().toObject(Item.class);
                                        item.setItemId(doc.getDocument().getId());

                                        // temporary guard until database is cleaned Up
                                        if (item.getFamilyId() == null || item.getOwner() == null) {
                                            continue;
                                        }

                                        if (userId.compareTo(item.getOwner()) == 0){
                                            itemList.add(item);
                                            itemsListAdapter.notifyDataSetChanged();
                                        }

                                        else if (currentUser.getUserSession().compareTo(item.getFamilyId()) == 0){
                                            if(item.getPrivacy().compareTo("O") != 0){
                                                itemList.add(item);
                                                itemsListAdapter.notifyDataSetChanged();
                                            }
                                        }

                                    }
                                }
                            }
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

    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.itemsDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Item");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonHomepageAccess) {
                    openHomePageActivity();
                } else if (id == R.id.ButtonFamilyItemsAccess) {
                    openViewFamilyItemsActivity();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openFamilyMemberPageActivity();
                } else if (id == R.id.ButtonProfileAccess) {
                    openProfileActivity();
                } else if (id == R.id.ButtonLogOutAccess) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(ViewFamilyItemsActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onNoteClick(int position) {
        String nextItemView = itemList.get(position).getItemId();
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", nextItemView);
        startActivity(intent);
    }
}
