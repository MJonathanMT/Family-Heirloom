//package com.example.keepsake;
//
//import android.app.Dialog;
//import android.app.ListActivity;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.content.Intent;
//import java.lang.String;
//
//import android.app.SearchManager;
//import android.widget.ImageView;
//import android.widget.ListAdapter;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.firebase.ui.firestore.SnapshotParser;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class UserSearchActivity extends ListActivity {
//    private FirebaseFirestore db;
//    private UsersViewHolder userHolder;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.search);
//        initialiseDB();
//
//        // Get the intent, verify the action and get the query
//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            ArrayList<String> parsedQuery = parseQuery(query);
//            searchUsers(parsedQuery);
//        }
//    }
//
//
//
//
//
//
//    public void initialiseDB() {
//        FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
//    }
//
//
//}
