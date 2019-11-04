package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "New Login";

    private EditText mEmail, mPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;

    /**
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        bindViews();

    }

    /**
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        buttonLogin = findViewById(R.id.buttonToLogIn);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // Login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(mEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(mPassword.getText()).toString().trim();

                // Check if email input is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check if password input is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
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

        // Forgot password text
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResetPasswordActivity();
            }
        });

        ImageView buttonBackToMain = findViewById(R.id.backarrow);

        // Button to main login page
        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
    }

    /**
     * This function redirects the current Intent to the ResetPasswordActivity
     * and starts the next activity.
     */
    public void openResetPasswordActivity(){
        startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
    }

    /**
     * This function redirects the current Intent to the UserProfileActivity
     * and starts the next activity.
     */
    public void openUserProfileActivity(){
        startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
    }
}
