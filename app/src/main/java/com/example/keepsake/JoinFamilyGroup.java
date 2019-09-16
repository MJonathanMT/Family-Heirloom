package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinFamilyGroup extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_family_group);

        button = (Button) findViewById(R.id.button_continue_to_request_page);
        button.setOnClickListener(new View.OnClickListener() {
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
