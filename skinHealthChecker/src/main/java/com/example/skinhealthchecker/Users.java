package com.example.skinhealthchecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Color.RED;

/**
 * Ο διαχειριστής των προφίλ ! Ο χρήστης μπορεί να φτιάξει , επιλέξει διαγράψει κάποιο προφίλ .
 *
 *
 *
 * The profile manager! The user can make, choose to delete a profile.
 */

public class Users  extends Activity implements
        CompoundButton.OnCheckedChangeListener {
 String TAG;
    DatabaseHandler db; // db handler
 //   TabHost tabHost;

 //   private Spinner    profil  ;

    private EditText profilname  ; // the text input for profile name
    private EditText name  ;// the text input for user name
    private EditText age  ;// the text input for user age
    private EditText mail ;// the text input for user mail
    private EditText phone  ;// the text input for user phone number
    private Spinner spinner2 ;// spinner that shows saved profiles
    private TextView text;// textview that will show a message to user if fills something wrong

    private Spinner    fullo; // spinner that shows the gender list
     private boolean history;

    public void onCreate(Bundle savedInstanceState) {


        history =false; //on creating profile the bad history will be false by default
        TAG = "RESULTS";
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();// getting intent extras -> but there is no extras here
        db = new DatabaseHandler(this);// link the database to handler
        Configurations def = db.getDEf(); //gets apps configurations


        if (def.GetLanguage())//load the actives language xml
        setContentView(R.layout.account);

        else
            setContentView(R.layout.accounten);

        TabHost host = (TabHost)findViewById(R.id.tabHost);// link the the  xml contents
        host.setup();

         //  profil = (Spinner) findViewById(R.id.spinner2);

          profilname = (EditText) findViewById(R.id.profilname);
          name = (EditText) findViewById(R.id.name);
          age = (EditText) findViewById(R.id.age);
         mail = (EditText) findViewById(R.id.mail);
         phone = (EditText) findViewById(R.id.phone);
        text= (TextView) findViewById(R.id.textView);
spinner2=(Spinner) findViewById(R.id.spinner2);

   //     mylist=(ListView)findViewById(R.id.mylist);
      //   spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, GetCreatedNames()); //selected item will look like a spinner set from XML

             fullo = (Spinner) findViewById(R.id.spinnersex);
        //Tab 1
        TabHost.TabSpec spec;
        if (def.GetLanguage()) {// sets the two tabs using correct active language
              spec = host.newTabSpec("Προφίλ");
            spec.setContent(R.id.baseone);
            spec.setIndicator("Προφίλ");
            host.addTab(spec);
            //Tab 2
            spec = host.newTabSpec("Νέο");
            spec.setContent(R.id.basetwo);
            spec.setIndicator("Νέο Προφίλ");
            host.addTab(spec);
        }else

        {  spec = host.newTabSpec("Profil");
            spec.setContent(R.id.baseone);
            spec.setIndicator("Profile");
            host.addTab(spec);
            //Tab 2
            spec = host.newTabSpec("New");
            spec.setContent(R.id.basetwo);
            spec.setIndicator("New profile");
            host.addTab(spec);


        }
      Button captureButtonU = (Button) findViewById(R.id.gotomain);// on select profile button
        captureButtonU.setFocusable(true);
        //captureButton.setFocusableInTouchMode(true);
        captureButtonU.requestFocus();
        captureButtonU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                int id=GetSelectedId(String.valueOf(spinner2.getSelectedItem())); // gets the spinner id

if (id>-1) { //if there is no error

    Configurations def = db.getDEf();  // gets latest instance of configuration
   //String [] profnames = GetCreatedNames();
   // String name = profnames[id];
 Log.d("profname",db.getProfile(def.GetPID()).GetProfilname());
   // Profile  pr= db.getProfilebyname(name);
    def.SetPID(id); // set the selected id to configurations
    db.updateDef(def); // updates configuration
}
                Intent intentU = new Intent(getApplicationContext(), CameraActivity.class);// goes to start screen
                startActivity(intentU);
                //

            }
        });

        Button captureButtonU2 = (Button) findViewById(R.id.save);// saves new profile button
        captureButtonU2.setFocusable(true);
        //captureButton.setFocusableInTouchMode(true);
        captureButtonU2.requestFocus();
        captureButtonU2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (
                    age.getText().toString().isEmpty()||
                    String.valueOf(fullo.getSelectedItem()).isEmpty()||
                            name.getText().toString().isEmpty()||
                            profilname.getText().toString().isEmpty()||
                            mail.getText().toString().isEmpty()
                       //     || phone.getText().toString().isEmpty()
                        )// if all data filled (except phone number )
                {// prints error
                    text.setText("Εισάγετε τα στοιχεία του νέου προφίλ σας." +
                            "\nΠρέπει να συμπληρώσετε όλα τα στοιχεία  \n πριν προχωρήσετε !!!!", TextView.BufferType.EDITABLE);
                    text.setTextColor(RED);

                    getWindow().getDecorView().findViewById(R.id.base).invalidate();



                } else if (( mail.getText().toString().indexOf('@')<1)||(mail.getText().toString().indexOf('@')>=mail.getText().toString().length()-1))
                {// if mail is not in right shape shows error

                    text.setText("Εισάγετε τα στοιχεία του νέου προφίλ σας." +
                            "\n Το e-mail που δώσατε δεν πρέπει να είναι σωστό !!!!", TextView.BufferType.EDITABLE);
                    text.setTextColor(RED);
                    getWindow().getDecorView().findViewById(R.id.base).invalidate();

                     }

                    else {

                    String string =  mail.getText().toString();
                    Pattern pattern = Pattern.compile("([@])");// checks if there is more than one @ in mail
                    Matcher matcher = pattern.matcher(string);
                    int count = 0;
                    while (matcher.find()) count++;

                    if (count>1) { // if there is many @ prints error
                        text.setText("Εισάγετε τα στοιχεία του νέου προφίλ σας." +
                                "\n Το e-mail που δώσατε δεν πρέπει να είναι σωστό !!!!", TextView.BufferType.EDITABLE);
                        text.setTextColor(RED);
                        getWindow().getDecorView().findViewById(R.id.base).invalidate();
                    }else { //if all seems to be right

                     int id =    CreateNewProfile(); // creates new profile

                      Configurations def = db.getDEf();// gets Configurations instance and activates new id
                        def.SetPID(id);//
                        db.updateDef(def);
                        Intent intentU = new Intent(getApplicationContext(), CameraActivity.class);// go back to start screen
                        startActivity(intentU);
                    }

                }

            }
        });

        Button captureButtonD = (Button) findViewById(R.id.deleteprofil);  // if delete button pressed
        captureButtonD.setFocusable(true);
        //captureButton.setFocusableInTouchMode(true);
        captureButtonD.requestFocus();
        captureButtonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                int id;
                try{
                  id=GetSelectedId(String.valueOf(spinner2.getSelectedItem()));} // gets the selected profiles id
