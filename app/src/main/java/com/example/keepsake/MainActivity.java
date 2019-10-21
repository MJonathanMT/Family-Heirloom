package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputEditText mEmail, mPassword;
    Button buttonLogIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);

        mAuth = FirebaseAuth.getInstance();

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(mEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(mPassword.getText()).toString().trim();

                // Check if email input is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check if password input is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Login with user email and password if user has already registered
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    // Redirect to signup page
    public void signup_btn(View view){
        startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
    }
}