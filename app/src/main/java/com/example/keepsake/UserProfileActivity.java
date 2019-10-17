package com.example.keepsake;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class UserProfileActivity extends AppCompatActivity implements ItemsListAdapter.OnNoteListener {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private static final String TAG = "FireLog";
    private RecyclerView posts;
    private FirebaseFirestore db;
    private ItemsListAdapter itemsListAdapter;
    private List<Item> itemList;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();


        DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(user).getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView displayName = findViewById(R.id.user_profile_name);
                ImageView displayProfilePicture = findViewById(R.id.user_profile_image);

                // Prints the name of the user session base on id of the view
                displayName.setText(user.getFirstName() +" "+ user.getLastName());
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);

            }
        });

        homeItemViewing();
        getUserId();
    }

    private void homeItemViewing(){
        itemList = new ArrayList<>();
        itemsListAdapter = new ItemsListAdapter(itemList, this);

        posts = findViewById(R.id.profile_page_recycleView);
        posts.setHasFixedSize(true);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setAdapter(itemsListAdapter);

        // get all the items relevant to the current user
        db.collection("item").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                assert queryDocumentSnapshots != null;
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() ==  DocumentChange.Type.ADDED) {
                        if (doc.getDocument().exists()){
                            Item item = doc.getDocument().toObject(Item.class);
                            if(item.getOwner()!=null && userId.compareTo(item.getOwner()) == 0) {
                                itemList.add(item);
                                itemsListAdapter.notifyDataSetChanged();
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
        }
    }

    @Override
    public void onNoteClick(int position) {

    }
}
