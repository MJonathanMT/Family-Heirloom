package com.example.keepsake.activities;

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

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.example.keepsake.utils.viewHolder.UserViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/***
 * This activity page is the page in the application
 * where you can create a new familyGroup collection in the database.
 * You are then the only Admin of the group.
 * As the admin you have rights to all access of the group.
 * You can accept/reject joinRequest and invite others to your familyGroup
 */
public class CreateFamilyActivity extends AppCompatActivity {
    private final String TAG = "Create Family";

    // Activity
    private ImageButton addMemberButton;
    private Button createFamilyButton;
    private HashSet<String> memberList = new HashSet<>();
    private EditText familyName;
    private LinearLayout memberListLayout;

    // Search user Pop-up
    private Dialog myDialog;
    private TextView searchBar;
    private RecyclerView userView;
    private ProgressBar progressBar;
    private Handler handler;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family_group);
        myDialog = new Dialog(this);
        handler = new Handler();

        bindViews();
    }


    public void showAddMemberPopup(View v) {
        myDialog.setContentView(R.layout.activity_add_user_popup);
        
        searchBar = myDialog.findViewById(R.id.editTextSearch);
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

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    /***
     * This function takes in the string the user typed in the pop up bar
     * and passes the string into the fireBase data and produce a
     * list of all the similar username corresponding to the string
     * @param queryString The string entered by the current user
     */
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
                        memberList.add(user.getUserID());
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

            Map data = new HashMap<String, String>() {{
                put(FirebaseFamilyAdapter.NAME_FIELD, name);
            }};

            OnSuccessListener listener = new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference document ) {
                        // Task completed successfully
                        addMembersToFamily(document.getId());
                }
            };

            FirebaseFamilyAdapter.createDocument(this, listener, data);

        } else {
            Toast.makeText(CreateFamilyActivity.this, "Invalid Family ID", Toast.LENGTH_SHORT).show();
        }
    }


    public void addMembersToFamily(final String familyID) {
        final String adminID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        memberList.add(adminID);

        createFamilyAdmin(familyID, adminID);

        if (!memberList.isEmpty()) {
            for (final String userID : memberList) {

                OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Task completed successfully
                            createFamilyMemberEntry(familyID, userID);
                        }
                    }
                };

                // check if user exists
                FirebaseUserAdapter.getDocument(this, userID, listener);
            }
        }
    }

    public void createFamilyMemberEntry(final String familyID, final String userID) {
        Map memberData = new HashMap<String, String>() {{
            put(FirebaseFamilyAdapter.EXISTS_FIELD, "1");
        }};

        FirebaseFamilyAdapter.createMemberDocument(this, familyID, userID, memberData);


        Map familyData = new HashMap<String, String>() {{
            put(FirebaseUserAdapter.ACCEPTED_FIELD, "1");
        }};

        FirebaseUserAdapter.createFamilyDocument(this, userID, familyID, familyData);

    }

    public void createFamilyAdmin(String familyID, String userID) {
        HashMap data = new HashMap<String, String>() {{
            put(FirebaseFamilyAdapter.EXISTS_FIELD, "1");
        }};

        FirebaseFamilyAdapter.createAdminDocument(this, familyID, userID, data);

        FirebaseUserAdapter.updateDocument(this, userID, FirebaseUserAdapter.USER_SESSION_FIELD, familyID);
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
                openMyProfileActivity(v);
            }
        });
    }

    /***
     * This function redirects the current Intent to the userProfileActivity
     * and starts the next activity.
     */
    public void openMyProfileActivity(View v) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
}
