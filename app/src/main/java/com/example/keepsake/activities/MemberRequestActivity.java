package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.example.keepsake.utils.viewHolder.UserViewHolder;
import com.example.keepsake.utils.adapter.MemberRequestListAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/***
 * This activity shows the list of the joinRequest that the
 * current familyGroup has.
 * Only the Admin of the familyGroup can have access to this page
 * and accept/decline users to the group
 */
public class MemberRequestActivity extends AppCompatActivity implements MemberRequestListAdapter.OnNoteListener {

    private static final String TAG = "Member Requests";

    private User user;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();
    private String familyID;

    private List<User> memberRequestsList;
    private RecyclerView requestView;
    private ScrollView scrollViewRequests;
    private MemberRequestListAdapter memberRequestListAdapter;

    private TextView searchBar;
    private ImageButton imageButtonCancel;
    private RecyclerView userView;
    private ScrollView scrollViewUsers;
    private FirestoreRecyclerAdapter userListAdapter;
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
        setContentView(R.layout.activity_member_request);

        bindViews();
        createUserClass();

    }

    /***
     * This function changes between 2 view which is
     * the memberRequest and the searchView bar
     * @param showRequests boolean to determine to show the memberRequest or not.
     */
    public void switchView(final boolean showRequests){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (scrollViewUsers != null) {
                    scrollViewUsers.setVisibility(!showRequests ? View.VISIBLE : View.GONE);

                }
                if (scrollViewRequests != null) {
                    scrollViewRequests.setVisibility(showRequests ? View.VISIBLE : View.GONE);
                    memberRequestListAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    /***
     * This function updates the changes made on the memberRequestList
     * by creating an eventListener and
     * setting the memberRequestListAdapter to the requestView
     */
    private void memberRequestViewUpdate(){
        requestView = findViewById(R.id.recyclerViewRequests);
        requestView.setHasFixedSize(true);
        requestView.setLayoutManager(new LinearLayoutManager(this));

        memberRequestsList = new ArrayList<>();
        memberRequestListAdapter = new MemberRequestListAdapter(memberRequestsList, this);


        EventListener<QuerySnapshot> listener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (queryDocumentSnapshots != null){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getDocument().exists()){

                            if (doc.getType() ==  DocumentChange.Type.ADDED) {
                                String userID = doc.getDocument().getId();
                                loadUserInfo(userID);
                            }
                        }
                    }
                }
            }
        };

        FirebaseFamilyAdapter.getJoinRequestCollection(this, familyID, listener);

        requestView.setAdapter(memberRequestListAdapter);
    }

    /***
     * This function loads the information of the userID and
     * takes data from the fireBase database.
     * @param userID ID of the user.
     */
    private void loadUserInfo(String userID){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    user.setUserID(userID);
                    memberRequestsList.add(user);

                    if (memberRequestsList.size() == 1){
//                                createRequestsView();
                    }


                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }


    /***
     * This function creates a user class that could be accessed in this activity.
     * It pulls data off the currentUser that is logged-in
     * and creates an instance of a User class of the currentUser
     */
    private void createUserClass(){
        // create a user class for the current user
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

                if (user != null){
                    familyID = user.getUserSession();

                    if (user.getUserSession()!=null){
                        if (!user.getUserSession().isEmpty()){
                            memberRequestViewUpdate();

                        }
                    }

                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    /***
     * this function is called when the admin clicks the accept button.
     * It will accept the user on the position in the adapter.
     * @param position the index position of the adapter.
     */
    @Override
    public void onAcceptClick(int position) {
        // Add current userId to family_group's members
        final String userID = memberRequestsList.get(position).getUserID();

        addMemberToFamilyGroup(userID);
        acceptUser(userID);

        memberRequestsList.remove(position);
        memberRequestListAdapter.notifyDataSetChanged();
        memberRequestListAdapter.notifyItemRemoved(position);
    }
    /***
     * this function is called when the admin clicks the reject button.
     * It will reject the user on the position in the adapter.
     * @param position the index position of the adapter.
     */
    @Override
    public void onDeclineClick(int position) {
        final String userID = memberRequestsList.get(position).getUserID();

        memberRequestsList.remove(position);
        memberRequestListAdapter.notifyItemRemoved(position);

        FirebaseFamilyAdapter.deleteJoinRequestDocument(this, familyID, userID);

        memberRequestListAdapter.notifyDataSetChanged();
    }

    /***
     * this function will add the new userID to
     * the list of members inside the familyGroup
     * @param userID ID of the new user.
     */
    public void addMemberToFamilyGroup(String userID){
        Map data = new HashMap<String, String>() {{
            put(FirebaseFamilyAdapter.EXISTS_FIELD, "1");
        }};

        FirebaseFamilyAdapter.createMemberDocument(this, familyID, userID, data);
        FirebaseFamilyAdapter.deleteJoinRequestDocument(this, familyID, userID);

    }

    /***
     * This function searches the user database
     * for name (strings) that matches with the queryString
     * @param queryString the input string the user typed in
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

        userListAdapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public void onBindViewHolder(final UserViewHolder holder, int position, final User user) {
                // Bind the User object to the UserHolder
                // ...
                holder.bind(getApplicationContext(), user);

                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userView.removeView(view);
                        memberRequestsList.add(user);
                        memberRequestListAdapter.notifyDataSetChanged();
                        switchView(true);

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

        userView.setAdapter(userListAdapter);
        userListAdapter.startListening(); //connects to firebase collection
        userListAdapter.notifyDataSetChanged();
        userListAdapter.onDataChanged();

    }

    /***
     * this function parses the query given on " " (blank spaces)
     * and returns the parsedQuery
     * @param query the initial string passed to the function
     * @return the modified/parsed query
     */
    public ArrayList<String> parseQuery(String query){
        String[] splitQuery =  query.split(" ");
        ArrayList<String> parsedQuery = new ArrayList<>();

        for(int i=0; i < splitQuery.length; i++){
            parsedQuery.add(splitQuery[i]);
        }

        return parsedQuery;
    }

    /***
     * Accepts the userID and set their accepted_field to 1.
     * @param userID ID of the user applying to the familyGroup
     */
    public void acceptUser(String userID) {
        Map data = new HashMap<String, String>() {{
            put(FirebaseUserAdapter.ACCEPTED_FIELD, "1");
        }};

        FirebaseUserAdapter.createFamilyDocument(this, userID, familyID, data);
    }

    /***
     * This function provides the progress bar of the action being done
     * @param isLoading boolean if the action is still loading
     */
    public void setLoading(final boolean isLoading) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (userView != null) {
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
        scrollViewRequests = findViewById(R.id.scrollViewRequests);
        scrollViewUsers = findViewById(R.id.scrollViewUsers);
        userView = findViewById(R.id.recyclerViewUsers);
        userView.setHasFixedSize(true);
        userView.setLayoutManager(new LinearLayoutManager(this));


        imageButtonCancel = findViewById(R.id.imageButtonCancel);
        imageButtonCancel.setVisibility(View.GONE);
        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchView(true);
                imageButtonCancel.setVisibility(View.GONE);
                searchBar.clearFocus();
            }
        });

        handler = new Handler();


        searchBar = findViewById(R.id.editTextSearch);
        userView = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setLoading(true);
                imageButtonCancel.setVisibility(View.VISIBLE);
                switchView(false);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                firebaseUserSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}
