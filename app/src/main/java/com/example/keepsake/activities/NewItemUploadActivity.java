package com.example.keepsake.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Family;
import com.example.keepsake.database.firebaseSnapshot.Item;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewItemUploadActivity extends AppCompatActivity {
    private final String TAG = "Upload Item";

    private Button btnupload;
    private ImageView img;
    private TextInputEditText itemName, itemDescription;
    private Spinner spinnerFamilyGroup, spinnerPrivacy;

    private Uri filePath;
    final int IMAGE_REQUEST = 71;
    private StorageTask mUploadTask;
    private String userID = FirebaseAuthAdapter.getCurrentUserID();
    private String itemPrivacy;
    private String familyID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_upload);

        bindViews();
        populateFamilyGroupSpinner();
        populatePrivacyLevelSpinner();
    }

    public void bindViews(){
        btnupload = findViewById(R.id.uploadBtn);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.textViewDescription);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        spinnerFamilyGroup = findViewById(R.id.spinnerFamilyGroup);

        img = findViewById(R.id.uploadImageView);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadItem();
                openUserProfileActivity();
            }
        });
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
        final ArrayList<Family> familyList = new ArrayList<>();

        OnSuccessListener listener = new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot docRef : queryDocumentSnapshots){
                    String familyID = docRef.getId();

                    OnSuccessListener familyListener = new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                Family family = documentSnapshot.toObject(Family.class);
                                family.setFamilyID((documentSnapshot.getId()));
                                familyList.add(family);

                                if (familyList.size() == 1){
                                    createFamilySpinner(familyList);
                                }
                            }
                        }
                    };

                    FirebaseFamilyAdapter.getDocument(NewItemUploadActivity.this, familyID, familyListener);
                }
            }
        };

        FirebaseUserAdapter.getAcceptedFamilies(this, userID, listener);

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
                familyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setCurrentUserSession();
    }

    private void setCurrentUserSession(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String userSession = documentSnapshot.get(FirebaseUserAdapter.USER_SESSION_FIELD, String.class);
                    int position = ((ArrayAdapter)spinnerFamilyGroup.getAdapter()).getPosition(new Family("", userSession));
                    spinnerFamilyGroup.setSelection(position, true);
                }
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    private void setFamilyID(String familyID){
        this.familyID = familyID;
    }

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

    private void uploadItem() {
        final String name = itemName.getText().toString().trim();
        final String description = itemDescription.getText().toString().trim();
        final String privacy = spinnerPrivacy.getSelectedItem().toString().substring(0, 1);
        final SimpleDateFormat timeStamp = new SimpleDateFormat(FirebaseItemAdapter.DATE_FORMAT);

        if(filePath != null) {
                String imageName = System.currentTimeMillis()+"."+getExtension(filePath);

                OnSuccessListener imageUploadListener = new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String url = Objects.requireNonNull(uri).toString();

                        Item item = new Item(
                                name,
                                description,
                                privacy,
                                userID,
                                familyID,
                                url,
                                timeStamp.format(new Date()).trim()
                        );

                        OnSuccessListener itemUploadListener = new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference docRef) {
                                item.setItemID(docRef.getId());
                                addOwnershipRecord(item);
                                addToUserItemsCollection(item.getItemID());
                                openUserProfileActivity();
                            }
                        };

                        FirebaseItemAdapter.createDocument(NewItemUploadActivity.this, itemUploadListener, item);
                    }
                };

                FirebaseItemAdapter.uploadImage(this, filePath, imageName, imageUploadListener);
            }

        else {
                Toast.makeText(NewItemUploadActivity.this, "Please fill in the input", Toast.LENGTH_LONG).show();
            }
    }

    public void addOwnershipRecord(Item item){
        Map<String, String> data = new HashMap<String, String>() {{
            put(FirebaseItemAdapter.OWNER_ID_FIELD, item.getOwnerID());
            put(FirebaseItemAdapter.START_DATE_FIELD, item.getStartDate());
            put(FirebaseItemAdapter.MEMORY_FIELD, item.getDescription());
            put(FirebaseItemAdapter.PRIVACY_FIELD, item.getPrivacy());
            put(FirebaseItemAdapter.FAMILY_ID_FIELD, item.getFamilyID());
        }};

        FirebaseItemAdapter.createOwnershipRecordDocument(this, item.getItemID(), data);
    }

    public void addToUserItemsCollection(String itemID){
        Map<String, String> data = new HashMap<String, String>() {{
            put(FirebaseItemAdapter.EXISTS_FIELD, "1");
        }};

        FirebaseUserAdapter.createItemDocument(this, userID, itemID, data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    img.setImageBitmap(bitmap);
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

    public void openUserProfileActivity(){
        startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));

    }
}