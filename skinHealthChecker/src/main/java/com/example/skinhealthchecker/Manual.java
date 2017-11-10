package com.example.skinhealthchecker;
/*
Εμφανίζει συμβουλές για την βέλτιστη χρήση της εφαρμογής καθώς και πληροφορίες για αυτήν .



Shows tips for optimal use of the app as well as information about it.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Manual extends AppCompatActivity {
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//starts activity
        db = new DatabaseHandler(this);//link database handler
  Configurations def = db.getDEf();//gets app configurations
        super.onCreate(savedInstanceState);

        if (def.GetLanguage())//gets the active language
        setContentView(R.layout.activity_manual);//depending of the active language it displays the correct xml
        else
            setContentView(R.layout.activity_manualen);


        Button captureButton32 = (Button) findViewById(R.id.bback);// if back button pressed go to camera activity
        captureButton32.setFocusable(true);
        //captureButton.setFocusableInTouchMode(true);
        captureButton32.requestFocus();
        captureButton32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                Intent intent23 = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent23);// staring camera activity
                //

            }
        });

        /**Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    **/}

}
