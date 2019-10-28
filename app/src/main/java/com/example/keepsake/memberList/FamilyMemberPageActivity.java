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

import com.example.keepsake.AccountSettingsActivity;
import com.example.keepsake.HomePageActivity;
import com.example.keepsake.MainActivity;
import com.example.keepsake.R;
import com.example.keepsake.User;
import com.example.keepsake.UserProfileActivity;
import com.example.keepsake.ViewFamilyItemsActivity;
import com.example.keepsake.memberRequest.MemberRequestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private Button buttonMemberRequestPage;
    private RecyclerView posts;
    private User currentUser;
    private String currentFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_page);
        db = FirebaseFirestore.getInstance();

        manageButtons();
        createNavBar();
        getUserId();
        createUserClass();

//        FirebaseFirestore.getInstance()
//                .collection("user")
//                .document(Objects.requireNonNull(currentUser).getUUID())
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
        createMemberView();

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
                        checkAdmin(currentFamilyId);
                    }

                }
            }
        });
    }

    private void checkAdmin(String familyID){
        db.collection("family_group")
                .document(familyID)
                .collection("admin")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                buttonMemberRequestPage.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }

    private void memberViewUpdate(){
        final ArrayList<String> acceptedFamilyGroups = new ArrayList<>();
        // get all the items relevant to the current user


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
        buttonMemberRequestPage = findViewById(R.id.memberRequestPage);

        buttonMemberRequestPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMemberRequestActivity();
            }
        });
        Button buttonSettings = findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openAccountSettingsActivity();
            }
        });
    }


    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
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

                if (id == R.id.ButtonFamilyItemsAccess) {
                    openViewFamilyItemsActivity();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openFamilyMemberPageActivity();
                } else if (id == R.id.ButtonProfileAccess) {
                    openProfileActivity();
                } else if (id == R.id.ButtonLogOutAccess) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(FamilyMemberPageActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
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

    public void openMemberRequestActivity(){
        Intent intent = new Intent(this, MemberRequestActivity.class);
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
}

