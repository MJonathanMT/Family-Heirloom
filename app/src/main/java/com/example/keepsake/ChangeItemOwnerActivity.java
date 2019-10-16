package com.example.keepsake;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeItemOwnerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String itemID;
    private User newOwner;
    private String ownerPrivacy = "O";
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    private ImageButton imageButtonChangeOwner;
    private Button buttonConfirm;
    private TextView textViewName;
    private Spinner spinnerPrivacy;


    // search dialog
    private Dialog dialog;
    private TextView searchBar;
    private ImageButton searchButton;
    private RecyclerView userView;
    private ProgressBar progressBar;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialiseDB();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_item_owner);

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");

        dialog = new Dialog(this);
        handler = new Handler();

        textViewName = findViewById(R.id.textViewName);
        imageButtonChangeOwner = findViewById(R.id.imageButtonChangeOwner);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);

        imageButtonChangeOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserSearchPopup();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpdateOwnership();
                openHomePageActivity();
            }
        });

        populateUserDetails();
        populatePrivacyLevels();
    }

    public void populateUserDetails(){
        db.collection("user")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String name = documentSnapshot.get("firstName") + " " + documentSnapshot.get("lastName");
                            textViewName.setText(name);
                        }
                    }
                });
    }

    public void populatePrivacyLevels(){
        ArrayAdapter<String> privacyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.privacyLevels));
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String level = spinnerPrivacy.getSelectedItem().toString();
                ownerPrivacy = level.substring(0, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerPrivacy.setAdapter(privacyAdapter);
    }

    public void openUserSearchPopup(){
        dialog.setContentView(R.layout.activity_add_user_popup);

        searchBar = dialog.findViewById(R.id.editTextSearch);
        searchButton = dialog.findViewById(R.id.imageButtonSearch);
        userView = dialog.findViewById(R.id.recyclerViewUsers);
        progressBar = dialog.findViewById(R.id.progressBar);

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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

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
                        user.setFirstName(snapshot.get("firstName", String.class));
                        user.setLastName(snapshot.get("lastName", String.class));
                        user.setUUID(snapshot.getId());
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
                        dialog.dismiss();

                        String name = user.getFirstName() + " " + user.getLastName();
                        textViewName.setTypeface(Typeface.DEFAULT);
                        textViewName.setText(name);

                        newOwner = user;
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

    public void handleUpdateOwnership(){
        if(newOwner == null){
            Toast.makeText(ChangeItemOwnerActivity.this, "No user selected", Toast.LENGTH_SHORT).show();
            return;
        }
        createOwnerShipRecord();
        transferOwnership();
    }

    public void createOwnerShipRecord(){
        final DocumentReference item = db.collection("item").document(itemID);

        item.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();

                    addOwnershipRecord(item, document);

                } else {
                    Toast.makeText(ChangeItemOwnerActivity.this, "Failed to create ownership record", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void transferOwnership(){
        db.collection("user")
                .document(userID)
                .collection("items")
                .document(itemID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            final Map<String, String> data = new HashMap<String, String>() {{
                                put("exists", "1");;
                            }};

                            db.collection("user")
                                    .document(newOwner.getUUID())
                                    .collection("items")
                                    .document(itemID)
                                    .set(data);
                        } else {
                            Toast.makeText(ChangeItemOwnerActivity.this, "Failed to transfer ownership", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addOwnershipRecord(final DocumentReference docRef, final DocumentSnapshot document){
        // current date value
        final Date date = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        //todo(naverill) convert all familyName references to familyID references
        final Map<String, String> oldOwnerData = new HashMap<String, String>() {{
            put("startDate", document.get("start_date", String.class));
            put("privacy", ownerPrivacy);
            put("endDate", formatter.format(date));
            put("ownerID", userID);
            put("familyName", document.get("familyName", String.class));
        }};

        docRef.collection("ownership_record").document().set(oldOwnerData);

        //todo (naverill) refine how the item is transferred into new family
//        final Map<String, String> newOwnerData = new HashMap<String, String>() {{
//            put("start_date", formatter.format(date));
//            put("privacy", "O");
//            put("ownerID", newOwner.getUUID());
//            put("familyID", newOwner.getUserSession());
//        }};
//
//        docRef.collection("ownership_record").document().set(newOwnerData);

        docRef.update("startDate", formatter.format(date));
        docRef.update("privacy", "O");
        docRef.update("owner", newOwner.getUUID());
        docRef.update("familyName", document.get("familyName", String.class));
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

    public void openHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
}
