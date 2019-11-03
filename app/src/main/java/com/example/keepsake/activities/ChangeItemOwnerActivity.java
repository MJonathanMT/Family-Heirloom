package com.example.keepsake.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Item;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.example.keepsake.utils.viewHolder.UserViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChangeItemOwnerActivity extends AppCompatActivity {
    private final String TAG = "Change Owner";

    private String itemID;
    private User newOwner;
    private String ownerPrivacy = "O";
    private String userID = FirebaseAuthAdapter.getCurrentUserID();


    private ImageButton imageButtonChangeOwner;
    private ImageView imageViewProfile;
    private Button buttonConfirm;
    private TextView textViewDisplayName;
    private TextView textViewDisplayUsername;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_item_owner);

        Intent intent = getIntent();
        itemID = intent.getStringExtra(FirebaseItemAdapter.ID_FIELD);

        dialog = new Dialog(this);
        handler = new Handler();

        bindViews();
        populateUserDetails();
        populatePrivacyLevels();
    }

    public void populateUserDetails(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    String name = user.getFirstName() + " " + user.getLastName();
                    textViewDisplayName.setText(name);
                    textViewDisplayUsername.setText(documentSnapshot.get(FirebaseUserAdapter.USERNAME_FIELD, String.class));
                    // Load into activity
                    Picasso.get().load(user.getUrl()).into(imageViewProfile);
                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
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
            query = FirebaseUserAdapter.queryUserFirstName(this, parsedQuery.get(0));
        } else if (parsedQuery.size() > 1){
            query = FirebaseUserAdapter.queryUserFullName(this, parsedQuery.get(0), parsedQuery.get(1));
        } else {
            return;
        }

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, new SnapshotParser<User>() {
                    @NonNull
                    @Override
                    public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        User user = snapshot.toObject(User.class);
                        user.setUserID(snapshot.getId());
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
                        textViewDisplayName.setTypeface(Typeface.DEFAULT);
                        textViewDisplayName.setText(name);
                        textViewDisplayUsername.setText(user.getUsername());
                        Picasso.get().load(user.getUrl()).into(imageViewProfile);

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
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                Item item = document.toObject(Item.class);
                addOwnershipRecord(item);
            }
        };

        FirebaseItemAdapter.getDocument(this, itemID, listener);
    }

    public void transferOwnership(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final Map<String, String> data = new HashMap<String, String>() {{
                    put(FirebaseItemAdapter.EXISTS_FIELD, "1");
                }};

                FirebaseUserAdapter.createItemDocument(ChangeItemOwnerActivity.this, newOwner.getUserID(), itemID, data);

            }
        };

        FirebaseUserAdapter.deleteItemDocument(this, userID, itemID, listener);
    }

    public void addOwnershipRecord(final Item item){
        // current date value
        final Date date = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat(FirebaseItemAdapter.DATE_FORMAT);

        OnSuccessListener listener = new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                    Item item = documentSnapshot.toObject(Item.class);

                    final HashMap<String, String> newOwnerData = new HashMap<String, String>() {{
                        put(FirebaseItemAdapter.START_DATE_FIELD, formatter.format(date));
                        put(FirebaseItemAdapter.PRIVACY_FIELD, ownerPrivacy);
                        put(FirebaseItemAdapter.OWNER_ID_FIELD, item.getOwnerID());
                        put(FirebaseItemAdapter.FAMILY_ID_FIELD, item.getFamilyID());
                    }};

                    FirebaseItemAdapter.updateDocument(ChangeItemOwnerActivity.this, itemID, newOwnerData);
                }
            }
        };

        FirebaseItemAdapter.getOwnershipRecordDocument(this, itemID, item.getOwnerID(), item.getStartDate(), listener);

        final Map<String, String> newOwnerData = new HashMap<String, String>() {{
            put(FirebaseItemAdapter.START_DATE_FIELD, formatter.format(date));
            put(FirebaseItemAdapter.PRIVACY_FIELD, FirebaseItemAdapter.PRIVACY_OWNER);
            put(FirebaseItemAdapter.OWNER_ID_FIELD, newOwner.getUserID());
            put(FirebaseItemAdapter.FAMILY_ID_FIELD, newOwner.getUserSession());
        }};

        FirebaseItemAdapter.createOwnershipRecordDocument(this, itemID, newOwnerData);
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

    public void bindViews(){
        textViewDisplayName = findViewById(R.id.textViewDisplayName);
        textViewDisplayUsername = findViewById(R.id.textViewDisplayUsername);
        imageViewProfile = findViewById(R.id.imageViewProfile);
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
                openMyProfileActivity();
            }
        });
    }

    public void openMyProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
}