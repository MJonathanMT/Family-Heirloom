package com.example.keepsake.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

public class UpdateDetailsActivity extends AppCompatActivity {
    private final String TAG = "Update Profile";

    private String userID = FirebaseAuthAdapter.getCurrentUserID();
    private User currentUser;

    private Button buttonSaveChanges, buttonDelete, buttonSettings;
    private EditText editTextFirstName, editTextLastName, editTextEmail;
    private TextView textViewChange;
    private ImageView imgView;
    private Uri mImageUri;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        // Loads user information into page
        loadUserInfo();
        createUserClass();
        createNavBar();
        bindViews();
    }

    public void loadUserInfo(){
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                editTextFirstName.setText(user.getFirstName());
                editTextLastName.setText(user.getLastName());
                editTextEmail.setText(user.getEmail());
                Picasso.get().load(user.getUrl()).into(imgView);
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    // Updates user profile information
    private void updateProfile(String firstname, String lastname, String email) {
        HashMap<String, String> data = new HashMap<String, String>() {{
            put(FirebaseUserAdapter.FIRST_NAME_FIELD, firstname);
            put(FirebaseUserAdapter.LAST_NAME_FIELD, lastname);
            put(FirebaseUserAdapter.EMAIL_FIELD, email);
        }};

        FirebaseUserAdapter.updateDocument(this, userID, data);
    }

    // Get file extension of the file selected
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

    // Updates user profile photo
    private void uploadImage() {
        if(mImageUri != null) {
            String imagePath = System.currentTimeMillis()+"."+getExtension(mImageUri);

            OnSuccessListener listener = new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    final String imageURL = Objects.requireNonNull(uri).toString();
                    FirebaseUserAdapter.updateDocument(UpdateDetailsActivity.this, userID, FirebaseUserAdapter.URL_FIELD, imageURL);

                    startActivity(new Intent(getApplicationContext(), UpdateDetailsActivity.class));
                    finish();
                }
            };

            FirebaseUserAdapter.uploadImage(this, mImageUri, imagePath, listener);
        }
        else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    // Activity to check result of uploading
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            uploadImage();
        }
        else {
            Toast.makeText(this, "Can't get result", Toast.LENGTH_LONG).show();
        }
    }

    private void createUserClass(){
        // create a user class for the current user
        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                if (currentUser != null){
                    currentUser.setUserID(documentSnapshot.getId());
                }

            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void bindViews(){

        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonDelete = findViewById(R.id.button_to_delete_account);
        editTextFirstName = findViewById(R.id.account_firstname);
        editTextLastName = findViewById(R.id.account_lastname);
        editTextEmail = findViewById(R.id.account_email);
        imgView = findViewById(R.id.imageView_profile_picture);
        textViewChange = findViewById(R.id.tv_change);
        buttonSettings = findViewById(R.id.buttonSettings);

        // Allows user to choose and change profile photo
        textViewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(UpdateDetailsActivity.this);
            }
        });

        // Image view for user profile photo
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(UpdateDetailsActivity.this);
            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(editTextFirstName.getText().toString(),
                        editTextLastName.getText().toString(),
                        editTextEmail.getText().toString());
                openHomePageActivity();
                finish();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSuccessListener listener = new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateDetailsActivity.this, "User account deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                };

                FirebaseAuthAdapter.deleteUser(listener);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openAccountSettingsActivity();
            }
        });
    }

    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.updateDetailsDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        OnSuccessListener listener = new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView displayMessage = findViewById(R.id.user_header_welcome_message);
                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_picture);

                // Prints the name of the user session base on id of the view
                displayMessage.setText(currentUser.getFirstName()+ " " + currentUser.getLastName());
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
            }
        };

        FirebaseUserAdapter.getDocument(this, userID, listener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonFamilyItemsAccess) {
                    openViewFamilyItemsActivity();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openFamilyMemberPageActivity();
                } else if (id == R.id.ButtonProfileAccess) {
                    openProfileActivity();
                } else if (id == R.id.ButtonLogOutAccess) {
                    FirebaseAuthAdapter.signOut();
                    Toast.makeText(UpdateDetailsActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    private void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    private void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    // Redirect user to user profile page
    private void openHomePageActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

}