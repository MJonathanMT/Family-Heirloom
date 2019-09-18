package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuItem;

public class ViewFamilyItems extends AppCompatActivity {

    private DrawerLayout drawerLayout = findViewById(R.id.itemsDrawerLayout);
    private ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_family_items);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Items");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonHomepageAccess) {
                    openActivity3();
                }
                else  if (id == R.id.ButtonFamilyItemsAccess){
                    openActivity4();
                }
                else  if (id == R.id.ButtonFamilyMembersAccess){
                    openActivity5();
                }
                return true;
            }
        });
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
