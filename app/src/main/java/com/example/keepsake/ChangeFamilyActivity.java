package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ChangeFamilyActivity extends AppCompatActivity {
    private FirebaseFirestore fbfs;
    private String userId;
    private ArrayList<String> userFamilyNameList = new ArrayList<>();
    private Spinner familyNamesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_family);
        fbfs = FirebaseFirestore.getInstance();
        getUserId();
        createFamilyList();
        manageChange();
    }

    private void createFamilyList(){
        // Get the list of family that the current user is in
        fbfs.collection("user").document(userId).collection("familyNames").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", "Error: " + e.getMessage());
                }
                assert queryDocumentSnapshots != null;
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() ==  DocumentChange.Type.ADDED) {
                        QueryDocumentSnapshot data = doc.getDocument();
                        userFamilyNameList.add((String) data.get("familyName"));
                    }
                }

                // Create spinner based on array list
                familyNamesSpinner = findViewById(R.id.changeCurrentFamilySpinner);
                ArrayAdapter<String> familyNamesAdapter = new ArrayAdapter<>(ChangeFamilyActivity.this,
                        android.R.layout.simple_list_item_1, userFamilyNameList);
                familyNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                familyNamesSpinner.setAdapter((familyNamesAdapter));
            }
        });
    }
    private void getUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    private void manageChange(){
        Button buttonChange = findViewById(R.id.buttonChange);


        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = String.valueOf(familyNamesSpinner.getSelectedItem());
                DocumentReference currentUser = fbfs.collection("user").document(userId);
                currentUser
                        .update("currentFamilyName", newName)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SUCCESS", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ERROR", "Error updating document", e);
                            }
                        });
                openActivity();
            }
        });


    }
    public void openActivity(){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
}
