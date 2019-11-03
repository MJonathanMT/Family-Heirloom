package com.example.keepsake.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.example.keepsake.R;

public class FamilySetupActivity extends AppCompatActivity {
    private  Button create_button;
    private  Button join_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_setup);
        bindViews();

    }

    public void bindViews(){
        create_button = findViewById(R.id.buttonCreateFamily);

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateFamilyActivity();
            }
        });

        join_button = findViewById(R.id.buttonJoinFamily);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinFamilyActivity();
            }
        });
    }

    public void openCreateFamilyActivity() {
        Intent intent = new Intent(this, CreateFamilyActivity.class);
        startActivity(intent);
    }

    public void openJoinFamilyActivity() {
        Intent intent = new Intent(this, JoinFamilyGroupActivity.class);
        startActivity(intent);
    }


}
