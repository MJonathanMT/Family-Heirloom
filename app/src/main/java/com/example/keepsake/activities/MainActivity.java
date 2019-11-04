package com.example.keepsake.activities;

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

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Main";

    Button buttonLogIn, buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
    }

    public void bindViews(){
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonLogIn = findViewById(R.id.buttonToLogin);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPageActivity(v);

            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupPageActivity(v);
            }
        });
    }

    // Check if user is already signed in
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuthAdapter.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }
    }

    // Redirect to signup page
    public void openSignupPageActivity(View view){
        startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
    }

    public void openLoginPageActivity(View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}