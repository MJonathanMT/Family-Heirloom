package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpPage extends AppCompatActivity {

    EditText mFirstname, mLastname, mEmail, mPassword, mConfirmPassword;
    Button buttonFamilySetup;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        mFirstname = findViewById(R.id.mFirstname);
        mLastname = findViewById(R.id.mLastname);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        mConfirmPassword = findViewById(R.id.mConfirmPassword);
        buttonFamilySetup = findViewById(R.id.buttonFamilySetup);

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

                if (TextUtils.isEmpty(firstname)) {
                    Toast.makeText(SignUpPage.this, "Please Enter First Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Full Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (email.matches(emailPattern)){
                    Toast.makeText(getApplicationContext(),"Invalid Email address", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Confirm Password", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(SignUpPage.this, "Password too short", Toast.LENGTH_LONG).show();
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpPage.this, "Password does not match", Toast.LENGTH_LONG).show();
                }
                if (password.equals(confirmPassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        User user = new User(
                                                firstname,
                                                lastname,
                                                email
                                        );
                                        FirebaseFirestore.getInstance().collection("user").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    startActivity(new Intent(getApplicationContext(), FamilySetup.class));
                                                    Toast.makeText(SignUpPage.this, "User information saved", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpPage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
