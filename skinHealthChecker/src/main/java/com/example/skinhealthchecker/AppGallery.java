package com.example.skinhealthchecker;
/*
Είναι το activity που είναι υπεύθυνο για την εμφάνιση της συλλογής των αποθηκευμένων ελιών και των
 δεδομένων τους για τον κάθε χρήστη . Λαμβάνονται απο την βάση όλα τα αποθηκευμένα mole . Προβάλονται
 ένα κάθε φορά και  ακόμα προβάλεται μια εικόνα κατάστασης - διάγνωσης μαζί με τα χαρακτηριστικά μιας ελιάς.




 It is the activity that is responsible for displaying the collection of stored moles
  their data for each user. All saved moles are taken from the base. They are being implemented
  one at a time, and a status-diagnosis image along with the characteristics of an mole  is displayed.
 */
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.io.*;

public class AppGallery extends AppCompatActivity {
    List<Mole> mole ;  // list of moles
    Gallery galleryView;  // instance of gallery -> xml
    ImageView imgView;  //instance of imageview  -> xml
    int [] ids; // list of mole ids
    float initialX; //variable used for scroll function
   // private Cursor cursor;
    Bitmap value;//variable used to load the mole image
    String TAG; // for logging
    static final int MIN_DISTANCE = 150;  // min scrolling distance for changing image
  //  static String  DATA = new String();
    int max=0; //variable which one contains the count of the moles

    private  boolean langu;//variable used to load correct active language
    static InputStream imageStream;//variable used for loading mole images from storage
 int curr=0; // //variable used to  save current viewing mole id
    //images from drawable
    DatabaseHandler db; // database handler...
    Bitmap  [] imageResource; // list of  images of moles
    TextView textname; //variable used to  viewing the mole characteristics
    ImageView isok;//variable used to  viewing diagnosis image
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);  //attaching handler with the database
/*

start with detect specify what problems we should look for.
Methods whose names start with penalty specify what we should do when we detect a problem.
For example, detect everything and log anything that's found:

.detectAll()
     .penaltyLog()
     .build();
 */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Configurations def = db.getDEf();  // getting the default-last Configurations of the app from database
