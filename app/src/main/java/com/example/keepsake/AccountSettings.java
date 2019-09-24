package com.example.keepsake;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AccountSettings extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Button buttonUpdateDetails = findViewById(R.id.buttonUpdateDetails);
        Button buttonLogOut =  findViewById(R.id.buttonLogOut);
        Button buttonChangeFamily = findViewById(R.id.buttonChangeCurrentFamily);
        Button buttonAddFamily = findViewById(R.id.buttonAddFamily);

        buttonUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity1();
            }
        });

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(AccountSettings.this, "Signout successful!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

//                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);
//                builder.setTitle("Exit");
//                builder.setMessage("Do you want to exit ??");
//                builder.setPositiveButton("Yes. Logging out now!", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        finish();
//                        System.exit(0);
//
//                    }
//                });
//                builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        dialogInterface.dismiss();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });


        buttonChangeFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3();
            }
        });


        buttonAddFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

    }
    public void openActivity1(){
        Intent intent = new Intent(this, UpdateDetails.class);
        startActivity(intent);
    }
    public void openActivity2(){
        Intent intent = new Intent(this, JoinFamilyGroup.class);
        startActivity(intent);
    }
    public void openActivity3(){
        Intent intent = new Intent(this, ChangeFamily.class);
        startActivity(intent);
    }
}
