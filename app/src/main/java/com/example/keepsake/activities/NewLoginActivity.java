package com.example.keepsake.activities;

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

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class NewLoginActivity extends AppCompatActivity {
    private final String TAG = "New Login";

    EditText mEmail, mPassword;
    Button buttonRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        bindViews();

    }

    public void bindViews(){
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        buttonRedirect = findViewById(R.id.buttonToLogIn);

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

                OnSuccessListener listener = new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        openUserProfileActivity();
                    }
                };

                // Login with user email and password if user has already registered
                FirebaseAuthAdapter.signIn(email, password, listener);

            }
        });

        ImageView buttonBackToMain = findViewById(R.id.backarrow);

        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
    }

    public void openUserProfileActivity(){
        startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
    }

    // Redirect to signup page
    public void openSignUpPageActivity(View view) {
        startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
    }
}
