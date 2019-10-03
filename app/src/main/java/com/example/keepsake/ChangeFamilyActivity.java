package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;

import javax.annotation.Nullable;

public class ChangeFamilyActivity extends AppCompatActivity {
    private FirebaseFirestore fbfs;
    private String userId;
    private List<String> userFamilyNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_family);

        fbfs = FirebaseFirestore.getInstance();
        getUserId();
        createFamilyList();

        Button buttonChange = findViewById(R.id.buttonChange);

        final Spinner familyNamesSpinner = findViewById(R.id.changeCurrentFamilySpinner);
//        getResoucers.getStringArray(R.array.familyNames));
        // Spinner click listener
        familyNamesSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter<String> familyNamesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, userFamilyNameList);
        familyNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familyNamesSpinner.setAdapter(familyNamesAdapter);


        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = String.valueOf(familyNamesSpinner.getSelectedItem());
                Log.d("CURRENT FAMILY", newName);
                DocumentReference currentUser = fbfs.collection("user").document(userId);

                // Set the "isCapital" field of the city 'DC'
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

    private void createFamilyList(){
        // Get the list of family that the current user is in
        fbfs.collection("user").document(userId).collection("familyNames").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ERROR", "Error: " + e.getMessage());
                }
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() ==  DocumentChange.Type.ADDED) {
                        QueryDocumentSnapshot data = doc.getDocument();
                        userFamilyNameList.add((String) data.get("familyName"));
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
    }
    public void openActivity(){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
}
