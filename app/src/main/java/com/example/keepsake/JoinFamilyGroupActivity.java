package com.example.keepsake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class JoinFamilyGroupActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private EditText searchBar;
    private RecyclerView familyView;
    private ProgressBar progressBar;
    private Handler handler;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseDB();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setContentView(R.layout.activity_join_family_group);

        searchBar = findViewById(R.id.editTextSearch);
        progressBar = findViewById(R.id.progressBar);
        familyView = findViewById(R.id.recyclerViewFamilies);

        handler = new Handler();
        familyView.setHasFixedSize(true);
        familyView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        populateFamilyView(userID);

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

    public void firebaseFamilySearch(String queryString) {
        String parsedQuery = queryString.trim();

        final Query query = db.collection("family_group")
                    .whereEqualTo("familyName", parsedQuery);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document : task.getResult()){
                        Toast.makeText(JoinFamilyGroupActivity.this, document.get("familyName", String.class), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        FirestoreRecyclerOptions<Family> options = new FirestoreRecyclerOptions.Builder<Family>()
                .setQuery(query, new SnapshotParser<Family>() {
                    @NonNull
                    @Override
                    public Family parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        final Family family = new Family();

                        if(snapshot.exists()){
                            family.setFamilyName(snapshot.get("familyName", String.class));
                            family.setUUID(snapshot.getId());
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
                        sendFamilyRequest(family.getUUID());
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

    public void sendFamilyRequest(String familyID){
        //todo(naverill) change all join requests ID to be userIDs in database
        createJoinRequest(familyID);
        createUserFamilyGroup(familyID);
        createUserSession(familyID);
    }

    public void createJoinRequest(String familyID){
        Map<String, String> dataJoinRequest = new HashMap<String, String>() {{
            put("exists", "1");
        }};

        db.collection("family_group")
                .document(familyID)
                .collection("joinRequests")
                .document(userID)
                .set(dataJoinRequest);
    }

    public void createUserFamilyGroup(String familyID){
        Map<String, String> dataFamilyGroup = new HashMap<String, String>() {{
            put("accepted", "0");
        }};

        db.collection("user")
                .document(userID)
                .collection("familyGroups")
                .document(familyID)
                .set(dataFamilyGroup);
    }

    public void createUserSession(String familyID){
        db.collection("user")
                .document(userID)
                .update("userSession", familyID);
    }

    public void populateFamilyView(String userID){
        final Query query = db.collection("user")
                .document(userID)
                .collection("familyGroups");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){

                    FirestoreRecyclerOptions<Family> options = new FirestoreRecyclerOptions.Builder<Family>()
                            .setQuery(query, new SnapshotParser<Family>() {
                                @NonNull
                                @Override
                                public Family parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                    final Family family = new Family();

                                    if(snapshot.exists()){
                                        String familyID = snapshot.getId();

                                        db.collection("familyGroup").document(familyID)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()){
                                                            family.setFamilyName(documentSnapshot.get("familyName", String.class));
                                                        }
                                                    }
                                                });

                                        family.setUUID(familyID);
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
            }
        });
    }


    public void openRequestFamilyGroupActivity() {
        Intent intent = new Intent(this, RequestFamilyGroupActivity.class);
        startActivity(intent);
    }

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

    public void initialiseDB() {
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

}
