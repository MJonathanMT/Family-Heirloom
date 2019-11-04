package com.example.keepsake.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Family;
import com.example.keepsake.utils.viewHolder.FamilyViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/***
 * This activity is where you have chosen to join an existing familyGroup
 * When you finish an action in this activity, a joinRequest will be send to the familyGroup.
 * You will be accept in the familyGroup if the admin of the group accepts you.
 */
public class JoinFamilyGroupActivity extends AppCompatActivity {
    private final String TAG = "Join Family";

    private EditText searchBar;
    private RecyclerView familyView;
    private ProgressBar progressBar;
    private Handler handler;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_family_group);

        bindViews();
        populateFamilyView(userID);

    }


    /***
     * This function searches the familyGroup database
     * for name (strings) that matches with the queryString
     * @param queryString the input string the user typed in
     */
    public void firebaseFamilySearch(String queryString) {
        String parsedQuery = queryString.trim();

        final Query query = FirebaseFamilyAdapter.queryFamilyName(this, parsedQuery);

        FirestoreRecyclerOptions<Family> options = new FirestoreRecyclerOptions.Builder<Family>()
                .setQuery(query, new SnapshotParser<Family>() {
                    @NonNull
                    @Override
                    public Family parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        final Family family = snapshot.toObject(Family.class);

                        if(snapshot.exists()){
                            family.setFamilyID(snapshot.getId());
                        }
                        return family;

                    }})
                .build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Family, FamilyViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FamilyViewHolder holder, int position, final Family family) {
                // Bind the User object to the UserHolder
                // ...
                holder.bind(family);

                holder.setButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendFamilyRequest(family.getFamilyID());
                        openRequestFamilyGroupActivity();
                    }
                });
            }

            @Override
            public FamilyViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.family_list_layout, group, false);

                return new FamilyViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                setLoading(false);
                super.onDataChanged();
            }
        };

        familyView.setAdapter(adapter);
        adapter.startListening(); //connects to firebase collection
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
    }

    /***
     * this function manages three functions which acts upon the familyID
     * @param familyID the ID of the familyGroup
     */
    public void sendFamilyRequest(String familyID){
        createJoinRequest(familyID);
        createUserFamilyGroup(familyID);
        createUserSession(familyID);
    }

    /***
     * this function sends a joinRequest to the familyGroup(familyID)
     * @param familyID the ID of the familyGroup
     */
    public void createJoinRequest(String familyID){
        Map<String, String> dataJoinRequest = new HashMap<String, String>() {{
            put(FirebaseFamilyAdapter.EXISTS_FIELD, "1");
        }};

        FirebaseFamilyAdapter.createJoinRequestDocument(this, familyID, userID, dataJoinRequest);
    }

    /***
     * this function creates a familyGroup with the familyID
     * and adds it the currentUser sending the joinRequest.
     * the accepted_field will be set to 0 until the admin accepts you
     * @param familyID the ID of the familyGroup
     */
    public void createUserFamilyGroup(String familyID){
        Map<String, String> dataFamilyGroup = new HashMap<String, String>() {{
            put(FirebaseUserAdapter.ACCEPTED_FIELD, "0");
        }};

        FirebaseUserAdapter.createFamilyDocument(this, userID, familyID, dataFamilyGroup);
    }

    /***
     * Changes the current userSession to the familyID
     * You wont be able to see any items until the admin accepts you
     * @param familyID the ID of the familyGroup
     */
    public void createUserSession(String familyID){
        FirebaseUserAdapter.updateDocument(this, userID, FirebaseUserAdapter.USER_SESSION_FIELD, familyID);
    }

    /***
     * this function populates the pop up view of the search bar
     * @param userID ID of the user
     */
    public void populateFamilyView(String userID){
        final Query query = FirebaseUserAdapter.queryFamilies(this, userID);

        FirestoreRecyclerOptions<Family> options = new FirestoreRecyclerOptions.Builder<Family>()
                .setQuery(query, new SnapshotParser<Family>() {
                    @NonNull
                    @Override
                    public Family parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        final Family family = new Family();

                        if(snapshot.exists()){
                            String familyID = snapshot.getId();

                            OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        family.setFamilyName(documentSnapshot.get(FirebaseFamilyAdapter.NAME_FIELD, String.class));
                                    }
                                }
                            };

                            FirebaseFamilyAdapter.getDocument(JoinFamilyGroupActivity.this, familyID, listener);

                            family.setFamilyID(familyID);
                        }
                        return family;

                    }}
                )
                .build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Family, FamilyViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FamilyViewHolder holder, int position, final Family family) {
                // Bind the User object to the UserHolder
                holder.bind(family);
                holder.setButtonVisibility(View.GONE);
            }

            @Override
            public FamilyViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.family_list_layout, group, false);

                return new FamilyViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                setLoading(false);
                super.onDataChanged();
            }
        };

        familyView.setAdapter(adapter);
        adapter.startListening(); //connects to firebase collection
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
    }

    /***
     * This function provides the progress bar of the action being done
     * @param isLoading boolean if the action is still loading
     */
    public void setLoading(final boolean isLoading) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    familyView.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
                }
                if (familyView != null) {
                    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    /***
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        searchBar = findViewById(R.id.editTextSearch);
        progressBar = findViewById(R.id.progressBar);
        familyView = findViewById(R.id.recyclerViewFamilies);

        handler = new Handler();
        familyView.setHasFixedSize(true);
        familyView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setLoading(true);
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                firebaseFamilySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /***
     * This function redirects the current Intent to the RequestFamilyGroupActivity
     * and starts the next activity.
     */
    public void openRequestFamilyGroupActivity() {
        Intent intent = new Intent(this, RequestFamilyGroupActivity.class);
        startActivity(intent);
    }
}
