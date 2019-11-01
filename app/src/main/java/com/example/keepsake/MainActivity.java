package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

    EditText mEmail, mPassword;
    TextView forgotPassword;
    Button buttonLogIn;
    ImageView backArrow;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        forgotPassword = findViewById(R.id.tv_forget_password);
        buttonLogIn = findViewById(R.id.buttonToLogin);
        backArrow = findViewById(R.id.backarrow);


        mAuth = FirebaseAuth.getInstance();

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(mEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(mPassword.getText()).toString().trim();

                // Check if email input is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if valid email pattern
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if password input is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
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
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Check if user is already signed in
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }
    }

    // Redirect to signup page
    public void signup_btn(View view){
        startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
    }
}