catch(NullPointerException e) {
                     id=0;
                }

                Log.d("id", Integer.toString(id));

                if (id>0) { // if the id is found and there is not the default id (id 0 )

                    Configurations def = db.getDEf();// gets the configuration

                     def.SetPID(0); /// set the defaults profile as active
                    db.updateDef(def);// update configurations

                    db.deleteProfile(db.getProfile(id));// delete the profile having the id

                    finish(); // restarts the intent
                    startActivity(getIntent());
                }

            }
        });

if (db.getProfileCount()>1) {// if there is more profiles except the defaults
     ArrayAdapter<String>
    spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, GetCreatedNames()); //selected item will look like a spinner set from XML
    // fill the ArrayAdapter with profile names

    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(spinnerArrayAdapter);// link the array adapter to spinner

    spinner2.setSelection(Getpos()); // sets the  active profile as selected
    getWindow().getDecorView().findViewById(R.id.baseone).invalidate();//updates grafics
}
    }



    /*
    this function is the checkbutton listener
    // on change condition event  get as input the id of checkbox which changed
    // and the new condition and updates the correct static var .
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) { // if checked sets true
            switch (buttonView.getId()) {


                case R.id.history:
                    history = true;

                    //do stuff
                    break;
            }


        }
        else
        {            switch (buttonView.getId()) {// if not sets false
            case R.id.history:
                history = false;

                //do stuff
                break;

        }

        }}


    /*
    This function creates a new profile filling it with data that user gave
    it returns the id of new profile
     */
    public int CreateNewProfile( ){

        Configurations def=db.getDEf();// gets the app latest configurations
        def.PIDCOUntup(); // creates new id
        def.SetPID(GetNewId(def)); //sets the new id to configurations
        Profile last = new  Profile(); // crates a new instance of profile
            db.updateDef(def); // updates the db
Log.d("new profiles id  is = ",Integer.toString(GetNewId(def)));
        last.SetAge(Integer.parseInt(age.getText().toString()));// fills profile last with data

        last.SetIdM(GetNewId(def));
        last.SetSex(String.valueOf(fullo.getSelectedItem()));
         last.SetHistory(history);
        last.SetName(name.getText().toString());
        last.SetProfilname(profilname.getText().toString());
        last.SetMail(mail.getText().toString());
        last.SetPhone(phone.getText().toString());

        db.addprofile(last); // adds last to db

        // Molestest(last);
        return last.GetIdM(); // returns the id

    }
    public int GetNewId(   Configurations def) { // gets the latest id from db


        int id = def.GetPIDCOUnt();
        return id;

    }
