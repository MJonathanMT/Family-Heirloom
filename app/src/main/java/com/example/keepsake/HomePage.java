package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HomePage extends AppCompatActivity {

    private Button button;
    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        button = (Button) findViewById(R.id.button_to_settings);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                openActivity1();
            }});
        button1 = (Button) findViewById(R.id.button_to_uploads);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                openActivity2();
            }});

    }


    public void openActivity1() {
        Intent intent = new Intent(this, AccountSettings.class);
        startActivity(intent);
    }
    public void openActivity2() {
        Intent intent = new Intent(this, NewItemUpload.class);
        startActivity(intent);
    }

//    public void openActivity3() {
//        Intent intent = new Intent(this, ViewItem.class);
//        startActivity(intent);
//    }

}
