package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChangeFamilyActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String userId;
    private ArrayList<String> userFamilyNameList = new ArrayList<>();
    private ArrayList<String> userFamilyIdList = new ArrayList<>();
    private int position;
    private Spinner spinnerFamilyGroup;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String familyID;
    private Button buttonChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_family);
        db = FirebaseFirestore.getInstance();

        spinnerFamilyGroup = findViewById(R.id.changeCurrentFamilySpinner);

        buttonChange = findViewById(R.id.buttonChange);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserSession();
            }
        });

        getUserId();
        createNavBar();
        populateFamilyGroupSpinner();
    }

    public void populateFamilyGroupSpinner(){
        Query query = db.collection("user")
                .document(userID)
                .collection("familyGroups")
                .whereEqualTo("accepted", "1");

        final ArrayList<Family> familyList = new ArrayList<>();

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot docRef : queryDocumentSnapshots){
                    String familyID = docRef.getId();

                    db.collection("family_group")
                            .document(familyID)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Family family = new Family();
                                    family.setFamilyName(documentSnapshot.get("familyName", String.class));
                                    family.setUUID((documentSnapshot.getId()));
                                    familyList.add(family);

                                    if (familyList.size() == 1){
                                        createFamilySpinner(familyList);
                                    }
                                }
                            });
                }
            }
        });

    }

    public void createFamilySpinner(ArrayList<Family> familyList){
        Log.d("LIST", " "+ String.valueOf(familyList.size()));
        ArrayAdapter<Family> familyAdapter = new ArrayAdapter<Family>(this, R.layout.family_list_layout, familyList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            public View getCustomView(int position, View convertView, ViewGroup parent){
                View spinner = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_list_layout, parent, false);
                TextView familyName = spinner.findViewById(R.id.textViewFamilyName);
                TextView familyID = spinner.findViewById(R.id.textViewFamilyID);
                TextView buttonJoin = spinner.findViewById(R.id.buttonJoin);

                buttonJoin.setVisibility(View.GONE);

                familyName.setText(getItem(position).getFamilyName());
                familyID.setText(getItem(position).getUUID());
                spinner.setScaleX((float)0.75);
                spinner.setScaleY((float)0.75);
                return spinner;
            }

            @Override
            public int getPosition(Family item) {
                int i;
                for (i=0; i < getCount(); i++){
                    if (getItem(i).getUUID().compareTo(item.getUUID()) == 0){
                        return i;
                    }
                }
                return -1;
            }
        };

        familyAdapter.setDropDownViewResource(R.layout.family_list_layout);

        spinnerFamilyGroup.setAdapter(familyAdapter);
        spinnerFamilyGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              String selectedFamilyID = ((Family) spinnerFamilyGroup.getSelectedItem()).getUUID();
              setFamilyID(selectedFamilyID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setCurrentUserSession();
    }

    private void setCurrentUserSession(){
        db.collection("user")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String userSession = documentSnapshot.get("userSession", String.class);
                            int position = ((ArrayAdapter)spinnerFamilyGroup.getAdapter()).getPosition(new Family("", userSession));
                            Log.d("FAMILY ITEM", String.valueOf(position));

                            spinnerFamilyGroup.setSelection(position, true);
                        }
                    }
                });
    }

    private void setFamilyID(String familyID){
        this.familyID = familyID;
    }

    private void getUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    public void updateUserSession(){
        int index = ((ArrayAdapter) spinnerFamilyGroup.getAdapter()).getPosition(new Family("", familyID));
        spinnerFamilyGroup.setSelection(index, true);
        Log.d("Index ", String.valueOf(index));

        if (!familyID.isEmpty()){
            db.collection("user")
                    .document(userId)
                    .update("userSession", familyID)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            openMyProfileActivity();
                            Log.d("SUCCESS", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ERROR", "Error updating document", e);
                        }
                    });
        }

    }

    public void openMyProfileActivity(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

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
                    Toast.makeText(ChangeFamilyActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
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
}
