package com.example.keepsake.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.keepsake.R;

public class NewMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmain);

        manageButtons();

    }

    private void manageButtons() {

        Button buttonLogIn = findViewById(R.id.buttonToLogIn);
        TextView buttonSignUp = findViewById(R.id.register);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openNewLoginActivity();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openRegisterActivity();
            }
        });
    }

    public void openNewLoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void openRegisterActivity(){
        Intent intent = new Intent(this, SignUpPageActivity.class);
        startActivity(intent);
    }
}
