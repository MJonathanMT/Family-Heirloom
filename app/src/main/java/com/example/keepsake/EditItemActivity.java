package com.example.keepsake;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private ImageButton updateItem;
    private EditText editName;
    private EditText editDescription;
    private Button changeOwnerButton;
    private ImageView imageEditItemPhoto;
    private ImageButton imageButtonUploadImage;
    private ImageButton deleteItemButton;
    private final int IMAGE_REQUEST = 71;
    private Uri mImageUri;
    private StorageReference storageReference;

    private Spinner spinnerFamilyGroup;
    private ArrayList<Family> familyList;

    private Spinner spinnerPrivacy;

    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String itemID;

    private String itemPrivacy = "O";
    private String familyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        initialiseDB();

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");

        editName = findViewById(R.id.editTextEditName);
        editDescription = findViewById(R.id.editTextEditDescription);
        imageEditItemPhoto = findViewById(R.id.imageViewItemPhoto);
        imageButtonUploadImage = findViewById(R.id.imageButtonUploadImage);
        changeOwnerButton = findViewById(R.id.imageButtonChangeOwner);
        updateItem = findViewById(R.id.imageButtonUploadItem);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        spinnerFamilyGroup = findViewById(R.id.spinnerFamilyGroup);

        deleteItemButton = findViewById(R.id.imageButtonDeleteItem);

        storageReference = FirebaseStorage.getInstance().getReference("item");

        final DocumentReference docRef = db.collection("item")
                .document(itemID);

        populatePrivacyLevelSpinner();
        loadItemInfo(docRef);

        imageEditItemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        imageButtonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem(docRef);
            }
        });

        changeOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeOwnerActivity();
            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("deleteing Item", "item being deleted");
                deleteItem();
