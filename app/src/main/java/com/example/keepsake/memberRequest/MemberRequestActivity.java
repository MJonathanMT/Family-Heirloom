package com.example.keepsake.memberRequest;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.User;
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
    private FirebaseFirestore fbfs;
    private MemberRequestListAdapter memberRequestListAdapter;
    private List<MemberRequests> memberRequestsList;
    private User currentUser;
    private String userId;
    private String currentFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_request);

        fbfs = FirebaseFirestore.getInstance();
        getUserId();
        createUserClass();


        memberRequestsList = new ArrayList<>();
        memberRequestListAdapter = new MemberRequestListAdapter(memberRequestsList, this);

        posts = findViewById(R.id.request_list);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(memberRequestListAdapter);



    }
    private void memberRequestViewUpdate(){
        fbfs.collection("family_group").document(currentFamilyId).collection("joinRequest").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    MemberRequests memberRequests  = doc.getDocument().toObject(MemberRequests.class);

                    Log.d("UMM BEFORE ADDING", memberRequests.getName());
                    if (doc.getType() ==  DocumentChange.Type.ADDED) {
                        memberRequests.setUserId(doc.getDocument().getId());
                        memberRequestsList.add(memberRequests);
                        memberRequestListAdapter.notifyDataSetChanged();

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
        DocumentReference docUser = fbfs.collection("user").document(userId);
        docUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("hah", "starting update view");
                currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                currentFamilyId = currentUser.getUserSession();
                Log.d("OH YEA", currentFamilyId);
                memberRequestViewUpdate();
            }
        });
    }

    @Override
    public void onAcceptClick(int position) {
        // Add current userId to family_group's members
        Map<String, Object> newMember = new HashMap<>();
        newMember.put("name", memberRequestsList.get(position).getName());
        final String newId = memberRequestsList.get(position).getUserId();

        fbfs.collection("family_group").document(currentFamilyId).collection("members").document(newId).set(newMember);

        // Add family_group's name to userId's family_names
        final String[] familyName = new String[1];
        DocumentReference docRef = fbfs.collection("family_group").document(currentFamilyId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    familyName[0] = (String) document.get("name");
                    Log.d("new group name", familyName[0] + document.get("name"));Map<String, Object> newGroup = new HashMap<>();

                    Log.d("adding new name", String.valueOf(familyName));
                    newGroup.put("name",familyName[0]);

                    fbfs.collection("user").document(newId).collection("familyNames").document(currentFamilyId).set(newGroup);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        memberRequestsList.remove(position);
        fbfs.collection("family_group").document(currentFamilyId).collection("joinRequest").document(newId).delete();
        memberRequestListAdapter.notifyDataSetChanged();
        memberRequestListAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onDeclineClick(int position) {
        memberRequestsList.remove(position);
        memberRequestListAdapter.notifyItemRemoved(position);
        final String newId = memberRequestsList.get(position).getUserId();
        fbfs.collection("family_group").document(currentFamilyId).collection("joinRequest").document(newId).delete();
        memberRequestListAdapter.notifyDataSetChanged();
    }

}
