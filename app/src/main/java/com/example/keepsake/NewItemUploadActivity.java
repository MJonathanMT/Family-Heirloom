package com.example.keepsake;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class NewItemUploadActivity extends AppCompatActivity {

    Button btnbrowse, btnupload;
    ImageView img;
    TextInputEditText itemName, itemDescription;
    Spinner familyName, privacy;
    StorageReference mStorageRef;
    FirebaseFirestore mFirebaseFirestore;
    FirebaseUser mUser;
    Uri filePath;
    final int IMAGE_REQUEST = 71;
    StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_upload);

        btnbrowse = findViewById(R.id.chooseBtn);
        btnupload = findViewById(R.id.uploadBtn);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        privacy = findViewById(R.id.spinnerPrivacy);
        familyName = findViewById(R.id.familyNames);
        mStorageRef = FirebaseStorage.getInstance().getReference("item");
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        img = findViewById(R.id.uploadImageView);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        btnbrowse.setOnClickListener(new View.OnClickListener() {
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

        ArrayAdapter<String> familyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.familyNames));
        familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familyName.setAdapter(familyAdapter);

        ArrayAdapter<String> privacyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.privacyLevels));
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privacy.setAdapter(privacyAdapter);
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
        final String itemPrivacy = privacy.getSelectedItem().toString().trim();
        final String itemFamilyName = familyName.getSelectedItem().toString().trim();
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
                                    itemFamilyName,
                                    itemPrivacy,
                                    mUser.getUid(),
                                    description,
                                    url,
                                    timeStamp.format(new Date()).toString().trim()
                            );
                            mFirebaseFirestore.collection("item").document()
                                    .set(upload)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(NewItemUploadActivity.this, "Images is uploaded", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
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