package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {

    private Button button;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        button = (Button) findViewById(R.id.button_to_settings);
        button1 = (Button) findViewById(R.id.button_to_uploads);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button == findViewById(R.id.button_to_settings)) {
                    openActivity1();
                } else if (button1 == findViewById(R.id.button_to_uploads)) {
                    openActivity2();
                }
            }
        });
    }

    public void openActivity1() {
        Intent intent = new Intent(this, AccountSettings.class);
        startActivity(intent);
    }
    public void openActivity2() {
        Intent intent = new Intent(this, NewItemUpload.class);
        startActivity(intent);
    }
}
