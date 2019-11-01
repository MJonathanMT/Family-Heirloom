package com.example.keepsake;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewItemTimelineActivity extends AppCompatActivity {
    private String itemID;
    private FirebaseFirestore db;
    private RecyclerView viewOwnershipRecord;

    //todo(naverill) ensure edit button can only be seen by users who own the item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_timeline);
        initialiseDB();

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");

        viewOwnershipRecord = findViewById(R.id.recyclerViewRecord);
        viewOwnershipRecord.setHasFixedSize(true);
        viewOwnershipRecord.setLayoutManager(new LinearLayoutManager(this));

        if (itemID != null){
            loadItemTimeline(itemID);
        } else {
            openViewItemActivity();
        }
    }

    public void loadItemTimeline(final String itemID){
        Query query = db.collection("item")
                .document(itemID)
                .collection("ownership_record")
                .orderBy("startDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<OwnershipRecord> options = new FirestoreRecyclerOptions.Builder<OwnershipRecord>()
                .setQuery(query, OwnershipRecord.class)
                .build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<OwnershipRecord, OwnershipRecordViewHolder>(options) {
            @Override
            public void onBindViewHolder(final OwnershipRecordViewHolder holder, int position, final OwnershipRecord record) {
                // Bind the User object to the UserHolder
                // ...
                holder.bind(getApplicationContext(), record);
            }

            @Override
            public OwnershipRecordViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.timeline_view, group, false);

                return new OwnershipRecordViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };

        viewOwnershipRecord.setAdapter(adapter);
        adapter.startListening(); //connects to firebase collection
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
    }


    public final void openViewItemActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }
}
