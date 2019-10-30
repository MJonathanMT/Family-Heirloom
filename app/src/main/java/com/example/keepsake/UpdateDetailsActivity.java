package com.example.keepsake;

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

import com.example.keepsake.memberList.FamilyMemberPageActivity;
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

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User currentUser;
    private String userId;
    private FirebaseFirestore db;



    Button buttonSaveChanges, buttonDelete;
    FirebaseUser firebaseUser;
    EditText firstname, lastname, email;
    TextView tv_change;
    ImageView imgView;
    Uri mImageUri;
    StorageTask uploadTask;
    FirebaseFirestore mFirebaseFirestore;
    StorageReference storageReference;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);
        getUserId();
        createUserClass();
        createNavBar();
        manageButtons();

        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonDelete = findViewById(R.id.button_to_delete_account);
        firstname = findViewById(R.id.account_firstname);
        lastname = findViewById(R.id.account_lastname);
        email = findViewById(R.id.account_email);
        imgView = findViewById(R.id.imageView_profile_picture);
        tv_change = findViewById(R.id.tv_change);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("user");

        DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(firebaseUser).getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                firstname.setText(user.getFirstName());
                lastname.setText(user.getLastName());
                email.setText(user.getEmail());
                Picasso.get().load(user.getUrl()).into(imgView);
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(UpdateDetailsActivity.this);
            }
        });
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
                updateProfile(firstname.getText().toString(),
                        lastname.getText().toString(),
                        email.getText().toString());
                openHomePageActivity();
            }
        });
    }

    private void updateProfile(String firstname, String lastname, String email) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("firstName", firstname);
        hashMap.put("lastName", lastname);
        hashMap.put("email", email);

        reference.update(hashMap);
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
                        DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("url", imageURL);

                        reference.update(hashMap);
                        startActivity(new Intent(getApplicationContext(), UpdateDetailsActivity.class));
                    }
                    else {
                        Toast.makeText(UpdateDetailsActivity.this, "Failed to update", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

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

    private void openHomePageActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.updateDetailsDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        db.collection("user")
                .document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView displayMessage = findViewById(R.id.user_header_welcome_message);
                ImageView displayProfilePicture = findViewById(R.id.user_header_profile_picture);

                // Prints the name of the user session base on id of the view
                displayMessage.setText(currentUser.getFirstName()+ " " + currentUser.getLastName());
                Picasso.get().load(user.getUrl()).into(displayProfilePicture);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Details");
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
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(UpdateDetailsActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }

    private void createUserClass(){
        // create a user class for the current user
        db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null){
                            currentUser.setUserID(documentSnapshot.getId());
                            Log.d("Current Id", documentSnapshot.getId());
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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

    private void manageButtons(){

        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openAccountSettingsActivity();
            }
        });
    }

    private void openAccountSettingsActivity() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

}