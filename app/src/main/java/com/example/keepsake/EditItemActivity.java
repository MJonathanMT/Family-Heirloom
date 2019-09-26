package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.String;
import java.util.Map;
import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class EditItemActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageButton editPhotoButton;
    private ImageButton updateItem;
    private EditText editName;
    private EditText editDescription;
    private String itemID;
    private ImageButton buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        initialiseDB();

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");

        editName = findViewById(R.id.editTextEditName);
        editDescription = findViewById(R.id.editTextEditDescription);
        editPhotoButton = findViewById(R.id.imageButtonEditPhoto);
        updateItem = findViewById(R.id.imageButtonUploadItem);
        buttonExit = findViewById(R.id.imageButtonExit);

        final DocumentReference docRef = db.collection("item").document(itemID);

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
                                editName.setText((String) document.get("name"));
                                editDescription.setText((String)document.get("description"));
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

    public void openViewItemActivity() {
        System.out.println(itemID);
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemID", itemID);
        startActivity(intent);
    }


    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

    public final void openViewItemActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemID", itemID);
        startActivity(intent);
    }

}
