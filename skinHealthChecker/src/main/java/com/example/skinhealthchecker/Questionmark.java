package com.example.skinhealthchecker;
/*
Εμφανίζει ένα ερωτηματολόγιο για τον χρήστη σχετικά με την εντοπισμένη  ελιά . Δίνει την επιλογές στον
χρήστη σχετικά για το αν θέλει να αποθηκεύσει η να ανανεώσει μια ελιά  ή όχι .
 */
/*
        Displays a questionnaire for the user about the localized mole, gives the choices to
        user about whether he wants to store or update an mole or not.
          */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.color.black;
import static android.graphics.Color.RED;


/**
 * Created by chief on 20/2/2017.
 */



//public class Questionmark extends AppCompatActivity {

public class Questionmark extends Activity implements
        CompoundButton.OnCheckedChangeListener {

    TabHost tabHost;

    static String TAG;
    static double realwidth; // the width of the mole
    static double realheight;// the height of the mole

     private Spinner spinner2; // spinner filled with activ profiles moles names
      private    boolean liquid=false; //if true , the mole has liquid on it
    private    boolean itch=false;//if true , the mole has itch
    private   boolean inpain = false;//if true , the mole is in pain
    private   boolean ishard = false;//if true , the mole has hard felling

    private   boolean edgesproblem=false;//if true , the mole has edgesproblem
    private static double [][] morfology;// morphology of the mole [][0] for one side [][1] for the other
    static InputStream imageStream; // used to load the image
    public static  Bitmap value; // image of the mole

    DatabaseHandler db; // db handler
    private static double colorproblem;//if true, the mole have more than one color
    private static boolean edgessimilarityproblem;// if true, the mole has asymmetry
    private static int src;//the length of mole (in lines or rows (what ever is bigger)

    public void onCreate(Bundle savedInstanceState) {

          db = new DatabaseHandler(this); //links the db with the handler

        TAG = "RESULTS";
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();// gets info from last activity
        if (extras != null) {
            Intent intent = getIntent();
            realwidth = intent.getDoubleExtra("realwidth", 0);
            realheight = intent.getDoubleExtra("realheight", 0);
            edgesproblem = intent.getBooleanExtra("edgesproblem", false);
            src= intent.getIntExtra("src", 0);

            //Bundle b = intent.getExtras();
            String[][] value = (String[][]) extras.getSerializable("tableString");
            //         morfology=  new double[5][2];

            morfology = new double[value.length][2];

            for (int i = 0; i < value.length; i++) {// fills the morphology s array with data
                morfology[i][0] = Double.parseDouble(value[i][0]);
                morfology[i][1] = Double.parseDouble(value[i][1]);

            }


            colorproblem = intent.getDoubleExtra("colorproblem", 0); //updates the colorproblem status
            edgessimilarityproblem = intent.getBooleanExtra("edgessimilarityproblem", false);//updates the edgessimilarityproblem status


        }
        Configurations def = db.getDEf();// gets the apps configurations
        if (def.GetLanguage()){// select to load the xml having the active language
        setContentView(R.layout.tablayout);

            TabHost host = (TabHost)findViewById(R.id.tabHost);// setups 3 tabs using the greek language
            host.setup();

            //Tab 1
            TabHost.TabSpec spec = host.newTabSpec("Νέος");
            spec.setContent(R.id.base);
            spec.setIndicator("Νέος σπίλος");
            host.addTab(spec);

            //Tab 2
            spec = host.newTabSpec("Αποθηκευμένος");
            spec.setContent(R.id.base2);
            spec.setIndicator("Αποθηκευμένος\n σπίλος");
            host.addTab(spec);

            //Tab 3
            spec = host.newTabSpec("Χωρίς");
            spec.setContent(R.id.base3);
            spec.setIndicator("Χωρίς αποθήκευση");

            host.addTab(spec);




        }
        else{
            setContentView(R.layout.tablayouten);

            TabHost host = (TabHost)findViewById(R.id.tabHost);// setups 3 tabs using the english language
            host.setup();

            //Tab 1
            TabHost.TabSpec spec = host.newTabSpec("New");
            spec.setContent(R.id.base);
            spec.setIndicator("New mole");
            host.addTab(spec);

            //Tab 2
            spec = host.newTabSpec("Saved");
            spec.setContent(R.id.base2);
            spec.setIndicator("Saved \n mole");
            host.addTab(spec);

            //Tab 3
            spec = host.newTabSpec("nosave");
            spec.setContent(R.id.base3);
            spec.setIndicator("No-save mode");

            host.addTab(spec);


        }






        spinner2 = (Spinner) findViewById(R.id.spinner22); // links the xml contents  with local vars
//tabs 1 checkboxs names  has one digit
//tabs 2 checkboxs  names has two digits
//tabs 3 checkboxs  names has three digits

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox); //checkBox s for liquid case
        final CheckBox checkBox00 = (CheckBox) findViewById(R.id.checkBox12);
        final CheckBox checkBox000 = (CheckBox) findViewById(R.id.checkBox333);

        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2); //checkBox s for hasitch case
        final CheckBox checkBox22 = (CheckBox) findViewById(R.id.checkBox22);
        final CheckBox checkBox222 = (CheckBox) findViewById(R.id.checkBox2333);

        final CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);//checkBox s for ishard's case
        final CheckBox checkBox33 = (CheckBox) findViewById(R.id.checkBox32);
        final CheckBox checkBox333 = (CheckBox) findViewById(R.id.checkBox333);

        final CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);//checkBox s for inpain case
        final CheckBox checkBox44 = (CheckBox) findViewById(R.id.checkBox42);
        final CheckBox checkBox444 = (CheckBox) findViewById(R.id.checkBox4333);





        final EditText textname =(EditText) findViewById(R.id.name33); // in here user writes the name of the new mole

        checkBox.setOnCheckedChangeListener(this);//setups listeners

        checkBox00.setOnCheckedChangeListener(this);
        checkBox000.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox22.setOnCheckedChangeListener(this);
        checkBox222.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        checkBox33.setOnCheckedChangeListener(this);
        checkBox333.setOnCheckedChangeListener(this);
        checkBox4.setOnCheckedChangeListener(this);
        checkBox44.setOnCheckedChangeListener(this);
        checkBox444.setOnCheckedChangeListener(this);



        Button captureButton = (Button) findViewById(R.id.save);// if the user press save button
        captureButton.setFocusable(true);
        //     captureButton.setFocusableInTouchMode(true);
        captureButton.requestFocus();
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                TextView textv = (TextView) findViewById(R.id.textView2); // prints message to user in case of something goes wrong



                 String name=textname.getText().toString(); //gets the name

                Log.d("incheck", Boolean.toString(liquid));

                if   (!name.isEmpty())  {// if name is filled



                // starts diagnosis activity
                    Intent intent = new Intent(getApplicationContext(), Diagnosis.class);

                    Bundle b = new Bundle();//sends to next activity  the mole id and if the user wants not to save the current mole
                    intent.putExtra("id",  CreateNewMole(name)); //Optional parameters

                    intent.putExtra("delete", false); //Optional parameters




                     startActivity(intent);


                } else {

                    Configurations def = db.getDEf();
                if (def.GetLanguage()) {
                    //in case of users does not give name
                    textv.setText("Πρέπει να δώσετε ένα όνομα  \n πριν προχωρήσετε !!!!", TextView.BufferType.EDITABLE);
                    textv.setTextColor(RED);
                }
                else{

                    textv.setText("You must insert name before proceeding!!!!", TextView.BufferType.EDITABLE);
                    textv.setTextColor(RED);
                     }
                //
                }

            }
        });


        Button captureButton2 = (Button) findViewById(R.id.save222);// case of second tab button
        captureButton2.setFocusable(true);
        //     captureButton.setFocusableInTouchMode(true);
        captureButton2.requestFocus();
        captureButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //



            String  name  =String.valueOf(spinner2.getSelectedItem()); // gets the name of the mole that will updates from the spiner

             /**   if (checkBox00.isEnabled()) {
                    liquid = true;
                }
                if (checkBox22.isEnabled()) {
                    itch = true;
                }
                if (checkBox33.isEnabled()) {
                    ishard = true;
                }
                if (checkBox44.isEnabled()) {
                    inpain = true;
                }
                if (checkBox55.isEnabled()) {
                    history = true;
                }
**/


                    Intent intent = new Intent(getApplicationContext(), Diagnosis.class);// start s diagnosis activity

                    Bundle b = new Bundle();//sends to next activity  the mole id  , and that the mole will not be deleted

                    intent.putExtra("delete", false); //Optional parameters
                    intent.putExtra("id",  UpdateMole(name)); //Optional parameters




                    startActivity(intent);



                //

            }
        });

        Button captureButton3 = (Button) findViewById(R.id.save2333);// case of 3 tab
        captureButton3.setFocusable(true);
        //     captureButton.setFocusableInTouchMode(true);
        captureButton3.requestFocus();
        captureButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //







                    Intent intent = new Intent(getApplicationContext(), Diagnosis.class); // start the diagnosis tab
                    Bundle b = new Bundle();


                    intent.putExtra("id",  CreateNewMole(("nosave"))); //creates and sends new id


                    intent.putExtra("delete", true); //sets that the mole will be deleted




                    startActivity(intent);


                //

            }
        });


           if (GetNewId()>0) // if there is moles of current profile stored in database then fills the name spinner with moles
        //    if (false)

            {
     //       Configurations def =  db.getDEf();








                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, GetCreatedNames()); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //fills the spinner
            spinner2.setAdapter(spinnerArrayAdapter);
                getWindow().getDecorView().findViewById(R.id.base).invalidate();// updates graphics
        }

        else
        {

            db.getWritableDatabase();

        }

    }


    @Override
    public void onBackPressed() { // if back is pressed then go back to start activity
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);

      //  intent.putExtra("realwidth", realwidth); //Optional parameters
     //   intent.putExtra("realheight", realheight); //Optional parameters
        startActivity(intent);

    }