langu=def.GetLanguage();// getting the language state(gr-en)
        if (langu) // display the layout for  en or gr . if langu==1 then language = gr
        setContentView(R.layout.gallery);
        else
            setContentView(R.layout.galleryen);

        textname = (TextView) findViewById(R.id.propert);// linking the TextView propert
         isok = (ImageView) findViewById(R.id.isok);// linking the ImageView isok

       // db = new DatabaseHandler(this);
        if (GetNewId() < 0) //if nothing is written
            db.getWritableDatabase();// seting that handler can write the database


      //  Configurations def =db.getDEf();
        Profile pro =db.getProfile(def.GetPID()); // getting the app active profile
    //    mole = db.getAllMoles();

        mole = db.getAllProfileMoles(pro);// getting the list of the active profile moles



        if (mole.size() > 0) {  // if there is moles in the list then....
            imageResource = new Bitmap[mole.size()];// create a bitmap table with all moles

            max = mole.size(); // moles count
            ids = new int[mole.size()]; // creating a table of mole ids

            int temp = 0;
            for (int i = mole.size() - 1; i >= 0; i--) {


                imageResource[temp] = Getbitmap(mole.get(i).GetId());// getting the image
                ids[i] = temp; //getting the ids of moles order reverse row

                temp++;


            }


            imgView = (ImageView) findViewById(R.id.imageView); //linking the imageview with the xml
            galleryView = (Gallery) findViewById(R.id.gallery);//linking the galleryView with the xml

            imgView.setImageBitmap(imageResource[0]); //seting the viewing image of last captured mole
            galleryView.setAdapter(new myImageAdapter(this)); // This will bind to the Gallery view with a series of ImageView views
            movetoid(ids[0]); //display the data of last captured mole


            //gallery image onclick event
            galleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int i, long id) {
                    curr = i; // when a thumb image is clicked curr will contains the position of the image
                    movetoid(ids[curr]);//geting the id from  position/display the data of the mole which owning the id

              //      int imagePosition = i + 1;
                    imgView.setImageBitmap(imageResource[curr]); // displaying the image of the mole which owning the id

                    //     Toast.makeText(getApplicationContext(), "You have selected image = " + imagePosition, Toast.LENGTH_LONG).show();
                    getWindow().getDecorView().findViewById(R.id.updateframe).invalidate(); // updating the graphics

                }
            });

        }
            Button captureButton = (Button) findViewById(R.id.Delete); // if the delete button pressed
            captureButton.setFocusable(true);
            //		captureButton.setFocusableInTouchMode(true);
            captureButton.requestFocus();
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //

                    if (mole.size()>0){
                    db.deleteMole(mole.get(ids[curr]));// delete the active- viewing mole from database

                    finish();// restarting the activity
                    startActivity(getIntent());}
                    //

                }
            });

        Button ftp = (Button) findViewById(R.id.button); // if the ftp button is pressed
    ftp.setFocusable(true);
               //		captureButton.setFocusableInTouchMode(true);
        ftp.requestFocus();
        ftp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (mole.size()>0) // if there is moles stored

              ftpimage(mole.get(ids[curr])); //send the mole  by ftp
                //

            }
        });


            Button captureButton1 = (Button) findViewById(R.id.Back12); // if back button pressed
            captureButton1.setFocusable(true);
            //		captureButton.setFocusableInTouchMode(true);
            captureButton1.requestFocus();
            captureButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    gotostart(); // go to cameraactivity -> first actiivty


                    //

                }
            });



    }


    // create a new ImageView for each item referenced by the Adapter
    public class myImageAdapter extends BaseAdapter {
        private Context mcontext;

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mgalleryView = new ImageView(mcontext);
           // mgalleryView.setImageResource(imageResource[position]);

            mgalleryView.setImageBitmap(imageResource[position]);//Sets a Bitmap as the content of this ImageView.

//Gallery extends LayoutParams to provide a place to hold current Transformation information along with previous position/transformation info.


            mgalleryView.setLayoutParams(new Gallery.LayoutParams(150, 150));
            mgalleryView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);//Controls how the image should be resized or moved to match the size of this ImageView.

            //imgView.setImageResource(R.drawable.image_border);
            mgalleryView.setPadding(3, 3, 3, 3);//The padding is expressed in pixels for the left, top, right and bottom parts

            return mgalleryView;
        }



        public myImageAdapter(Context c) {
            mcontext = c;
        }//

        public int getCount() {   return imageResource.length;     } // returning the image list length

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
    }


    //this fuction returns the last created id  of mole
    public int GetNewId() {

        Configurations def = db.getDEf();
        int id = def.GetIdM();
        return id;

    }



    public Bitmap Getbitmap(int id) { // reading the image from the storage giving the id of mole image
        // returns the Bitmap of the image
        Log.e(TAG, "geting bitmap");

        String NOMEDIA=" .nomedia";

//	File mediaStorageDir = new File(
//				Environment
//						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES +NOMEDIA ),"MyCameraApp");
//geting the apps private  path
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {  // but normaly it will be created !!
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        File pictureFile =  new File(Geturl(id)); // link the image from storage with pictureFile
        // Geturl generating the dir of image  using only the id of the image
        try {// loads the image from storage and creating the image
            imageStream = new FileInputStream(pictureFile);
             value = BitmapFactory.decodeStream(imageStream);

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
        return value;
    }

    // Geturl generating the dir of image  using only the id of the image
// input: image id -> output : Directory position
    public String Geturl(int id) {
// getting the app private dir
        File mediaStorageDir = new File(
                this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        if (!mediaStorageDir.exists()) {
                 Log.d("MyCameraApp", "failed to create directory");
                return null;

        }// generating the image dir by adding the name of image . the image name will dir will be like-> SAVED+IDnum+.nodata
        File pictureFile = new File(mediaStorageDir.getPath() + File.separator + "SAVED"+ Integer.toString(id) + ".nodata");





        return pictureFile.getPath();}//returning the path



    //checking if there is a slide to right or left
    //if there is a slide then it moves to previous or next image
    @Override
    public boolean onTouchEvent(MotionEvent event) {  // when the screen is touched
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();// getting the first coordinate of touch
                break;
            case MotionEvent.ACTION_UP:
                float finalX = event.getX(); // getting the last coordinate of touch
                float deltaX = finalX - initialX; //calculating the distance


                if (Math.abs(deltaX) > MIN_DISTANCE) // if the absolute  distance is big enough
                {
                    // Left to Right swipe action
                    if (finalX > initialX) // calculating if the  last coordinate is bigger than first
                    //if so then moving to previous mole
                    {

                        if ((curr > 0 )&&(curr <= max-1)) {// checking if position is in limits

                            imgView.setImageBitmap(imageResource[curr - 1]);
                            movetoid(ids[curr-1]);
                            curr = curr - 1; }                     }

                    // Right to left swipe action
                    else
                    {
                        if ((curr >= 0 )&&(curr < max-1)) {// checking if position is in limits
                            //if so then moving to next mole

                            imgView.setImageBitmap(imageResource[curr
                                    +1]);
                            movetoid(ids[curr+1]);

                            curr = curr+ 1;

                        }                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return false;
    }


    private  void movetoid(int in) // viewing the information of mole which  one id = in
    {
        //    DATA="";

     //   Configurations def =db.getDEf();
      //  Profile pro =db.getProfile(def.GetPID());
        StringBuilder sb = new StringBuilder();
if (!langu) { //if language is english
    sb.append(" Name:\t");
    sb.append(mole.get(in).GetName());//getting the name
    sb.append(System.getProperty("line.separator"));
  //  sb.append(" Age:\t");
  //  sb.append(pro.GetAge());

 //   sb.append(System.getProperty("line.separator"));
 //   sb.append(" Gender:\t");
 //   sb.append(pro.GetSex());
  //  sb.append(System.getProperty("line.separator"));
    sb.append(" Doubtful Perimeter:\t");
    sb.append(mole.get(in).BadBorder);//getting the border status
    sb.append(System.getProperty("line.separator"));
 //   sb.append(" Bad history:\t");
//    sb.append(pro.GetHistory());
 //   sb.append(System.getProperty("line.separator"));
    sb.append(" Color Normal  :\t");
    if (mole.get(in).GetColorCurve() < 3)//getting the color status
        sb.append(true);
    else
        sb.append(false);

    sb.append(System.getProperty("line.separator"));
    sb.append(" Diameter:\t");//getting the mole diameter
    sb.append(mole.get(in).GetDiameter() / 10);
    sb.append(System.getProperty("line.separator"));
    sb.append(" Evolving:\t");
    sb.append(mole.get(in).GetEvolving());//getting the evolving status
    sb.append(System.getProperty("line.separator"));
    sb.append(" Has Asymmetry:\t");
    sb.append(mole.get(in).GetHasAsymmetry());//getting the asymmetry status
    sb.append(System.getProperty("line.separator"));
    sb.append(" Has blood:\t");
    sb.append(mole.get(in).GetHasBlood());//getting the blood status
    sb.append(System.getProperty("line.separator"));
    sb.append("Is Itching:\t");//getting the inch status
    sb.append(mole.get(in).GetHasItch());
    sb.append(System.getProperty("line.separator"));
    sb.append(" It hurts:\t");
    sb.append(mole.get(in).GetInPain());//getting the inpain status
    sb.append(System.getProperty("line.separator"));

    sb.append(" It's Hard:\t");
    sb.append(mole.get(in).GetIsHard());//getting the ishard status
    sb.append(System.getProperty("line.separator"));
    sb.append("Has Uploaded to server:\t");//getting the if the mole has uploaded to server status
    //  sb.append(mole.get(in).GetIsHard());
    sb.append(mole.get(in).GetDLD());


    sb.append(System.getProperty("line.separator"));
} else // if the active language is greek
{
    sb.append(" Όνομα:\t"); //setting the same information but in greek language
    sb.append(mole.get(in).GetName());
    sb.append(System.getProperty("line.separator"));
   // sb.append(" Ηλικία:\t");
  //  sb.append(pro.GetAge());

 //   sb.append(System.getProperty("line.separator"));
 //   sb.append(" Φύλο:\t");
 //   sb.append(pro.GetSex());
 //   sb.append(System.getProperty("line.separator"));
    sb.append(" Αμφίβολη Περίμετρος:\t");

    if (mole.get(in).BadBorder)
    sb.append("Ναι");
    else
        sb.append("Όχι");

    sb.append(System.getProperty("line.separator"));
 //   sb.append(" Κακό Ιστορικό:\t");
 //   if (pro.GetHistory())
  //      sb.append("Ναι");
 //   else
 //       sb.append("Όχι");


  //  sb.append(System.getProperty("line.separator"));
    sb.append(" Χρωματικά Φυσιολογικός  :\t");
    if (mole.get(in).GetColorCurve() < 3)
        sb.append("Ναι");
    else
        sb.append("Όχι");

    sb.append(System.getProperty("line.separator"));
    sb.append(" Διάμετρος:\t");
    sb.append(mole.get(in).GetDiameter() / 10);
    sb.append(System.getProperty("line.separator"));
    sb.append(" Εξελίσσεται:\t");
   // sb.append();
    if (mole.get(in).GetEvolving())
        sb.append("Ναι");
    else
        sb.append("Όχι");


    sb.append(System.getProperty("line.separator"));
    sb.append(" Έχει Ασυμμετρία:\t");

    if (mole.get(in).GetHasAsymmetry())
        sb.append("Ναι");
    else
        sb.append("Όχι");

    sb.append(System.getProperty("line.separator"));
    sb.append(" Έχει Αίμα:\t");
   // sb.append(mole.get(in).GetHasBlood());
    if (mole.get(in).GetHasBlood())
        sb.append("Ναι");
    else
        sb.append("Όχι");

    sb.append(System.getProperty("line.separator"));
    sb.append(" Έχει Φαγούρα:\t");
  //  sb.append(mole.get(in).GetHasItch());
    if (mole.get(in).GetHasItch())
        sb.append("Ναι");
    else
        sb.append("Όχι");

    sb.append(System.getProperty("line.separator"));
    sb.append(" Πονάει:\t");
  //  sb.append(mole.get(in).GetInPain());
    if (mole.get(in).GetInPain())
        sb.append("Ναι");
    else
        sb.append("Όχι");
    sb.append(System.getProperty("line.separator"));

    sb.append(" Είναι Σκληρός:\t");
  //  sb.append(mole.get(in).GetIsHard());
    if (mole.get(in).GetIsHard())
        sb.append("Ναι");
    else
        sb.append("Όχι");
    sb.append(System.getProperty("line.separator"));
    sb.append("Έχει ανεβέι στον σερβερ:\t");
    //  sb.append(mole.get(in).GetIsHard());
    if (mole.get(in).GetDLD())
        sb.append("Ναι");
    else
        sb.append("Όχι");
    sb.append(System.getProperty("line.separator"));


}
       //  sb.append(" Όνομα:\t");             sb.append(mole.get(in));
       // sb.append(System.getProperty("line.separator"));


        Log.d("hard",Boolean.toString(mole.get(in).GetIsbad()));
        Log.d("is",String.valueOf(mole.get(in).GetId()));
        if (!mole.get(in).GetIsbad())// if the diagnosis is positive then the mole will have one green ν image  near the characteristics
        isok.setImageResource(R.drawable.n);//else will have X image
else
        isok.setImageResource(R.drawable.x);


        textname.setText(sb.toString());// setting the text of mole characteristics
        getWindow().getDecorView().findViewById(R.id.updateframe).invalidate();//updating graphics


    }

// this function is starting the first activity(start screen)
    public void gotostart() {

        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(intent);


    }

    @Override
    public void onBackPressed() {// if the android back button is pressed
        gotostart();

    }
    @Override
    protected void onDestroy() { //if app close deleting all bitmaps from memory
        super.onDestroy();
        try {
            for (int i = 0; i < imageResource.length; i++)
                imageResource[i].recycle();

        }
        catch (NullPointerException e){



        }
    }

    //This function gets input of a mole instance . It sends thw mole data ( image and characteristics )
    // first the function creates a file to storage called INFO.nodata
    //then connects to the  ftp server and sends INFO.data and SAVED.data (image)

    public   void ftpimage(Mole mole){

        FTPClient ftpClient = new FTPClient(); // is the ftp client incant
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS"); //this date format will be used for creating timestamp
        Configurations def = db.getDEf();// loading app init configurations

        String tempstring =Getinfo(mole);  //creates string of mole data
        String temp=  saveinfo(tempstring,mole.GetId()); // creates INFO.no data file .the returning ver  "temp" contains   the saved dir
        if (mole.GetDLD()){//if the mole rules is not right
            if (!langu)// prints to screen message
                Toast.makeText(getApplicationContext(), "This mole record has already uploaded!The dermatologist will check the data!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Τα δεδομένα αυτού του σπίλου έχουν ανεβεί ήδη μια φορά στον Server.Ο δερματολόγος θα τα ελένξει !", Toast.LENGTH_LONG).show();


        }
        else if (def.GetPID()==0){// cant upload using DEFAuLT profile

            if (!langu)
                Toast.makeText(getApplicationContext(), "You cant upload a mole record by using DEFAULT profile.Please create or choose  another !", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Δεν μπορείτε να ανεβάσετε μια εγγραφή σπίλου χρησιμοποιώντας το προφίλ DEFAULT.Παρακαλώ δημιουργήστε ή επιλέξτε ένα άλλο !", Toast.LENGTH_LONG).show();
        }

        else {// if mole is edible to uploaded , the app creates ftp connection


            try {
                ftpClient.connect("aigroup.ceid.upatras.gr", 21);  //imports  server's url and port
                ftpClient.login("aig01", "aig842");//importing  server's login data
                ftpClient.changeWorkingDirectory("/files/");// set that data will saved to folder "files"
                File mediaStorageDir = new File(this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
                // mediaStorageDir=app private dir
                if (!mediaStorageDir.exists())

                {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("MyCameraApp", "failed to create directory");
                        //   return null;
                    }
                }
                Log.d("ftp", ftpClient.getReplyString());

                if (ftpClient.getReplyString().contains("250") || ftpClient.getReplyString().contains("230")) {
/// if confection established successfully
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);  //FTP.BINARY_FILE_TYPE, is used for file transfer
                    //     BufferedInputStream buffIn = null;
                    //    buffIn = new BufferedInputStream(new FileInputStream(mediaStorageDir.getPath()  + File.separator + "SAVED"+ Integer.toString(mole.GetId()) + ".nodata"));
                    ftpClient.enterLocalPassiveMode(); //Set the current data connection mode to PASSIVE_LOCAL_DATA_CONNECTION_MODE .

                    //    Handler progressHandler=null;
                    //     ProgressInputStream progressInput = new ProgressInputStream(buffIn, progressHandler);
                    Log.d("ftp", mediaStorageDir.getPath() + File.separator + "SAVED" + Integer.toString(mole.GetId()) + ".nodata");


                    // sending the image
                    FileInputStream srcFileStream = new FileInputStream(mediaStorageDir.getPath() + File.separator + "SAVED" + Integer.toString(mole.GetId()) + ".nodata");
                    // the name of sending file includes timestamp on server side
                    boolean result = ftpClient.storeFile("SAVED" + dateFormat.format(new Date()) + Integer.toString(mole.GetId()), srcFileStream);
                    // sending the INFO

                    FileInputStream srcFileStream2 = new FileInputStream(temp);
                    // the name of sending file includes timestamp on server side

                    boolean result2 = ftpClient.storeFile("INFO" + dateFormat.format(new Date()) + Integer.toString(mole.GetId()), srcFileStream2);
                    Log.d("ftp", Integer.toString(ftpClient.getReplyCode()));


                    //   buffIn.close();
                    ftpClient.logout();///closes connections
                    ftpClient.disconnect();
                    if (result && result2) { // prints message to user about the conclusion of ftp transfer
                        if (!langu)
                            Toast.makeText(getApplicationContext(), "Upload successful !The dermatologist will check the data!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "Τα δεδομένα στάλθηκαν στον δερματολόγο !", Toast.LENGTH_LONG).show();
                        mole.SetDLD(true);
                     //   mole.SetIsHard(true);

                        db.updateMole(mole);// updating the data base that correct mole has uploaded ones

                 //       Mole test = db.getMole(mole.GetId());
                 //       Toast.makeText(getApplicationContext(), Boolean.toString(test.GetDLD()), Toast.LENGTH_LONG).show();
                   //     Toast.makeText(getApplicationContext(), Boolean.toString(test.GetIsHard()), Toast.LENGTH_LONG).show();

                    }
                }

            } catch (SocketException e) {
                Log.e(" FTP", e.getStackTrace().toString());
            } catch (UnknownHostException e) {
                Log.e(" FTP", e.getStackTrace().toString());
            } catch (IOException e) {
                Log.e("FTP", e.getStackTrace().toString());
                System.out.println("IO Exception!");
            }

        }

    }

    public static final int MEDIA_TYPE_IMAGE = 1;


    // the saveinfo function gets input of mole's info and mole's id number and creates INFO.nodata file
    //which saves to app storage dir
    public   String saveinfo(String data ,int id) {
        PrintWriter writer=null;
      //  Log.d("ftp",data);

        File   file = getOutputMediaFile(MEDIA_TYPE_IMAGE,id); //creating the file in app's directory
        NullPointerException e = null;
        if (file == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: "
                            + e.getMessage());
            return null;
        }

        try {
           // FileOutputStream fos = new FileOutputStream(file);
            writer = new PrintWriter(file ); //writing data
            writer.print(data);
            writer.flush();
            writer.close();
             // fos.write(data.getBytes());
          //  fos.flush();

          //  fos.close();

        } catch (FileNotFoundException e1) {
            Log.d(TAG, "File not found: " + e1.getMessage());
        } catch (IOException e1) {
            Log.d(TAG, "Error accessing file: " + e1.getMessage());
        }

      return  file.getAbsolutePath();
    }


    // The getOutputMediaFile function returns the app private dir
    public     File getOutputMediaFile(int type,int  id) {

        String NOMEDIA=" .nomedia";
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
        File pictureFile =  new File(mediaStorageDir.getPath()  + File.separator + "INFO" + ".nodata");


        return pictureFile;

    }


    // The Getinfo function get's input of a mole instance and returns a string containing mole's and active profile's info
    private String Getinfo (Mole mole) {
Profile prof1 ;
        Configurations def1 =db.getDEf();
        prof1= db.getProfile(def1.GetPID());

        StringBuilder sb = new StringBuilder();//generating string

        sb.append(" Όνομα - Επωνυμο :\t");
        sb.append(prof1.GetName());
        sb.append(System.getProperty("line.separator"));

        sb.append(" Mail :\t");
        sb.append(prof1.GetMail());
        sb.append(System.getProperty("line.separator"));

        sb.append(" Αρ.Τηλεφώνου:\t");
        sb.append(prof1.GetPhone());
        sb.append(System.getProperty("line.separator"));

        sb.append(" Ηλικία:\t");
           sb.append(prof1.GetAge());

          sb.append(System.getProperty("line.separator"));
        sb.append(" Ψευδώνυμο  Σπίλου:\t");
        sb.append(mole.GetName());
        sb.append(System.getProperty("line.separator"));

           sb.append(" Φύλο:\t");
           sb.append(prof1.GetSex());
          sb.append(System.getProperty("line.separator"));
        sb.append(" Αμφίβολη Περίμετρος:\t");

        if (mole.BadBorder)
            sb.append("Ναι");
        else
            sb.append("Όχι");

        sb.append(System.getProperty("line.separator"));
           sb.append(" Κακό Ιστορικό:\t");
        sb.append(System.getProperty("line.separator"));

        if (prof1.GetHistory())
              sb.append("Ναι");
            else
               sb.append("Όχι");


          sb.append(System.getProperty("line.separator"));
        sb.append("Χρωματικά Φυσιολογικός  :\t");
        if (mole.GetColorCurve() < 3)
            sb.append("Ναι");
        else
            sb.append("Όχι");

        sb.append(System.getProperty("line.separator"));
        sb.append(" Διάμετρος:\t");
        sb.append(mole.GetDiameter() / 10);
        sb.append(System.getProperty("line.separator"));
        sb.append(" Εξελίσσεται:\t");
        // sb.append();
        if (mole.GetEvolving())
            sb.append("Ναι");
        else
            sb.append("Όχι");


        sb.append(System.getProperty("line.separator"));
        sb.append(" Έχει Ασυμμετρία:\t");

        if (mole.GetHasAsymmetry())
            sb.append("Ναι");
        else
            sb.append("Όχι");

        sb.append(System.getProperty("line.separator"));
        sb.append(" Έχει Αίμα:\t");
        // sb.append(mole.get(in).GetHasBlood());
        if (mole.GetHasBlood())
            sb.append("Ναι");
        else
            sb.append("Όχι");

        sb.append(System.getProperty("line.separator"));
        sb.append(" Έχει Φαγούρα:\t");
        //  sb.append(mole.get(in).GetHasItch());
        if (mole.GetHasItch())
            sb.append("Ναι");
        else
            sb.append("Όχι");

        sb.append(System.getProperty("line.separator"));
        sb.append(" Πονάει:\t");
        //  sb.append(mole.get(in).GetInPain());
        if (mole.GetInPain())
            sb.append("Ναι");
        else
            sb.append("Όχι");
        sb.append(System.getProperty("line.separator"));

        sb.append(" Είναι Σκληρός:\t");
        //  sb.append(mole.get(in).GetIsHard());
        if (mole.GetIsHard())
            sb.append("Ναι");
        else
            sb.append("Όχι");
        sb.append(System.getProperty("line.separator"));



        return sb.toString();
    }
}