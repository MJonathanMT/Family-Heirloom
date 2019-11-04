package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private final String TAG = "Reset Password";

    Button buttonResetPassword;
    TextInputEditText mEmail;

    /**
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        bindViews();
    }

    /**
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        mEmail = findViewById(R.id.mEmail);

        // Button reset password
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(mEmail.getText()).toString().trim();

                // Check if email input is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if valid email pattern
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ResetPasswordActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                    return;
                }

                OnSuccessListener listener = new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        openMainActivity();
                    }
                };

                FirebaseAuthAdapter.notifyResetPassword(email, listener);
            }
        });
    }

    /**
     * This function redirects the current Intent to the MainActivity
     * and starts the next activity.
     */
    public void openMainActivity(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