// returns next available mole id
    public int GetNewId() {

        Configurations def = db.getDEf();
        int id = def.GetIdM();// gets next available id
        return id;

    }


/*
    this function gets the name of profile as input , finds its id and returns it


 */

    public int GetSelectedId(String in ){
        List<Mole> myList = new ArrayList<Mole>();

        myList =db.getAllMoles();// get all moles stored in db

int out=-1;//init


        for(int i=myList.size()-1;i>=0;i--){

            if  (myList.get(i).GetName().equals(in)) {// if the name match any moles name
                out = myList.get(i).GetId();//gets its id

        break;
            }
        }
        return out;
    }
    /*
     The function GetCreatedNames returns all moles names that is stored in db
     in backwards order
      */
    public String [] GetCreatedNames( ){
        List<Mole> myList = new ArrayList<Mole>();


        Configurations def =db.getDEf();// gets the actives profiles instance
        Profile pro =db.getProfile(def.GetPID());
        //    mole = db.getAllMoles();

        myList = db.getAllProfileMoles(pro);// gets all profile's moles
      //  myList =db.getAllMoles();
String names [] = new String[myList.size()];

        int count =0;

   for(int i=myList.size()-1;i>=0;i--){

       names[count]=myList.get(count).GetName();//saves moles names backwards


       count++;// names counter


   }
        return names;
    }


    //this function gets a mole's name ,finds its id , gets the instance of the mole  and updates its data in db
    //also checks if moles evolving
    public int UpdateMole(String name){

        Mole last = new  Mole();
    double diameter =0;
        if (realwidth>realheight)// calculates the diameter
            diameter=realwidth;
        else
            diameter=realwidth;

        int id=GetSelectedId(name); // gets the moles id

        last.SetMole(Geturl(id));// updates its data
     //   last.SetWidth(realwidth);
     //   last.SetHeight(realheight);
     //   last.SetAge(Integer.parseInt(age));
        last.SetBadBorder(edgesproblem);
    //    last.SetBadHistory(history);
        last.SetColorCurve(colorproblem);
        last.SetEvolving(false);
      //  last.SetGender(sex);
        last.SetHasAsymmetry(edgessimilarityproblem);
        last.SetDLD(false); // the mole can upload again

        last.SetHasBlood(liquid);
        last.SetId(id);
        last.SetIsHard(ishard);
        last.SetHasItch(itch);
        last.SetMorfology(morfology);
        last.SetName(name);
        last.SetInPain(inpain);
        Log.d("id",Integer.toString(GetNewId()));
        Configurations def=db.getDEf();
         last.SetPID(def.GetPID());
         Log.d("id",Integer.toString(GetNewId()));
        def.idup();// up the counter of next available mole id

Mole old=db.getMole(id);// gets old instance of mole and compares with new one

        last.SetDiameter(diameter);
         Log.d("diamm",Double.toString(last.GetDiameter()));

        double oldDiameter =0;

        if (old.GetWidth()>old.Getheight())
            oldDiameter=old.GetWidth();
        else
            oldDiameter=old.Getheight();


        if(Math.abs(diameter-oldDiameter)>oldDiameter*0.3) // if there the new one .com is 30% bigger
        {

last.SetEvolving(true);// sets that is an evolving mole
        }
        if(old.GetEvolving()   )
        {

            last.SetEvolving(true);// if the old mole was evolving -> the new one will also
        }

       double oldMorfology [][]= old.GetMorfology();//checks new and older morphology's
      if   (MyLib.dangerousedges(morfology,oldMorfology,src) )
          last.SetEvolving(true);

        if (last.GetEvolving())//if evolving then is bad
            last.SetIsbad(true);


        db.updateMole(last);// update the mole in db

        Bitmap photo =Getbitmap(5);// reading the new image from storage
        savebitmap(photo,last.GetId());// updating old image in storage

        photo.recycle();// clean the photo from memory
        return last.GetId();// returns the id
    }

