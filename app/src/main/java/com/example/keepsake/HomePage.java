package com.example.keepsake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class HomePage extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonUpload = findViewById(R.id.buttonUpload);
        DrawerLayout drawerLayout = findViewById(R.id.homeDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openActivity1();
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                openActivity2();
            }
        });

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonHomepageAccess) {
                    openActivity3();
                } else if (id == R.id.ButtonFamilyItemsAccess) {
                    openActivity4();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openActivity5();
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

    public void openActivity3() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void openActivity4() {
        Intent intent = new Intent(this, ViewFamilyItems.class);
        startActivity(intent);
    }

    public void openActivity5() {
        Intent intent = new Intent(this, FamilyMemberPage.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}
