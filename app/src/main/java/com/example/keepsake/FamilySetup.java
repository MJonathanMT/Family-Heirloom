package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FamilySetup extends AppCompatActivity {

    private Button button;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_setup);
        button = (Button) findViewById(R.id.button_to_create_family_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity1();
            }
        });

        button1 = (Button) findViewById(R.id.button_to_join_family_group);
        button1.setOnClickListener(new View.OnClickListener() {
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