//this function Gets as input moles name
// returns the id of the mole
    /*
 the function creates a mole  instant , fills it with data , gets a new id and saves the instance to data base

     */
    public int CreateNewMole(String name){



        Mole last = new  Mole();

            if (realwidth>realheight)// calculates diameter
                last.SetDiameter(realwidth);
            else
                last.SetDiameter(realwidth);



            last.SetMole(Geturl(GetNewId()));// inputs data
            last.SetWidth(realwidth);
            last.SetHeight(realheight);
          //  last.SetAge(Integer.parseInt(age));
            last.SetBadBorder(edgesproblem);
          //  last.SetBadHistory(history);
            last.SetColorCurve(colorproblem);
            last.SetEvolving(false);
       //     last.SetGender(sex);
            last.SetHasAsymmetry(edgessimilarityproblem);

            last.SetHasBlood(liquid);
            last.SetId(GetNewId());

        Log.d("prin thn apothi",Boolean.toString(ishard));
            last.SetIsHard(ishard);
            last.SetHasItch(itch);
            //  last.SetMole();
            last.SetMorfology(morfology);
            last.SetName(name);
            last.SetInPain(inpain);
        last.SetIsbad(false);// the diagnosis is in next activity
        last.SetDLD(false);// the mole can upload

        Log.d("hard",Boolean.toString(last.GetIsHard()));

        Log.d("id",Integer.toString(GetNewId()));
        Configurations def=db.getDEf(); //gets apps configurations
        //def.idup();
        last.SetPID(def.GetPID());// sets next available id

       // def = new Configurations(Integer.parseInt(age),sex,history,GetNewId());
        Log.d("id",Integer.toString(GetNewId()));
    def.idup();//next available id gets bigger
      //  def.SetAge(Integer.parseInt(age));
    //    def.SetHistory(history);
    //    def.SetGender(sex);
        db.updateDef(def);// updates configurations


        db.addMole(last);// saves mole  in DB

        Bitmap photo =Getbitmap(5); //read the mole photo from storage
        savebitmap(photo,last.GetId()); //generates a unique dir and saves it

        photo.recycle();
       // Molestest(last);
        return last.GetId();// returns id

    }

