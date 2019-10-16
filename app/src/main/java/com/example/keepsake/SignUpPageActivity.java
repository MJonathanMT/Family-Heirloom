package com.example.keepsake;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class SignUpPageActivity extends AppCompatActivity {

    ImageView mUserImage;
    EditText mFirstname, mLastname, mEmail, mPassword, mConfirmPassword;
    Button buttonFamilySetup;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    FirebaseFirestore mFirebaseFirestore;
    Uri filePath;
    String imageURL;
    final int IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        mUserImage = findViewById(R.id.userImageView);
        mFirstname = findViewById(R.id.mFirstname);
        mLastname = findViewById(R.id.mLastname);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        mConfirmPassword = findViewById(R.id.mConfirmPassword);
        buttonFamilySetup = findViewById(R.id.buttonFamilySetup);

        mStorageRef = FirebaseStorage.getInstance().getReference("user");
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonFamilySetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                final String firstname = mFirstname.getText().toString().trim();
                final String lastname = mLastname.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                // Check if image view is empty
                if (filePath == null) {
                    Toast.makeText(SignUpPageActivity.this, "Please Select An Image", Toast.LENGTH_LONG).show();
                }

                // Check if first name input is empty
                if (TextUtils.isEmpty(firstname)) {
                    Toast.makeText(SignUpPageActivity.this, "Please Enter First Name", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if last name input is empty
                if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(SignUpPageActivity.this, "Please Enter Full Name", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if email input is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpPageActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if valid email pattern
                if (!email.matches(emailPattern)){
                    Toast.makeText(getApplicationContext(),"Invalid Email address", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if password input is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpPageActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if confirm password input is empty
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpPageActivity.this, "Please Enter Confirm Password", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if password length is longer than 7 characters
                if (password.length() < 6) {
                    Toast.makeText(SignUpPageActivity.this, "Password too short", Toast.LENGTH_LONG).show();
                }

                // Check if confirm password matches password entered
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpPageActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
                }

                // If all the inputs are correct and valid, will create new user entry into database
                if (password.equals(confirmPassword) && filePath != null) {
                    String imgName = System.currentTimeMillis() + "." + getExtension(filePath);
                    final StorageReference imageRef = mStorageRef.child(imgName);
                    UploadTask mUploadTask = imageRef.putFile(filePath);
                    mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String url = Objects.requireNonNull(task.getResult()).toString();
                                imageURL = url;
                            } else if (!task.isSuccessful()) {
                                Toast.makeText(SignUpPageActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpPageActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    User user = new User(
                                            firstname,
                                            lastname,
                                            email,
                                            imageURL
                                    );
                                    FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                            .set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(getApplicationContext(), FamilySetupActivity.class));
                                                        Toast.makeText(SignUpPageActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpPageActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
            }
        });

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    mUserImage.setImageBitmap(bitmap);
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
