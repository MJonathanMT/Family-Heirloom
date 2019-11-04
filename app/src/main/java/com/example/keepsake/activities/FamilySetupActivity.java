package com.example.keepsake.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.example.keepsake.R;

/***
 * This activity is the page where you can choose
 * either to create a new familyGroup or to join an existing familyGroup.
 */
public class FamilySetupActivity extends AppCompatActivity {
    private  Button create_button;
    private  Button join_button;

    /***
     * This function is where you initialize your activity.
     * When Activity is started, onCreate() method will be called
     * Acts as a main function to call the other functions
     * @param savedInstanceState is a non-persistent, dynamic data in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_setup);
        bindViews();

    }

    /***
     * This function sets all the OnClickListeners on the existing buttons within the activity.
     * It makes all the buttons clickable and redirects the user the the specific activity.
     */
    public void bindViews(){
        create_button = findViewById(R.id.buttonCreateFamily);

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateFamilyActivity();
            }
        });

        join_button = findViewById(R.id.buttonJoinFamily);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinFamilyActivity();
            }
        });
    }
    /***
     * This function redirects the current Intent to the CreateFamilyActivity
     * and starts the next activity.
     */
    public void openCreateFamilyActivity() {
        Intent intent = new Intent(this, CreateFamilyActivity.class);
        startActivity(intent);
    }

    /***
     * This function redirects the current Intent to the JoinFamilyGroupActivity
     * and starts the next activity.
     */
    public void openJoinFamilyActivity() {
        Intent intent = new Intent(this, JoinFamilyGroupActivity.class);
        startActivity(intent);
    }


}
