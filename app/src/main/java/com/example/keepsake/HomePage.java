package com.example.keepsake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomePage extends AppCompatActivity {

    private Button button;
    private Button button1;
    private Button button2;


    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

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

        dl = (DrawerLayout)findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonHomepageAccess){
                    Toast.makeText(HomePage.this, "HomePage", Toast.LENGTH_SHORT).show();
                }
                else  if (id == R.id.ButtonFamilyItemsAccess){
                    Toast.makeText(HomePage.this, "FamilyItems", Toast.LENGTH_SHORT).show();
                }
                else  if (id == R.id.ButtonFamilyMembersAccess){
                    Toast.makeText(HomePage.this, "FamilyMembers", Toast.LENGTH_SHORT).show();
                }
                return true;
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

//    public void openActivity3() {
//        Intent intent = new Intent(this, ViewItem.class);
//        startActivity(intent);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}
