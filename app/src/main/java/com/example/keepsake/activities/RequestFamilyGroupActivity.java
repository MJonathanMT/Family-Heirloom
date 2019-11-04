package com.example.keepsake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepsake.R;

/***
 * This activity shows confirmation that you have sent a
 * joinRequest to the familyGroup you applied for.
 */
public class RequestFamilyGroupActivity extends AppCompatActivity {
    private final String TAG = "Join Request";

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_family_group);

        bindViews();
    }

    /***
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        Button buttonHomePage = findViewById(R.id.buttonHomePage);

        buttonHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openUserProfileActivity();
            }
        });
    }

    /***
     * This function redirects the current Intent to the userProfileActivity
     * and starts the next activity.
     */
    public void openUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
}