/*
Ths function gets as input a id and generates a save dir
 */

    public String Geturl(int id) {

        File mediaStorageDir = new File( // gets apps private dir
                this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }// adds to dir ->SAVED+id+.nodata
        File pictureFile = new File(mediaStorageDir.getPath() + File.separator + "SAVED"+ Integer.toString(id) + ".nodata");



        //returns path
        return pictureFile.getPath();}




    public static final int MEDIA_TYPE_IMAGE = 1;

// this function gets an image which was saved temporary at previous intent
    /*
    returns the  image
     */
    public   Bitmap Getbitmap(int h) {
        Log.e(TAG, "geting bitmap");

        String NOMEDIA=" .nomedia";


        File mediaStorageDir = new File(
                this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());



        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }//creats the dir url
        File pictureFile =  new File(mediaStorageDir.getPath() + File.separator + "IMGtmp2" +Integer.toString(h)+ ".nodata");
        try {//
            imageStream = new FileInputStream(pictureFile);//loads the image to stream
            value = BitmapFactory.decodeStream(imageStream);//gets the image from stream

            value= Bitmap.createBitmap(
                    value,
                    0,
                    0,
                    value.getWidth()
                    ,
                    value.getHeight()
            );

// Bitmap lastBitmap = null;
//lastBitmap = Bitmap.createScaledBitmap(value, 2000, 2000, true);


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;// retutns image
    }