/*
    this function gets the name of profile as input , finds its id and returns it


 */
    public int GetSelectedId(String in ){

        List<Profile> myList = new ArrayList<Profile>();

        myList =db.getAllProfiles();// get all profiles stored in db

        int out=-1;//init


        for(int i=myList.size()-1;i>=0;i--){

            if  (myList.get(i).GetProfilname().equals(in)) { // if the name match any profile name
                out = myList.get(i).GetIdM(); //gets its id
                break;
            }
        }
        return out;
    }

    /*
    The function GetCreatedNames returns all profile names that is stored in db
    in backwards order
     */
    public String [] GetCreatedNames( ){
        List<Profile> myList = new ArrayList<Profile>();

        myList =db.getAllProfiles(); // gets all profiles
        String names [] = new String[myList.size()];

        int count =0;

        for(int i=myList.size()-1;i>=0;i--){
            //
            names[count]=myList.get(count).GetProfilname();//saves profile names backwards


            count++;// names counter

        }
        return names;
    }

    /*
   The function Getpos returns the position of the active profile in the spinner line
    */
    public int Getpos( ){
        List<Profile> myList = new ArrayList<Profile>();

        myList =db.getAllProfiles();// gets all profiles
        String names [] = new String[myList.size()];

        int count =0;

        for(int i=myList.size()-1;i>=0;i--){

            names[count]=myList.get(count).GetProfilname();//saves profile names backwards


            count++;// names counter

        }

         int pos =0; // init the position
        Configurations def = db.getDEf();// gets apps configuration
        Profile prof = db.getProfile(def.GetPID()); // gets actives profile instance

        for (int i=0;i<myList.size();i++) {
            if (names[i].equals( prof.GetProfilname())) { //if the name of active profile is equal with any name in the list
                Log.d("prof", Integer.toString(i));
                pos = i;// returns the position of the name
                break;
            }

        }
        return pos;// returns the position of the name
    }

    @Override
    public void onBackPressed() {// when back pressed go to mail activity
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);


        startActivity(intent);

    }
        }
