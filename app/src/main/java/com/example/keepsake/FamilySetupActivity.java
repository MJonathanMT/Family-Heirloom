package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FamilySetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_setup);

        Button buttonCreateFamily = findViewById(R.id.buttonCreateFamily);
        Button buttonJoinFamily = findViewById(R.id.buttonJoinFamily);

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
        Intent intent = new Intent(this, CreateFamilyGroupActivity.class);
        startActivity(intent);
    }

    public void openActivity2() {
        Intent intent = new Intent(this, JoinFamilyGroupActivity.class);
        startActivity(intent);
    }
}
