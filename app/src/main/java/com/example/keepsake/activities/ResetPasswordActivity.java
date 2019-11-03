package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseAdapter.FirebaseAdapter;
import com.example.keepsake.database.firebaseAdapter.FirebaseAuthAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private final String TAG = "Reset Password";

    Button buttonResetPassword;
    TextInputEditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        bindViews();
    }

    public void bindViews(){
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        mEmail = findViewById(R.id.mEmail);

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

    public void openMainActivity(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }


}
