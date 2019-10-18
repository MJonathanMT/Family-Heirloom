package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditItemActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private ImageButton editPhotoButton;
    private ImageButton updateItem;
    private EditText editName;
    private EditText editDescription;
    private ImageButton changeOwnerButton;
    private ImageButton buttonExit;

    private Spinner spinnerFamilyGroup;
    private Spinner spinnerPrivacy;

    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String itemID;

    private String itemPrivacy = "O";
    private String familyID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        initialiseDB();

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");

        editName = findViewById(R.id.editTextEditName);
        editDescription = findViewById(R.id.editTextEditDescription);
        editPhotoButton = findViewById(R.id.imageButtonEditPhoto);
        changeOwnerButton = findViewById(R.id.imageButtonChangeOwner);
        updateItem = findViewById(R.id.imageButtonUploadItem);
        buttonExit = findViewById(R.id.imageButtonClearOwner);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        spinnerFamilyGroup = findViewById(R.id.spinnerFamilyGroup);

        final DocumentReference docRef = db.collection("item").document(itemID);

        populatePrivacyLevelSpinner();
        populateFamilyGroupSpinner();
        loadItemInfo(docRef);

        editPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemPhotoActivity();
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewItemActivity(itemID);
            }
        });

        updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem(docRef);
            }
        });

        changeOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeOwnerActivity();
            }
        });
    }

    public void loadItemInfo(DocumentReference docRef){

        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Task completed successfully
                            final DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                String familyName = document.get("name", String.class);
                                editName.setText(familyName);
                                editDescription.setText(document.get("description", String.class));
                                itemPrivacy = document.get("privacy", String.class);
                                familyID = document.get("familyID", String.class);

                                int index = ((ArrayAdapter) spinnerFamilyGroup.getAdapter()).getPosition(new Family(familyName, familyID));
                                spinnerFamilyGroup.setSelection(index, true);
                                Toast.makeText(EditItemActivity.this, "Index " + String.valueOf(index), Toast.LENGTH_SHORT).show();


                                int selectedIndex;
                                if (itemPrivacy == "O"){
                                    selectedIndex = 0;
                                } else if (itemPrivacy == "F"){
                                    selectedIndex = 1;
                                } else if (itemPrivacy == "P") {
                                    selectedIndex = 2;
                                } else {
                                    selectedIndex = 0;
                                }

                                spinnerPrivacy.setSelection(selectedIndex, true);
                            } else {
                                //TODO(naverill) notify user of failure
                            }
                        } else {
                            // Task failed with an exception
                            Exception exception = task.getException();
                            Toast.makeText(EditItemActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );
    }

    public void populatePrivacyLevelSpinner(){
        ArrayAdapter<String> privacyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.privacyLevels));
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String level = spinnerPrivacy.getSelectedItem().toString();
                itemPrivacy = level.substring(0, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerPrivacy.setAdapter(privacyAdapter);
    }

    public void populateFamilyGroupSpinner(){
        Query query = db.collection("user").document(userID).collection("familyGroups");

        final List<Family> familyList = new ArrayList<>();

        ArrayAdapter<Family> familyAdapter = new ArrayAdapter<Family>(this, R.layout.family_list_layout, familyList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot docRef : queryDocumentSnapshots){
                    String familyID = docRef.get("familyID", String.class);

                    DocumentReference famRef = db.collection("family_group").document(familyID);

                    famRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Family family = new Family();
                            family.setFamilyName(documentSnapshot.get("familyName", String.class));
                            family.setUUID((documentSnapshot.getId()));
                            familyList.add(family);
                        }
                    });
                }
            }
        });

        spinnerFamilyGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                familyID = ((Family) spinnerFamilyGroup.getSelectedItem()).getUUID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerFamilyGroup.setAdapter(familyAdapter);
    }

    private void updateItem(final DocumentReference docRef){
        final String newName = editName.getText().toString().trim();
        final String newDescription = editDescription.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(EditItemActivity.this, "Please Enter Item Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newDescription)) {
            Toast.makeText(EditItemActivity.this, "Please Enter Item Description", Toast.LENGTH_SHORT).show();
            return;
        }

        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            Map<String, String> data = new HashMap<String, String>() {{
                                    put("name", newName);
                                    put("description", newDescription);
                                    put("privacy", itemPrivacy);
                                    put("familyID", familyID);
                                }};

                            docRef.set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditItemActivity.this, "Upload successsful", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditItemActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            // Task failed with an exception
                            Exception exception = task.getException();
                        }
                    }
                });
        openViewItemActivity();
    }

    public void openEditItemPhotoActivity() {
        // TODO(KIREN)
    }

    public void openChangeOwnerActivity(){
        Intent intent = new Intent(this, ChangeItemOwnerActivity
                .class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

    public void openViewItemActivity() {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }


    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

    public final void openViewItemActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

}
