package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseSnapshot.OwnershipRecord;
import com.example.keepsake.utils.viewHolder.OwnershipRecordViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ViewItemTimelineActivity extends AppCompatActivity {
    private final String TAG = "View Timeline";

    private String itemID;
    private RecyclerView viewOwnershipRecord;

    //todo(naverill) ensure edit button can only be seen by users who own the item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_timeline);

        Intent intent = getIntent();
        itemID = intent.getStringExtra(FirebaseItemAdapter.ID_FIELD);

        bindViews();
    }

    public void loadItemTimeline(final String itemID){
        Query query = FirebaseItemAdapter.queryOwnershipRecordCollection(this, itemID);

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

    public void bindViews(){
        viewOwnershipRecord = findViewById(R.id.recyclerViewRecord);
        viewOwnershipRecord.setHasFixedSize(true);
        viewOwnershipRecord.setLayoutManager(new LinearLayoutManager(this));

        if (itemID != null){
            loadItemTimeline(itemID);
        } else {
            openViewItemActivity();
        }
    }


    public final void openViewItemActivity() {
        Intent intent = new Intent(this, ViewItemActivity.class);
        startActivity(intent);
    }
}
