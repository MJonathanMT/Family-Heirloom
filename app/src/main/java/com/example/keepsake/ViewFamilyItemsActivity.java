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
import com.google.firebase.firestore.DocumentReference;
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
    private FirebaseFirestore fbfs;
    private String userId;
    private List<Item> itemList;
    private ItemsListAdapter itemsListAdapter;
    private RecyclerView posts;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_family_items);
        fbfs = FirebaseFirestore.getInstance();

        getUserId();
        createUserClass();
        createFamilyItemView();
        createNavBar();
    }

    private void createUserClass(){
        // create a user class for the current user
        DocumentReference docUser = fbfs.collection("user").document(userId);
        docUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                Log.d("current user session",currentUser.getUserSession());
                familyItemViewingUpdate();
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

    private void familyItemViewingUpdate(){
        // get all the items relevant to the current user
        fbfs.collection("item").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", "Error: " + e.getMessage());
                }
                if(currentUser.getUserSession()!=null) {
                    assert queryDocumentSnapshots != null;
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Item item = doc.getDocument().toObject(Item.class);
                            item.setItemId(doc.getDocument().getId());

                            // temporary guard until database is cleaned Up
                            Log.d("current user sess", currentUser.getUserSession());
                            if(item.getFamilyId() == null){
                                continue;
                            }
                            Log.d("famiyl name", item.getFamilyId());
                            if (item.getFamilyId().compareTo(currentUser.getUserSession()) == 0) {
                                itemList.add(item);
                                itemsListAdapter.notifyDataSetChanged();
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
        Toast.makeText(ViewFamilyItemsActivity.this, userId, Toast.LENGTH_SHORT).show();
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
                }
                else  if (id == R.id.ButtonFamilyItemsAccess){
                    openViewFamilyItemsActivity();
                }
                else  if (id == R.id.ButtonFamilyMembersAccess){
                    openFamilyMemberPageActivity();
                }
                return true;
            }
        });

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
