package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;

public class RequestFamilyGroupActivity extends AppCompatActivity {
    private final String TAG = "Join Request";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_family_group);

        bindViews();
    }

    public void bindViews(){
        Button buttonHomePage = findViewById(R.id.buttonHomePage);

        buttonHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openUserProfileActivity();
            }
        });
    }

    public void openUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
}

