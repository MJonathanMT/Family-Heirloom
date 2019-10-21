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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class HomePageActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {

    private ActionBarDrawerToggle drawerToggle;
    private static final String TAG = "FireLog";
    private RecyclerView posts;
    private FirebaseFirestore db;
    private ItemsListAdapter itemsListAdapter;
    private List<Item> itemList;
    private User currentUser;

    private String userId;
    private ArrayList<String> userFamilyNameList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<String> userFamilyIDList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_home_page);
        db = FirebaseFirestore.getInstance();

        getUserId();
        createUserClass();
//
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

        createFamilyList();
        loadItemViews();
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

    private void createFamilyList(){
        // Get the list of family that the current user is in
        db.collection("user")
                .document(userId)
                .collection("familyGroups")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                        if (queryDocumentSnapshots != null){
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                if (doc.getType() ==  DocumentChange.Type.ADDED) {

                                    QueryDocumentSnapshot data = doc.getDocument();
                                    String familyID = data.getId();
                                    String accepted = data.get("accepted", String.class);

                                    //todo(naverill) add 'accepted' field to user.familyGroups
                                    if (accepted != null){
                                        if (accepted.compareTo("1") == 0) {
                                            userFamilyIDList.add(familyID);
                                        }
                                    }
                                }
                            }
                        }
                    }
        });
    }

    private void loadItemViews(){
        itemList = new ArrayList<>();
        itemsListAdapter = new ItemsListAdapter(itemList,this);

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
                if (queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() ==  DocumentChange.Type.ADDED) {
                            Item item = doc.getDocument().toObject(Item.class);
                            item.setItemId(doc.getDocument().getId());
                            if(userId.compareTo(item.getOwner()) == 0) {
                                itemList.add(item);
                                itemsListAdapter.notifyDataSetChanged();
                            }
                            else if(userFamilyIDList.contains(item.getFamilyId())) {
                                if (item.getPrivacy().compareTo("O") != 0) {
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



    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.homeDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Timeline");
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
                    Toast.makeText(HomePageActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
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

    private void userLogOut() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNoteClick(int position) {
        String nextItemView = itemList.get(position).getItemId();
        Log.d("this item is:", nextItemView);
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", nextItemView);
        startActivity(intent);
    }
}
