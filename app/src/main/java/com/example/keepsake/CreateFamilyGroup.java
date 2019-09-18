package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateFamilyGroup extends AppCompatActivity {

    private Button buttonHomePage = findViewById(R.id.buttonHomePage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family_group);

        buttonHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity1();
            }
        });
    }

    public void openActivity1() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