/*
this function gets as input a moles image and a moles id
 generates the saving dir
 and saves the image at the dir
 */
    public   void savebitmap(Bitmap data ,int id) {

        Randar.pictureFile = getOutputMediaFile(id);  //creates the file and links it
        NullPointerException e = null;
        if (Randar.pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: "
                            + e.getMessage());
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(Randar.pictureFile); // gets the image in stream

            data.compress(Bitmap.CompressFormat.JPEG, 100, fos);//set the compress type
            fos.flush();// save image and close the stream

            fos.close();

        } catch (FileNotFoundException e1) {
            Log.d(TAG, "File not found: " + e1.getMessage());
        } catch (IOException e1) {
            Log.d(TAG, "Error accessing file: " + e1.getMessage());
        }


    }
/*
This function gets a moles id and creates a file . Returns the file dir

 */
    public     File getOutputMediaFile(int  id) {

        String NOMEDIA=" .nomedia";//gets apps private id
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        if(!mediaStorageDir.exists())

        {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
        // Date());
        // creates the file  . the name of the image file will be SAVED+id+.nodata
        File pictureFile =  new File(mediaStorageDir.getPath()  + File.separator + "SAVED"+ Integer.toString(id) + ".nodata");


        return pictureFile;//returns the file

    }
    /*
    this function is the checkbutton listener
    // on change condition event  get as input the id of checkbox which changed
    // and the new condition and updates the correct static var .
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) { // if checked sets true
        switch(buttonView.getId()){
            case R.id.checkBox:
                //do stuff
                liquid=true;
                break;
            case R.id.checkBox12:
                liquid=true;
                //do stuff
                break;
            case R.id.checkBox333:
                liquid=true;
                //do stuff
                break;
            case R.id.checkBox2:
                itch=true;
                //do stuff
                break;
            case R.id.checkBox22:
                itch=true;

                //do stuff
                break;
            case R.id.checkBox2333:
                itch=true;

                //do stuff
                break;
            case R.id.checkBox3:
                ishard=true;

                //do stuff
                break;
            case R.id.checkBox32:
                ishard=true;

                //do stuff
                break;
            case R.id.checkBox3333:
                ishard=true;

                //do stuff
                break;
            case R.id.checkBox4:
                inpain=true;
                //do stuff
                break;
            case R.id.checkBox42:
                inpain=true;

                //do stuff
                break;
            case R.id.checkBox4333:
                inpain=true;


                //do stuff
                break;


        }











        } else {// if not sets false

            switch(buttonView.getId()){
                case R.id.checkBox:
                    liquid=false;
                    //do stuff
                    break;
                case R.id.checkBox12:
                    liquid=false;

                    //do stuff
                    break;
                case R.id.checkBox333:
                    liquid=false;

                    //do stuff
                    break;
                case R.id.checkBox2:
                    itch=false;

                    //do stuff
                    break;
                case R.id.checkBox22:
                    itch=false;

                    //do stuff
                    break;
                case R.id.checkBox2333:
                    itch=false;

                    //do stuff
                    break;
                case R.id.checkBox3:
                    ishard=false;

                    //do stuff
                    break;
                case R.id.checkBox32:
                    ishard=false;

                    //do stuff
                    break;
                case R.id.checkBox3333:
                    ishard=false;

                    //do stuff
                    break;
                case R.id.checkBox4:
                    inpain=false;

                    //do stuff
                    break;
                case R.id.checkBox42:
                    inpain=false;

                    //do stuff
                    break;
                case R.id.checkBox4333:
                    inpain=false;

                    //do stuff
                    break;




            }



        }



    }


}