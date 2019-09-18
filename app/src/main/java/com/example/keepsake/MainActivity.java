package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLogIn = findViewById(R.id.buttonLogIn);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity1();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity2();
            }
        });
    }

    public void openActivity1() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void openActivity2() {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }
}
