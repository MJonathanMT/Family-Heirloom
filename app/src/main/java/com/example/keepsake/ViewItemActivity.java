package com.example.keepsake;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewItemActivity extends AppCompatActivity {

    private User currentUser;

    private FirebaseFirestore db;
    private Button buttonEdit;
    private ImageView imageViewItemPhoto;
    private TextView textViewItemName;
    private TextView textViewItemDescription;
    private TextView textViewFamilyName;
    private TextView textViewFamilyID;
    private Button buttonViewTimeline;
    private String itemID;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //todo(naverill) ensure edit button can only be seen by users who own the item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        initialiseDB();

        createUserClass();
        createNavBar();

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");

        buttonEdit = findViewById(R.id.buttonEdit);
        imageViewItemPhoto = findViewById(R.id.imageViewItemPhoto);
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        textViewFamilyName = findViewById(R.id.textViewFamilyName);
        textViewFamilyID = findViewById(R.id.textViewFamilyID);
        buttonViewTimeline = findViewById(R.id.buttonViewTimeline);

        loadItemInfo(itemID);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemActivity(itemID);
            }
        });

        buttonViewTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewItemTimelineActivity(itemID);
            }
        });

    }

    public void loadItemInfo(final String itemID){
        DocumentReference docRef = db.collection("item")
                .document(itemID);

        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Task completed successfully
                            final DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                textViewItemName.setText(document.get("name", String.class));
                                textViewItemDescription.setText(document.get("description", String.class));
                                Picasso.get().load(document.get("url", String.class)).into(imageViewItemPhoto);

                                Point size = new Point();
                                getWindowManager().getDefaultDisplay().getSize(size);
                                float scale = (size.x / (imageViewItemPhoto.getWidth()));
                                imageViewItemPhoto.setScaleX(scale);
                                imageViewItemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                String familyID = document.get("familyID", String.class);

                                if (familyID == null){
                                    return;
                                }

                                db.collection("family_group")
                                        .document(familyID)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()){
                                                textViewFamilyName.setText(documentSnapshot.get("familyName", String.class));
                                                textViewFamilyID.setText(documentSnapshot.getId());
                                            }
                                        }
                                });

                                if (userID.compareTo(document.get("owner", String.class)) != 0){
                                   buttonEdit.setVisibility(View.GONE);
                                }

                            } else {
                                System.out.println("Failed to find doc " + itemID);
                            }
                        } else {
                            // TODO(naverill) handle exception
                            // Task failed with an exception
                            Exception exception = task.getException();
                        }
                    }
                }

        );
    }

    public final void openEditItemActivity(String itemID) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

    public final void openViewItemTimelineActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemTimelineActivity.class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

    public final void openPreviousActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.accountSettingsDrawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        db.collection("user")
                .document(userID)
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
                    Toast.makeText(ViewItemActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    private void createUserClass(){
        // create a user class for the current user
        db.collection("user")
                .document(userID)
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

    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }
}
