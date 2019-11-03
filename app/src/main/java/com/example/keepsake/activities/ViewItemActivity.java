package com.example.keepsake.activities;

import android.content.Intent;
import android.graphics.Point;
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
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Family;
import com.example.keepsake.database.firebaseSnapshot.Item;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewItemActivity extends AppCompatActivity {
    private final String TAG = "View Item";

    private User currentUser;

    private Button buttonEdit;
    private ImageView imageViewItemPhoto;
    private TextView textViewItemName;
    private TextView textViewItemDescription;
    private TextView textViewFamilyName;
    private TextView textViewFamilyID;
    private Button buttonViewTimeline;
    private String itemID;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();

    //todo(naverill) ensure edit button can only be seen by users who own the item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        Intent intent = getIntent();
        itemID = intent.getStringExtra(FirebaseItemAdapter.ID_FIELD);

        createUserClass();
        createNavBar();
        bindViews();
        loadItemInfo(itemID);

    }

    public void loadItemInfo(final String itemID){
        OnSuccessListener itemListener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()){
                    Item item = document.toObject(Item.class);
                    textViewItemName.setText(item.getName());
                    textViewItemDescription.setText(item.getDescription());
                    Picasso.get().load(item.getUrl()).into(imageViewItemPhoto);

                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);
                    float scale = (size.x / (imageViewItemPhoto.getWidth()));
                    imageViewItemPhoto.setScaleX(scale);
                    imageViewItemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    String familyID = item.getFamilyID();

                    if (familyID == null){
                        return;
                    }

                    OnSuccessListener familyGroupListener = new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                Family family = documentSnapshot.toObject(Family.class);
                                textViewFamilyName.setText(family.getFamilyName());
                                textViewFamilyID.setText(documentSnapshot.getId());
                            }
                        }
                    };

                    FirebaseFamilyAdapter.getDocument(ViewItemActivity.this, familyID, familyGroupListener);

                    if (userID.compareTo(document.get(FirebaseItemAdapter.OWNER_ID_FIELD, String.class)) != 0){
                        buttonEdit.setVisibility(View.GONE);
                    }
            }
        }};

        FirebaseItemAdapter.getDocument(this, itemID, itemListener);
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

    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.viewItemDrawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

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
                    FirebaseAuthAdapter.signOut();
                    Toast.makeText(ViewItemActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    public void bindViews(){
        buttonEdit = findViewById(R.id.buttonEdit);
        imageViewItemPhoto = findViewById(R.id.imageViewItemPhoto);
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        textViewFamilyName = findViewById(R.id.textViewFamilyName);
        textViewFamilyID = findViewById(R.id.textViewFamilyID);
        buttonViewTimeline = findViewById(R.id.buttonViewTimeline);

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

    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
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


    public final void openEditItemActivity(String itemID) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
        startActivity(intent);
    }

    public final void openViewItemTimelineActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemTimelineActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
        startActivity(intent);
    }

}
