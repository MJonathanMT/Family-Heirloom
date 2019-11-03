package com.example.keepsake.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseItemAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseUserAdapter;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private final String TAG = "Sign Up";

    private ImageView mUserImage;
    private EditText mFirstname, mLastname, mEmail, mPassword, mConfirmPassword;
    private Button buttonFamilySetup;
    private Uri filePath;
    private String imageURL;
    public final int IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        bindViews();
    }

    private void uploadUserDetails(String firstName, String lastName, String email, String password, String confirmPassword){

        // If all the inputs are correct and valid, will create new user entry into database
        if (validateInput(firstName, lastName, email, password, confirmPassword)) {
            if (filePath != null) {
                String imagePath = System.currentTimeMillis() + "." + getExtension(filePath);

                OnSuccessListener uploadImageListener = new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = Objects.requireNonNull(uri).toString();
                        imageURL = url;
                    }
                };

                FirebaseUserAdapter.uploadImage(SignUpPageActivity.this, filePath, imagePath, uploadImageListener);

                OnSuccessListener createUserListener = new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        // Sign in success, update UI with the signed-in user's information
                        String username = email.substring(0, email.indexOf("@"));
                        User data = new User(
                                firstName,
                                lastName,
                                email,
                                imageURL,
                                username
                        );

                        FirebaseUserAdapter.createDocument(SignUpPageActivity.this, FirebaseAuthAdapter.getCurrentUserID(), data);

                        openFamilySetupActivity();
                    }
                };

                FirebaseAuthAdapter.createUser(email, password, createUserListener);
            }
        }
    }

    private boolean validateInput(String firstName, String lastName, String email, String password, String confirmPassword){
        // Check if image view is empty
        if (filePath == null) {
            Toast.makeText(SignUpPageActivity.this, "Please Select An Image", Toast.LENGTH_LONG).show();
        }

        // Check if first name input is empty
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(SignUpPageActivity.this, "Please Enter First Name", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if last name input is empty
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(SignUpPageActivity.this, "Please Enter Full Name", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if email input is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignUpPageActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if valid email pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(SignUpPageActivity.this,"Invalid Email Address", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if password input is empty
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignUpPageActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
            return false;
      }

        // Check if confirm password input is empty
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(SignUpPageActivity.this, "Please Enter Confirm Password", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if password length is longer than 7 characters
        if (password.length() < 6) {
            Toast.makeText(SignUpPageActivity.this, "Password too short", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if confirm password matches password entered
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpPageActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // Allows user to choose image from gallery
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

    public void bindViews(){
        mUserImage = findViewById(R.id.userImageView);
        mFirstname = findViewById(R.id.mFirstname);
        mLastname = findViewById(R.id.mLastname);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        mConfirmPassword = findViewById(R.id.mConfirmPassword);
        buttonFamilySetup = findViewById(R.id.buttonFamilySetup);

        buttonFamilySetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                final String firstName = mFirstname.getText().toString().trim();
                final String lastName = mLastname.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                uploadUserDetails(firstName, lastName, email, password, confirmPassword);

            }
        });

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
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

    public void openFamilySetupActivity(){
        startActivity(new Intent(getApplicationContext(), FamilySetupActivity.class));
    }
}
