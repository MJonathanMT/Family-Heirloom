package com.example.keepsake;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountSettings extends AppCompatActivity {

    private Button button;
    private Button button2;
    private Button button_log_out;
    private Button button_add_family;
    private Button button_change_family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        button = (Button) findViewById(R.id.button_update_details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity1();
            }
        });

        button_log_out = (Button) findViewById(R.id.button_log_out);
        button_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);
                builder.setTitle("Exit");
                builder.setMessage("Do you want to exit ??");
                builder.setPositiveButton("Yes. Logging out now!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                        System.exit(0);

                    }
                });
                builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        button_change_family = (Button) findViewById(R.id.button_change_current_family);
        button_change_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3();
            }
        });

        button_add_family = (Button) findViewById(R.id.button_add_family);
        button_add_family.setOnClickListener(new View.OnClickListener() {
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
