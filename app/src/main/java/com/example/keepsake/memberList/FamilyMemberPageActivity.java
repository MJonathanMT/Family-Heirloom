package com.example.keepsake.memberList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.Family;
import com.example.keepsake.HomePageActivity;
import com.example.keepsake.NewItemUploadActivity;
import com.example.keepsake.R;
import com.example.keepsake.User;
import com.example.keepsake.ViewFamilyItemsActivity;
import com.example.keepsake.memberRequest.MemberRequestActivity;
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

public class FamilyMemberPageActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    private FirebaseFirestore db;
    private String userId;
    private List<User> userList;
    private UserListAdapter userListAdapter;
    private RecyclerView posts;
    private User currentUser;
    private String currentFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_page);
        db = FirebaseFirestore.getInstance();

        getUserId();
        createUserClass();
        createMemberView();


        manageButtons();
        createNavBar();
    }

    private void createMemberView(){
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userList);

        posts = findViewById(R.id.member_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(userListAdapter);
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
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    currentUser = documentSnapshot.toObject(User.class);
                    currentUser.setUUID(userId);
                    currentFamilyId = currentUser.getUserSession();

                    if(currentFamilyId!=null){
                        memberViewUpdate();
                    }
//                Log.d("OH YEA", currentFamilyId);
//                memberRequestViewUpdate();
//                familyItemViewingUpdate();

                }
            }
        });
    }

    private void memberViewUpdate(){
        final ArrayList<String> acceptedFamilyGroups = new ArrayList<>();
        // get all the items relevant to the current user
        Log.d("Current user: ", currentUser.getUUID());
        Log.d("Current user: ", currentFamilyId);
        db.collection("user")
                .document(currentUser.getUUID())
                .collection("familyGroups")
                .document(currentFamilyId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String accepted = documentSnapshot.get("accepted", String.class);
                            if (accepted.compareTo("1") ==  0){
                                loadMembers();

                            }
                        }
                    }
                });
        Log.d("Accepted group size: ", String.valueOf(acceptedFamilyGroups.size()));
    }

    public void loadMembers(){
        db.collection("family_group")
                .document(currentFamilyId)
                .collection("members")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("ERROR", "Error: " + e.getMessage());
                        }

                        if (queryDocumentSnapshots != null){
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    if (doc.getDocument().exists()){
                                        addUserToView(doc.getDocument().getId());
                                    }
                                }
                            }
                        }


                    }
                });
    }

    public void addUserToView(String userID){
        final DocumentReference userRef = db.collection("user")
                .document(userID);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            userList.add(user);
                            userListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void manageButtons(){

        Button buttonMemberRequestPage = findViewById(R.id.memberRequestPage);

        buttonMemberRequestPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openActivity();
            }
        });
    }

    public void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.familyMembersLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Family Members");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonHomepageAccess) {
                    openActivity3();
                } else if (id == R.id.ButtonFamilyItemsAccess) {
                    openActivity4();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openActivity5();
                }
                return true;
            }
        });
    }
    public void openActivity(){
        Intent intent = new Intent(this, MemberRequestActivity.class);
        startActivity(intent);
    }
    public void openActivity3() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void openActivity4() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    public void openActivity5() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}

