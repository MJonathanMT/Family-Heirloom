package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Main";

    Button buttonLogIn, buttonSignUp;

    /**
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
    }

    /**
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonLogIn = findViewById(R.id.buttonToLogin);

        // Login button
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPageActivity(v);

            }
        });

        // Sign up button
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupPageActivity(v);
            }
        });
    }

    /**
     * Checks if user is already signed in using onStart() method of the application
     */
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuthAdapter.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }
    }

    /**
     * This function redirects the current Intent to the SignUpPageActivity
     * and starts the next activity.
     */
    public void openSignupPageActivity(View view){
        startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
    }

    /**
     * This function redirects the current Intent to the LoginActivity
     * and starts the next activity.
     */
    public void openLoginPageActivity(View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}