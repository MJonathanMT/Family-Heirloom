package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.lang.String;

public class ViewItemActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageButton buttonEdit;
    private ImageButton buttonExit;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
//        itemId = "Q5SWGQ3jNngl5DDtHni4";
        initialiseDB();

        buttonEdit = findViewById(R.id.buttonEdit);
        buttonExit = findViewById(R.id.imageButtonClearOwner);

        loadItemInfo(itemId);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemActivity(itemId);
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
                                ((TextView) findViewById(R.id.textViewItemName)).setText((String) document.get("name"));
                                ((TextView) findViewById(R.id.textViewEditDescription)).setText((String)document.get("description"));
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
