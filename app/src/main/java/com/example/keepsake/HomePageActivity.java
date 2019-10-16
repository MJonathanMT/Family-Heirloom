package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class HomePageActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    private static final String TAG = "FireLog";
    private RecyclerView posts;
    private FirebaseFirestore db;
    private ItemsListAdapter itemsListAdapter;
    private List<Item> itemList;

    private String userId;
    private ArrayList<String> userFamilyNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_home_page);
        db = FirebaseFirestore.getInstance();

        getUserId();
        createFamilyList();
        homeItemViewing();
        manageButtons();
        createNavBar();
    }

    private void getUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    private void createFamilyList(){
        // Get the list of family that the current user is in
        db.collection("user").document(userId).collection("familyNames").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() ==  DocumentChange.Type.ADDED) {
                            QueryDocumentSnapshot data = doc.getDocument();
                            userFamilyNameList.add((String) data.get("familyName"));
                        }
                    }
                }
            }
        });
    }

    private void homeItemViewing(){
        itemList = new ArrayList<>();
        itemsListAdapter = new ItemsListAdapter(itemList);
        itemsListAdapter.enableOnClickBehaviour();

        posts = findViewById(R.id.main_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(itemsListAdapter);

        // get all the items relevant to the current user
        db.collection("item").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if(queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() ==  DocumentChange.Type.ADDED) {
                            if (doc.getDocument().exists()) {

                                Item item = doc.getDocument().toObject(Item.class);

                                if (item.getOwner() != null && userId.compareTo(item.getOwner()) == 0) {
                                    item.setUUID(doc.getDocument().getId());
                                    itemList.add(item);
                                    itemsListAdapter.notifyDataSetChanged();

                                } else if (item.getFamilyName() != null && userFamilyNameList.contains(item.getFamilyName())) {
                                    //                            if (item.privacy.compareTo("O") != 0 || item.privacy.compareTo("family") == 0) {
                                    item.setUUID(doc.getDocument().getId());
                                    itemList.add(item);
                                    itemsListAdapter.notifyDataSetChanged();
                                    //                            }
                                }
                            }
                        }
                    }

                }

            }
        });
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

    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.homeDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
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
                }
                return true;
            }
        });
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

}