//                openProfileActivity();
            }
        });
    }

    public void loadItemInfo(DocumentReference docRef){

        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Task completed successfully
                            final DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                String familyName = document.get("name", String.class);
                                editName.setText(familyName);
                                editDescription.setText(document.get("description", String.class));
                                itemPrivacy = document.get("privacy", String.class);
                                familyID = document.get("familyID", String.class);
                                Picasso.get().load(document.get("url", String.class)).into(imageEditItemPhoto);

                                Point size = new Point();
                                getWindowManager().getDefaultDisplay().getSize(size);
                                float scale = (size.x / (imageEditItemPhoto.getWidth()));
                                imageEditItemPhoto.setScaleX(scale);
                                imageEditItemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                int selectedIndex;
                                if (itemPrivacy == "O"){
                                    selectedIndex = 0;
                                } else if (itemPrivacy == "F"){
                                    selectedIndex = 1;
                                } else if (itemPrivacy == "P") {
                                    selectedIndex = 2;
                                } else {
                                    selectedIndex = 0;
                                }

                                spinnerPrivacy.setSelection(selectedIndex, true);
                                populateFamilyGroupSpinner();

                            } else {
                                //TODO(naverill) notify user of failure
                            }
                        } else {
                            // Task failed with an exception
                            Exception exception = task.getException();
                            Toast.makeText(EditItemActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );
    }

    public void populatePrivacyLevelSpinner(){
        ArrayAdapter<String> privacyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.privacyLevels));
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String level = spinnerPrivacy.getSelectedItem().toString();
                itemPrivacy = level.substring(0, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerPrivacy.setAdapter(privacyAdapter);
    }

    public void populateFamilyGroupSpinner(){
        Query query = db.collection("user")
                .document(userID)
                .collection("familyGroups")
                .whereEqualTo("accepted", "1");

        familyList = new ArrayList<>();

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    for (DocumentSnapshot docRef : queryDocumentSnapshots){
                        if (docRef.exists()){
                            String id = docRef.getId();
                            loadFamilyInfo(id);
                        }

                    }
                }
            }
        });

    }

    public void loadFamilyInfo(String id){
        db.collection("family_group")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Family family = new Family();
                            family.setFamilyName(documentSnapshot.get("familyName", String.class));
                            family.setFamilyID((documentSnapshot.getId()));
                            familyList.add(family);

                            if (familyList.size() == 1){
                                createFamilySpinner(familyList);
                            }

                            int position = ((ArrayAdapter)spinnerFamilyGroup.getAdapter()).getPosition(new Family("", familyID));
                            if (position != -1){
                                spinnerFamilyGroup.setSelection(position, true);

                            }
                        }
                    }
                });
    }

    public void createFamilySpinner(ArrayList<Family> familyList){
        ArrayAdapter<Family> familyAdapter = new ArrayAdapter<Family>(this, R.layout.family_list_layout, familyList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            public View getCustomView(int position, View convertView, ViewGroup parent){
                View spinner = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_list_layout, parent, false);
                TextView familyName = spinner.findViewById(R.id.textViewFamilyName);
                TextView familyID = spinner.findViewById(R.id.textViewFamilyID);
                TextView buttonJoin = spinner.findViewById(R.id.buttonJoin);

                buttonJoin.setVisibility(View.GONE);

                familyName.setText(getItem(position).getFamilyName());
                familyID.setText(getItem(position).getFamilyID());
                spinner.setScaleX((float)0.75);
                spinner.setScaleY((float)0.75);
                return spinner;
            }

            @Override
            public int getPosition(Family item) {
                int i;
                for (i=0; i < getCount(); i++){
                    if (getItem(i).getFamilyID().compareTo(item.getFamilyID()) == 0){
                        return i;
                    }
                }
                return -1;
            }
        };

        familyAdapter.setDropDownViewResource(R.layout.family_list_layout);

        spinnerFamilyGroup.setAdapter(familyAdapter);
        spinnerFamilyGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFamilyID = ((Family) spinnerFamilyGroup.getSelectedItem()).getFamilyID();
                setFamilyID(selectedFamilyID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setFamilyID(String familyID){
        this.familyID = familyID;
    }

    private void updateItem(final DocumentReference docRef){
        final String newName = editName.getText().toString().trim();
        final String newDescription = editDescription.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(EditItemActivity.this, "Please Enter Item Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newDescription)) {
            Toast.makeText(EditItemActivity.this, "Please Enter Item Description", Toast.LENGTH_SHORT).show();
            return;
        }

        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            Map<String, String> data = new HashMap<String, String>() {{
                                    put("name", newName);
                                    put("description", newDescription);
                                    put("privacy", itemPrivacy);
                                    put("familyID", familyID);
                                }};

                            docRef.set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditItemActivity.this, "Upload successsful", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditItemActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            // Task failed with an exception
                            Exception exception = task.getException();
                        }
                    }
                });
        openViewItemActivity();
    }

    private void deleteItem(){
        // delete item from all users
        Log.d("item id is:", itemID);
        db.collection("item").document(itemID).collection("ownership_record")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String currentUser = document.getId();
                                Log.d("success", "removing item from " + currentUser);
                                db.collection("user").document(currentUser).collection("items").document(itemID).delete();
                                db.collection("item").document(itemID).collection("ownership_record").document(currentUser).delete();
                            }
                            db.collection("item").document(itemID).delete();
                            openProfileActivity();
                        } else {
                            Log.d("fail", "Error getting documents: ", task.getException());
                        }
                    }
                });




    }

    // TODO KIREN
    private void chooseImage() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    // TODO KIREN
    private String getExtension(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }
        catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    // TODO(KIREN)
    public void openEditItemPhotoActivity() {
        if(mImageUri != null) {
            String imgName = System.currentTimeMillis()+"."+getExtension(mImageUri);
            final StorageReference imageRef = storageReference.child(imgName);
            UploadTask uploadTask = imageRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        final String imageURL = Objects.requireNonNull(task.getResult()).toString();
                        DocumentReference reference = FirebaseFirestore.getInstance().collection("item").document(itemID);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("url", imageURL);

                        reference.update(hashMap);
                        openViewItemActivity();
                    }
                    else {
                        Toast.makeText(EditItemActivity.this, "Failed to update", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    // TODO KIREN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                mImageUri = data.getData();
                openEditItemPhotoActivity();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    imageEditItemPhoto.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    public void openChangeOwnerActivity(){
        Intent intent = new Intent(this, ChangeItemOwnerActivity
                .class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

    public void openViewItemActivity() {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

    public final void openViewItemActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("itemId", itemID);
        startActivity(intent);
    }

    public class FamilySpinner extends AppCompatSpinner {
        AdapterView.OnItemSelectedListener listener;

        public FamilySpinner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setSelection(int position) {
            super.setSelection(position);
            if (listener != null)
                listener.onItemSelected(null, null, position, 0);
        }

        public void setOnItemSelectedEvenIfUnchangedListener(
                OnItemSelectedListener listener) {
            this.listener = listener;
        }
    }
}
