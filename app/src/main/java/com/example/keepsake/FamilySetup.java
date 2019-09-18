package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FamilySetup extends AppCompatActivity {

    private Button buttonCreateFamily = findViewById(R.id.buttonCreateFamily);
    private Button buttonJoinFamily = findViewById(R.id.buttonJoinFamily);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_setup);

        buttonCreateFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity1();
            }
        });

        buttonJoinFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity2();
            }
        });
    }

    public void openActivity1() {
        Intent intent = new Intent(this, CreateFamilyGroup.class);
        startActivity(intent);
    }

    public void openActivity2() {
        Intent intent = new Intent(this, JoinFamilyGroup.class);
        startActivity(intent);
    }
}
