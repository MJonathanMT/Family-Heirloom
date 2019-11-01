package com.example.keepsake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class NewLoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button buttonRedirect;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        buttonRedirect = findViewById(R.id.buttonToLogIn);

        mAuth = FirebaseAuth.getInstance();

        buttonRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(mEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(mPassword.getText()).toString().trim();

                // Check if email input is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(NewLoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check if password input is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(NewLoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Login with user email and password if user has already registered
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(NewLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(NewLoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        ImageView buttonBackToMain = findViewById(R.id.backarrow);

        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });



    }

    // Redirect to signup page
    public void signup_btn(View view) {
        startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
    }
}
