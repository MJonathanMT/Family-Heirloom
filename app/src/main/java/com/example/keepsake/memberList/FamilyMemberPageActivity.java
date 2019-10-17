package com.example.keepsake.memberList;

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

import com.example.keepsake.HomePageActivity;
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
    private FirebaseFirestore fbfs;
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
        fbfs = FirebaseFirestore.getInstance();

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
        DocumentReference docUser = fbfs.collection("user").document(userId);
        docUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);


                assert currentUser != null;
                currentFamilyId = currentUser.getUserSession();
                memberViewUpdate();
//                Log.d("OH YEA", currentFamilyId);
//                memberRequestViewUpdate();
//                familyItemViewingUpdate();
            }
        });
    }

    private void memberViewUpdate(){
        // get all the items relevant to the current user
        fbfs.collection("family_group").document(currentFamilyId).collection("members").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", "Error: " + e.getMessage());
                }

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        User user = doc.getDocument().toObject(User.class);
                        Log.d("member's name", user.getFirstName());
                        userList.add(user);
                        userListAdapter.notifyDataSetChanged();

                    }
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

