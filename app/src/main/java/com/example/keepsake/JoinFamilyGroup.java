package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinFamilyGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_family_group);

        Button buttonRequestJoin = findViewById(R.id.buttonRequestJoin);

        buttonRequestJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity1();
            }
        });
    }

    public void openActivity1() {
        Intent intent = new Intent(this, RequestFamilyGroup.class);
        startActivity(intent);
    }

}
