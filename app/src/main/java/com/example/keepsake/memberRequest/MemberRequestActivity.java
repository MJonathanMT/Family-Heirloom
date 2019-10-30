package com.example.keepsake.memberRequest;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.User;
import com.example.keepsake.UserViewHolder;
import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class MemberRequestActivity extends AppCompatActivity implements MemberRequestListAdapter.OnNoteListener{

    private static final String TAG = "FireLog";
    private FirebaseFirestore db;

    private User currentUser;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String currentFamilyId;

    private List<User> memberRequestsList;
    private RecyclerView requestView;
    private ScrollView scrollViewRequests;
    private LinearLayout memberRequestLayout;
    private MemberRequestListAdapter memberRequestListAdapter;

    private TextView searchBar;
    private ImageButton imageButtonCancel;
    private List<User> userList;
    private RecyclerView userView;
    private ScrollView scrollViewUsers;
    private FirestoreRecyclerAdapter userListAdapter;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_member_request);


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

        createUserClass();

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

    public void switchView(final boolean showRequests){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (scrollViewUsers != null) {
                    scrollViewUsers.setVisibility(!showRequests ? View.VISIBLE : View.GONE);
                    Log.d("UserView", scrollViewUsers.getVisibility() == View.VISIBLE ? "Visible" : "gone");

                }
                if (scrollViewRequests != null) {
                    scrollViewRequests.setVisibility(showRequests ? View.VISIBLE : View.GONE);
                    memberRequestListAdapter.notifyDataSetChanged();
                    Log.d("RequestsView", scrollViewRequests.getVisibility() == View.VISIBLE ? "Visible" : "gone");
                }
            }
        });
    }

    private void memberRequestViewUpdate(){
        requestView = findViewById(R.id.recyclerViewRequests);
        requestView.setHasFixedSize(true);
        requestView.setLayoutManager(new LinearLayoutManager(this));

        memberRequestsList = new ArrayList<>();
        memberRequestListAdapter = new MemberRequestListAdapter(memberRequestsList, this);

        db.collection("family_group")
                .document(currentFamilyId)
                .collection("joinRequests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        });

        requestView.setAdapter(memberRequestListAdapter);
    }

    private void loadUserInfo(String userID){
        db.collection("user")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = new User();
                            user.setUserID(userID);
                            user.setUsername(documentSnapshot.get("username", String.class));
                            user.setFirstName(documentSnapshot.get("firstName", String.class));
                            user.setLastName(documentSnapshot.get("lastName", String.class));
                            memberRequestsList.add(user);

                            if (memberRequestsList.size() == 1){
//                                createRequestsView();
                            }

                            Log.d("Member requests size", String.valueOf(memberRequestsList.size()));

                        }
                    }
                });
    }



    private void createUserClass(){
        // create a user class for the current user
        db.collection("user")
                .document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(User.class);

                        if (currentUser != null){
                            currentFamilyId = currentUser.getUserSession();

                            if (currentUser.getUserSession()!=null){
                                if (!currentUser.getUserSession().isEmpty()){
                                    memberRequestViewUpdate();

                                }
                            }

                        }

                    }
                });
    }

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

    @Override
    public void onDeclineClick(int position) {
        final String userID = memberRequestsList.get(position).getUserID();

        memberRequestsList.remove(position);
        memberRequestListAdapter.notifyItemRemoved(position);

        db.collection("family_group")
                .document(currentFamilyId)
                .collection("joinRequest")
                .document(userID)
                .delete();

        memberRequestListAdapter.notifyDataSetChanged();
    }

    public void addMemberToFamilyGroup(String userID){
        DocumentReference familyRef = db.collection("family_group")
                .document(currentFamilyId);

        familyRef.collection("members")
                .document(userID)
                .set(
                        new HashMap<String, String>() {{
                            put("exists", "1");
                        }});

        familyRef.collection("joinRequests")
                .document(userID).delete();
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
                        user.setUserID(snapshot.getId());
                        user.setUsername(snapshot.get("username", String.class));
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

    public ArrayList<String> parseQuery(String query){
        String[] splitQuery =  query.split(" ");
        ArrayList<String> parsedQuery = new ArrayList<>();

        for(int i=0; i < splitQuery.length; i++){
            parsedQuery.add(splitQuery[i]);
        }

        return parsedQuery;
    }

    public void acceptUser(String userID) {
        db.collection("user")
                .document(userID)
                .collection("familyGroups")
                .document(currentFamilyId)
                .update("accepted", "1");
    }

    private void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }

    public void setLoading(final boolean isLoading) {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                if (progressBar != null) {
//                    userView.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
//                }
                if (userView != null) {
                    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

}
