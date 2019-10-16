package com.example.keepsake;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    Button buttonSaveChanges, buttonDelete;
    FirebaseUser firebaseUser;
    EditText firstname, lastname, email;
    TextView tv_change;
    ImageView imgView;
    Uri mImageUri;
    StorageTask uploadTask;
    FirebaseFirestore mFirebaseFirestore;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

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
            }
        });
    }

    private void updateProfile(String firstname, String lastname, String email) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("firstname", firstname);
        hashMap.put("lastname", lastname);
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
}