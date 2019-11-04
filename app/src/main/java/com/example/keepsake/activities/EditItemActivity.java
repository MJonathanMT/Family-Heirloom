package com.example.keepsake.activities;

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

import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseFamilyAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.Family;
import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.Item;
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

/***
 * This activity page is the page in the application
 * where you can edit the information of the item you're viewing
 * This page is only accessible if you are the current owner of
 * the item you are viewing
 */
public class EditItemActivity extends AppCompatActivity {
    private final String TAG = "Edit Item";

    private ImageButton updateItem;
    private EditText editName;
    private EditText editDescription;
    private Button changeOwnerButton;
    private ImageView imageEditItemPhoto;
    private ImageButton imageButtonUploadImage;
    private ImageButton deleteItemButton;
    private final int IMAGE_REQUEST = 71;
    private Uri mImageUri;

    private Spinner spinnerFamilyGroup;
    private ArrayList<Family> familyList;

    private Spinner spinnerPrivacy;

    private String userID = FirebaseAuthAdapter.getCurrentUserID();
    private String itemID;

    private String itemPrivacy = "O";
    private String familyID;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Intent intent = getIntent();
        itemID = intent.getStringExtra(FirebaseItemAdapter.ID_FIELD);
        Log.d("Item ID", " " + itemID);

        bindViews();
        populatePrivacyLevelSpinner();
        loadItemInfo();
    }

    /***
     * This function pulls data of the current item you are viewing from the fireStore data.
     * It then displays the existing data of the item on the activity page.
     */
    public void loadItemInfo(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                if (snapshot.exists()) {
                    Item item = snapshot.toObject(Item.class);
                    String name = item.getName();
                    Log.d("Name", " "+ name);

                    editName.setText(name);
                    editDescription.setText(item.getDescription());
                    itemPrivacy = item.getPrivacy();
                    familyID = item.getFamilyID();
                    Picasso.get().load(item.getUrl()).into(imageEditItemPhoto);

                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);
                    float scale = (size.x / (imageEditItemPhoto.getWidth()));
                    imageEditItemPhoto.setScaleX(scale);
                    imageEditItemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    int selectedIndex;
                    if (itemPrivacy == FirebaseItemAdapter.PRIVACY_OWNER) {
                        selectedIndex = 0;
                    } else if (itemPrivacy == FirebaseItemAdapter.PRIVACY_FAMILY) {
                        selectedIndex = 1;
                    } else if (itemPrivacy == FirebaseItemAdapter.PRIVACY_PUBLIC) {
                        selectedIndex = 2;
                    } else {
                        selectedIndex = 0;
                    }

                    spinnerPrivacy.setSelection(selectedIndex, true);
                    populateFamilyGroupSpinner();

                }
            }};

        FirebaseItemAdapter.getDocument(this, itemID, listener);
    }

    /***
     * This function connects the three possible privacy settings and populates the privacyAdapter
     * which is then set to be the data for the spinnerPrivacy.
     */
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
        Query query = FirebaseUserAdapter.queryAcceptedFamilies(this, userID);

        familyList = new ArrayList<>();

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null){
                            for (DocumentSnapshot docRef : queryDocumentSnapshots){
                                if (docRef.exists()){
                                    loadFamilyInfo(docRef.getId());
                                }

                            }
                        }
                    }
                });

    }

    public void loadFamilyInfo(String id){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Family family = documentSnapshot.toObject(Family.class);
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
        };

        FirebaseFamilyAdapter.getDocument(this, id, listener);
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

    private void updateItem(){
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

        HashMap<String, String> data = new HashMap<String, String>() {{
            put(FirebaseItemAdapter.NAME_FIELD, newName);
            put(FirebaseItemAdapter.DESCRIPTION_FIELD, newDescription);
            put(FirebaseItemAdapter.PRIVACY_FIELD, itemPrivacy);
            put(FirebaseItemAdapter.FAMILY_ID_FIELD, familyID);
        }};

        FirebaseItemAdapter.updateDocument(this, itemID, data);
        openViewItemActivity();
    }

    /***
     * Allows an user to choose an image via gallery
     */
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
    /***
    * Get the file extension of a file
    * @param uri uniform resource identifies for publicly-accessible resource
    * @return the extension of the file
    */
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

    public void bindViews(){
        editName = findViewById(R.id.editTextEditName);
        editDescription = findViewById(R.id.editTextEditDescription);
        imageEditItemPhoto = findViewById(R.id.imageViewItemPhoto);
        imageButtonUploadImage = findViewById(R.id.imageButtonUploadImage);
        changeOwnerButton = findViewById(R.id.imageButtonChangeOwner);
        updateItem = findViewById(R.id.imageButtonUploadItem);
        spinnerPrivacy = findViewById(R.id.spinnerPrivacy);
        spinnerFamilyGroup = findViewById(R.id.spinnerFamilyGroup);

        deleteItemButton = findViewById(R.id.imageButtonDeleteItem);

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
                updateItem();
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
                FirebaseItemAdapter.deleteItem(EditItemActivity.this, itemID);
                openProfileActivity();
            }
        });
    }


    public void openEditItemPhotoActivity() {
        if(mImageUri != null) {
            String imagePath = System.currentTimeMillis()+"."+getExtension(mImageUri);

            OnSuccessListener listener = new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    final String imageURL = Objects.requireNonNull(uri).toString();

                    HashMap<String, String> data = new HashMap<String, String>() {{
                        put(FirebaseItemAdapter.URL_FIELD, imageURL);
                    }};

                    FirebaseItemAdapter.updateDocument(EditItemActivity.this, itemID, data);
                    DocumentReference reference = FirebaseFirestore.getInstance().collection("item").document(itemID);

                    openViewItemActivity();
                }
            };

            FirebaseItemAdapter.uploadImage(this, mImageUri, imagePath, listener);
        }
        else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }


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
        Intent intent = new Intent(this, ChangeItemOwnerActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
        startActivity(intent);
    }

    public void openViewItemActivity() {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public final void openViewItemActivity(String itemID) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FirebaseItemAdapter.ID_FIELD, itemID);
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
