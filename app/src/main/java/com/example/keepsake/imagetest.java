package com.example.keepsake;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class imagetest extends AppCompatActivity {


    ImageView imageView;
    String url="https://firebasestorage.googleapis.com/v0/b/keepsake-fa9b6.appspot.com/o/item%2F1570003904729.jpg?alt=media&token=047a31b9-522e-4920-b3cd-cfd5045c6934";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagetest);

        imageView = findViewById(R.id.imageinput);
        Picasso.get().load(url).into(imageView);

    }
}
