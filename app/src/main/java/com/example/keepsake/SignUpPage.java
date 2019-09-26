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

public class SignUpPage extends AppCompatActivity {

    EditText mFirstname, mLastname, mEmail, mPassword, mConfirmPassword;
    Button buttonFamilySetup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        mFirstname = (EditText)findViewById(R.id.mFirstname);
        mLastname = (EditText)findViewById(R.id.mLastname);
        mEmail = (EditText)findViewById(R.id.mEmail);
        mPassword = (EditText)findViewById(R.id.mPassword);
        mConfirmPassword = (EditText)findViewById(R.id.mConfirmPassword);
        buttonFamilySetup = (Button) findViewById(R.id.buttonFamilySetup);

        mAuth = FirebaseAuth.getInstance();

        buttonFamilySetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                String firstname = mFirstname.getText().toString().trim();
                String lastname = mLastname.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstname)) {
                    Toast.makeText(SignUpPage.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Full Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpPage.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(SignUpPage.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                if (password.equals(confirmPassword)) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        startActivity(new Intent(getApplicationContext(), FamilySetup.class));
                                        Toast.makeText(SignUpPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpPage.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                }
            }
        });
    }

    public void buttonFamilySetup() {
        startActivity(new Intent(getApplicationContext(), FamilySetupActivity.class));
    }
}
