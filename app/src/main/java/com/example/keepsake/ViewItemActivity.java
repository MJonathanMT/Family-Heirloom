package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.lang.String;

public class ViewItemActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageButton buttonEdit;
    private TextView textViewItemName;
    private TextView textViewItemDescription;
    private ImageButton buttonExit;
    private TextView textViewFamilyName;
    private String itemID;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //todo(naverill) ensure edit button can only be seen by users who own the item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");

//        itemId = "Q5SWGQ3jNngl5DDtHni4";
        initialiseDB();

        buttonEdit = findViewById(R.id.buttonEdit);
        buttonExit = findViewById(R.id.imageButtonClearOwner);
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        textViewFamilyName = findViewById(R.id.textViewFamilyName);

        loadItemInfo(itemID);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemActivity(itemID);
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreviousActivity();
            }
        });

    }

    public void loadItemInfo(final String itemID){
        DocumentReference docRef = db.collection("item").document(itemID);

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

                                String familyID = document.get("familyID", String.class);

                                if (familyID == null){
                                    return;
                                }

                                db.collection("family_group").document(familyID)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            textViewFamilyName.setText(documentSnapshot.get("familyName", String.class));
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

    public final void openPreviousActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }
}
