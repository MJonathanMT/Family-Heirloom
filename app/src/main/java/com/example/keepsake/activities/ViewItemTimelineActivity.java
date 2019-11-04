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

/***
 * This activity shows all the past and current owner of
 * the current item being selected.
 * This information is stored on "ownershipRecords" inside item database
 */
public class ViewItemTimelineActivity extends AppCompatActivity {
    private final String TAG = "View Timeline";

    private String itemID;
    private RecyclerView viewOwnershipRecord;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_timeline);

        Intent intent = getIntent();
        itemID = intent.getStringExtra(FirebaseItemAdapter.ID_FIELD);

        bindViews();
    }

    /**
     * this function will load/display all the
     * owners that have owned this item
     * @param itemID ID of the item that is being viewed
     */
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

    /***
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
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

    /***
     * This function redirects the current Intent to the ViewItemActivity
     * and starts the next activity.
     */
    public final void openViewItemActivity() {
        Intent intent = new Intent(this, ViewItemActivity.class);
        startActivity(intent);
    }
}
