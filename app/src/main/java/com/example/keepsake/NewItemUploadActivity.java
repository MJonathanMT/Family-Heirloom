package com.example.keepsake;

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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;

public class NewItemUploadActivity extends AppCompatActivity {
    private Button btnupload;
    private ImageView img;
    private TextInputEditText itemName, itemDescription;
    private Spinner spinnerFamilyGroup, spinnerPrivacy;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    private Uri filePath;
    final int IMAGE_REQUEST = 71;
    private StorageTask mUploadTask;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String itemPrivacy;
    private String familyID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_upload);

        //btnbrowse = findViewById(R.id.chooseBtn);
        btnupload = findViewById(R.id.uploadBtn);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        spinnerFamilyGroup = findViewById(R.id.spinnerFamilyGroup);
        mStorageRef = FirebaseStorage.getInstance().getReference("item");
        db = FirebaseFirestore.getInstance();
        img = findViewById(R.id.uploadImageView);

        populateFamilyGroupSpinner();
        populatePrivacyLevelSpinner();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null) {
                    Toast.makeText(NewItemUploadActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();
                }
                else{
                    uploadImage();
                }
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
        Query query = db.collection("user")
                .document(userID)
                .collection("familyGroups")
                .whereEqualTo("accepted", "1");

        final ArrayList<Family> familyList = new ArrayList<>();

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot docRef : queryDocumentSnapshots){
                    String familyID = docRef.getId();

                    db.collection("family_group")
                            .document(familyID)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Family family = new Family();
                                    family.setFamilyName(documentSnapshot.get("familyName", String.class));
                                    family.setUUID((documentSnapshot.getId()));
                                    familyList.add(family);

                                    if (familyList.size() == 1){
                                        createFamilySpinner(familyList);
                                    }
                                }
                            });
                }
            }
        });

    }

    public void createFamilySpinner(ArrayList<Family> familyList){
        Log.d("LIST", " "+ String.valueOf(familyList.size()));
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
                familyID.setText(getItem(position).getUUID());
                spinner.setScaleX((float)0.75);
                spinner.setScaleY((float)0.75);
                return spinner;
            }

            @Override
            public int getPosition(Family item) {
                int i;
                for (i=0; i < getCount(); i++){
                    if (getItem(i).getUUID().compareTo(item.getUUID()) == 0){
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
                String selectedFamilyID = ((Family) spinnerFamilyGroup.getSelectedItem()).getUUID();
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
        db.collection("user")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String userSession = documentSnapshot.get("userSession", String.class);
                            int position = ((ArrayAdapter)spinnerFamilyGroup.getAdapter()).getPosition(new Family("", userSession));
                            spinnerFamilyGroup.setSelection(position, true);
                        }
                    }
                });
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

    private void uploadImage() {
        final String name = itemName.getText().toString().trim();
        final String description = itemDescription.getText().toString().trim();
        final String privacy = spinnerPrivacy.getSelectedItem().toString().substring(0, 1);
        //todo(naverill) make family spinner work properly
        final SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMddhhmmss");

        try {
            if(filePath != null) {
                String imgName = System.currentTimeMillis()+"."+getExtension(filePath);
                final StorageReference imageRef = mStorageRef.child(imgName);
                UploadTask mUploadTask = imageRef.putFile(filePath);

                mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            final String url = Objects.requireNonNull(task.getResult()).toString();
                            UploadInfo upload = new UploadInfo(
                                    name,
                                    familyID,
                                    privacy,
                                    userID,
                                    description,
                                    url,
                                    timeStamp.format(new Date()).toString().trim()
                            );

                            db.collection("item").document()
                                    .set(upload)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(NewItemUploadActivity.this, "Images is uploaded", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewItemUploadActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else if(!task.isSuccessful()) {
                            Toast.makeText(NewItemUploadActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            else {
                Toast.makeText(NewItemUploadActivity.this, "Please fill in the input", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            Toast.makeText(NewItemUploadActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
}