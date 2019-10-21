package com.example.keepsake;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.keepsake.memberList.FamilyMemberPageActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        createNavBar();
        Button buttonUpdateDetails = findViewById(R.id.buttonUpdateDetails);
        Button buttonChangeFamily = findViewById(R.id.buttonChangeCurrentFamily);
        Button buttonAddFamily = findViewById(R.id.buttonAddFamily);

        buttonUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDetailsActivity();
            }
        });

        buttonChangeFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeFamilyActivity();
            }
        });


        buttonAddFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFamilySetupActivity();
            }
        });

    }
    public void openUpdateDetailsActivity(){
        Intent intent = new Intent(this, UpdateDetailsActivity.class);
        startActivity(intent);
    }
    public void openFamilySetupActivity(){
        Intent intent = new Intent(this, FamilySetupActivity.class);
        startActivity(intent);
    }
    public void openChangeFamilyActivity(){
        Intent intent = new Intent(this, ChangeFamilyActivity.class);
        startActivity(intent);
    }
    private void createNavBar(){
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.ButtonHomepageAccess) {
                    openHomePageActivity();
                } else if (id == R.id.ButtonFamilyItemsAccess) {
                    openViewFamilyItemsActivity();
                } else if (id == R.id.ButtonFamilyMembersAccess) {
                    openFamilyMemberPageActivity();
                } else if (id == R.id.ButtonProfileAccess) {
                    openProfileActivity();
                } else if (id == R.id.ButtonLogOutAccess) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(AccountSettingsActivity.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));


                }
                return true;
            }
        });
    }
    private void openProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void openHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
    public void openViewFamilyItemsActivity() {
        Intent intent = new Intent(this, ViewFamilyItemsActivity.class);
        startActivity(intent);
    }

    public void openFamilyMemberPageActivity() {
        Intent intent = new Intent(this, FamilyMemberPageActivity.class);
        startActivity(intent);
    }
}
