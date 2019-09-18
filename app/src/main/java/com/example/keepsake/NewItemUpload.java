package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NewItemUpload extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_upload);


        Spinner mySpinner = findViewById(R.id.spinnerFamilyNames);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(NewItemUpload.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.familyNames));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
    }
}
