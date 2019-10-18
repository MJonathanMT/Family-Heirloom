package com.example.keepsake.memberRequest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.User;
import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MemberRequestActivity extends AppCompatActivity implements MemberRequestListAdapter.OnNoteListener{

    private static final String TAG = "FireLog";
    private RecyclerView posts;
    private ImageButton imageButtonExit;
    private FirebaseFirestore db;
    private MemberRequestListAdapter memberRequestListAdapter;
    private List<MemberRequests> memberRequestsList;
    private User currentUser;
    private String userId;
    private String currentFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_request);

        imageButtonExit = findViewById(R.id.imageButtonExit);

        db = FirebaseFirestore.getInstance();
        getUserId();
        createUserClass();


        memberRequestsList = new ArrayList<>();
        memberRequestListAdapter = new MemberRequestListAdapter(memberRequestsList, this);

        posts = findViewById(R.id.request_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(memberRequestListAdapter);

        imageButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFamilyMemberPageActivity();
            }
        });
    }

    private void memberRequestViewUpdate(){
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
                                    MemberRequests memberRequests  = doc.getDocument().toObject(MemberRequests.class);

                                    if (doc.getType() ==  DocumentChange.Type.ADDED) {
                                        memberRequests.setUserId(doc.getDocument().getId());
                                        memberRequestsList.add(memberRequests);
                                        memberRequestListAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
        });
    }

    private void getUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            Log.d("User ID", userId);
        }
    }

    private void createUserClass(){
        // create a user class for the current user
        DocumentReference docUser = db.collection("user").document(userId);
        docUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                if (currentUser != null){
                    currentFamilyId = currentUser.getUserSession();

                    if (!currentUser.getUserSession().isEmpty()){
                        memberRequestViewUpdate();

                    }
                }

            }
        });
    }

    @Override
    public void onAcceptClick(int position) {
        // Add current userId to family_group's members
        final String userID = memberRequestsList.get(position).getUserId();

        addMemberToFamilyGroup(userID);
        acceptUser(userID);

        memberRequestsList.remove(position);

        memberRequestListAdapter.notifyDataSetChanged();
        memberRequestListAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onDeclineClick(int position) {
        memberRequestsList.remove(position);
        memberRequestListAdapter.notifyItemRemoved(position);

        final String userID = memberRequestsList.get(position).getUserId();

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

}
