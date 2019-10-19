package com.example.keepsake;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CreateFamilyActivity extends AppCompatActivity {
    // DB
    private FirebaseFirestore db;

    // Activity
    private ImageButton addMemberButton;
    private Button createFamilyButton;
    private HashSet<String> memberList = new HashSet<>();
    private EditText familyName;
    private LinearLayout memberListLayout;

    // Search user Pop-up
    private Dialog myDialog;
    private TextView searchBar;
    private ImageButton searchButton;
    private RecyclerView userView;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialiseDB();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family_group);
        myDialog = new Dialog(this);
        handler = new Handler();

        memberListLayout = findViewById(R.id.linearLayoutMembers);

        addMemberButton = findViewById(R.id.addMemberButton);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMemberPopup(v);
            }
        });

        createFamilyButton = findViewById(R.id.buttonHomePage);
        createFamilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFamilyEntry();
                openFamilyHomepageActivity(v);
            }
        });
    }


    public void showAddMemberPopup(View v) {
        myDialog.setContentView(R.layout.activity_add_user_popup);

        searchBar = myDialog.findViewById(R.id.editTextSearch);
        searchButton = myDialog.findViewById(R.id.imageButtonSearch);
        userView = myDialog.findViewById(R.id.recyclerViewUsers);
        progressBar = myDialog.findViewById(R.id.progressBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setLoading(true);
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                firebaseUserSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        userView.setHasFixedSize(true);
        userView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);


        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setLoading(true);
                String query =  searchBar.getText().toString();
                firebaseUserSearch(query);
            }
        });


        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void firebaseUserSearch(String queryString){
        ArrayList<String> parsedQuery = parseQuery(queryString);

        final Query query;

        if (parsedQuery.size() == 1){
            query = db.collection("user")
                    .whereEqualTo("firstName", parsedQuery.get(0));
        } else if (parsedQuery.size() > 1){
            query = db.collection("user")
                    .whereEqualTo("firstName", parsedQuery.get(0))
                    .whereEqualTo("lastName", parsedQuery.get(1));
        } else {
            return;
        }

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, new SnapshotParser<User>() {
                    @NonNull
                    @Override
                    public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        User user = new User();
                        user.setFirstName((String) snapshot.get("firstName"));
                        user.setLastName((String) snapshot.get("lastName"));
                        user.setUUID(snapshot.getId());
                        user.setUsername(snapshot.get("username", String.class));
                        return user;
                    }
                })
                .build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public void onBindViewHolder(final UserViewHolder holder, int position, final User user) {
                // Bind the User object to the UserHolder
                // ...
                holder.bind(getApplicationContext(), user);

                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        memberList.add(user.getUUID());
                        myDialog.dismiss();
                        userView.removeView(view);
                        memberListLayout.addView(view);
                        view.setScaleY((float) 0.6);
                        view.setScaleX((float) 0.6);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            view.setForegroundGravity(Gravity.LEFT);
                        }
                        view.setOnClickListener(null);
                    }
                });
            }

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.user_list_layout, group, false);

                return new UserViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                setLoading(false);
                super.onDataChanged();
            }
        };
        userView.setAdapter(adapter);
        adapter.startListening(); //connects to firebase collection
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();

    }

    public void createFamilyEntry() {
        familyName = findViewById(R.id.editText_enter_family_id);

        if (familyName != null) {
            final String name = familyName.getText().toString();

            db.collection("family_group")
                    .add(
                    new HashMap<String, String>() {{
                        put("familyName", name);
                    }})
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                // Task completed successfully
                                final DocumentReference familyRef = task.getResult();
                                addMembersToFamily(familyRef);
                            } else {
                                // Task failed with an exception
                                Exception exception = task.getException();
                                Toast.makeText(CreateFamilyActivity.this, "Failed to create family entry", Toast.LENGTH_SHORT).show();
                            }
                        }
            });

        } else {
            //TODO(naverill) Notify user of failure
            Toast.makeText(CreateFamilyActivity.this, "Invalid Family ID", Toast.LENGTH_SHORT).show();
        }
    }

    public void addMembersToFamily(final DocumentReference familyRef) {
        final String adminID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        memberList.add(adminID);

        createFamilyMemberAdmin(familyRef, adminID);

        if (!memberList.isEmpty()) {
            for (final String userID : memberList) {

                // check if user exists
                final DocumentReference userRef = db.collection("user").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Task completed successfully
                            createFamilyMemberEntry(familyRef, userID);
                        } else {
                            // Task failed with an exception
                            Exception exception = task.getException();
                            Toast.makeText(CreateFamilyActivity.this, "Failed to add member: " + userID, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public void createFamilyMemberEntry(final DocumentReference familyRef, final String userID) {
        final DocumentReference memberRef = familyRef.collection("members").document(userID);

        memberRef.set(
                new HashMap<String, String>() {{
                    put("exists", "1");
                }})
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateFamilyActivity.this, "Failed to create family member entry", Toast.LENGTH_SHORT).show();
                            }
                        });


        db.collection("user")
                .document(userID)
                .collection("familyGroups")
                .document(familyRef.getId())
                .set(
                    new HashMap<String, String>() {{
                        put("accepted", "1");
                    }})
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateFamilyActivity.this, "Failed to create family member entry", Toast.LENGTH_SHORT).show();
                            }
                        });

    }

    public void createFamilyMemberAdmin(DocumentReference familyRef, String userID) {
        final DocumentReference adminRef = familyRef.collection("admin").document(userID);

        HashMap data = new HashMap<String, String>() {{
            put("exists", "1");
        }};

        adminRef.set(data);

        db.collection("user")
                .document(userID)
                .update("userSession", familyRef.getId());
    }

    public ArrayList<String> parseQuery(String query){
        String[] splitQuery =  query.split(" ");
        ArrayList<String> parsedQuery = new ArrayList<>();

        for(int i=0; i < splitQuery.length; i++){
            parsedQuery.add(splitQuery[i]);
        }

        return parsedQuery;
    }

    public void setLoading(final boolean isLoading) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    userView.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
                }
                if (userView != null) {
                    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    public void initialiseDB() {
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

    public void openFamilyHomepageActivity(View v) {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
}
