package com.example.skinhealthchecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.example.skinhealthchecker.Mole;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import static android.graphics.Color.BLUE;
import static android.provider.Telephony.Carriers.PASSWORD;

/**
 * Με βάση τα αποθηκευμένα στοιχεία που έχει η εφαρμογή (Αν υπάρχουν από προηγούμενη ανάλυση της ελιάς . )
 * ,σε σύγκριση με τα νέα και λαμβάνοντας υπόψη τα δεδομένα από το ερωτηματολόγιο γίνεται η εμφάνιση
 * μιας συμβουλευτικής διάγνωσης για τον χρήστη .
 * Τα δεδομένα εμφανίζονται στην οθόνη μαζί και με μια εικόνα που αντιστοιχεί στην διάγνωση
 * Υπάρχει δυνατότητα συνδεσης ftp με server.



 * Based on the stored data of the application (If there is a previous analysis of the mole.)
 *, compared with the new ones and taking into account the data from the questionnaire,
 * a diagnostic consultation for the user occurs.
 *  * Τα δεδομένα εμφανίζονται στην οθόνη μαζί και με μια εικόνα που αντιστοιχεί στην διάγνωση
 * Υπάρχει δυνατότητα συνδεσης ftp με server.


 */

public class Diagnosis extends Activity  {
    private boolean langu;// contains the active language
    private Boolean delete;//if yes then all data will destroyed after diagnosis
    String TAG;// string for logging
    DatabaseHandler db;// database handler
    int id;// the id of the mole which will be diagnosed
TextView textname;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {  final Mole mole;// creating new instance
        TAG="Diagnosis";
        Log.i(TAG, "onCreate");
        db = new DatabaseHandler(this);//opening the database
        super.onCreate(savedInstanceState);
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
        Configurations def = db.getDEf(); // getting the starting configurations

        langu=def.GetLanguage();// get the active language
        if (langu)// if greek
        setContentView(R.layout.diagnosis);
        else// if english
            setContentView(R.layout.diagnosisen);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if(data != null)// getting the data from priviews activity
        {
             delete = intent.getBooleanExtra("delete",false); // use your data type

            id=intent.getIntExtra("id",0);// getting the id of the mole which will be diagnosed
        }
        else
        {
            // No extra received
        }




      //  if (GetNewId()<=0)
            db.getWritableDatabase(); // set the db writable




          mole  =       db.getMole(id );// getting the mole's info
        movetoid(mole); // printing the info to screen

             Button captureButton32 = (Button) findViewById(R.id.start);
        captureButton32.setFocusable(true);
        //captureButton.setFocusableInTouchMode(true);
        captureButton32.requestFocus();
        captureButton32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //returning to first activity(camera one)

                Intent intent23 = new Intent(getApplicationContext(), CameraActivity.class);

                if(delete)// if user had set to delete the mole after diagnosis then the mole deleted
                    db.deleteMole(mole);


                startActivity(intent23);
                //

            }
        });

        Button captureButton33 = (Button) findViewById(R.id.button4);
        captureButton33.setFocusable(true);
        //captureButton.setFocusableInTouchMode(true);
        captureButton33.requestFocus();
        captureButton33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the send button pressed-> sending the mole to server
                ftpimage(mole);
                //

            }
        });

        /** ArrayList<Mole> MoleList = new ArrayList<Mole>();

         for (Mole item : MoleList)
         {
             MoleList.add(item);
         }
 **/


    }
    // this function gets from DB and returns the last used mole id
    public int GetNewId() {

        Configurations def = db.getDEf();
        int id = def.GetIdM();
        return id;

    }


//This function  set an image to screen
    private void healthy (){

//if the mole seems to be healthy , print the okrd or goodmole image(depending if the active language is gr or en)
        ImageView img= (ImageView) findViewById(R.id.health);
       if(langu)
        img.setImageResource(R.drawable.okrd);
        else
           img.setImageResource(R.drawable.goodmole);

    }
//This function  set an image to screen

    private void nothealthy (){

//if the mole seems to be healthy , print the notok or nogoodmole image(depending if the active language is gr or en)

        ImageView img= (ImageView) findViewById(R.id.health);
        if(langu)
        img.setImageResource(R.drawable.notok);
        else
            img.setImageResource(R.drawable.nogoodmode);
        //   img.setImageResource(R.mipmap.nogooden);
    }


    //This function gets as input a mole instance and returns true if the mole diagnosis is bad
    //otherwise false
    private boolean checkisbad (Mole m){
// there is a counter which one collecting points if any of mole properties is out of limits
        int count=0;

        if (m.GetEvolving())// if there is evolving issue then add to count 50 points
            count=count+50;
        if (m.GetIsHard())// if there is hard issue then add to count 100 points
            count=count+100;


        if (m.GetInPain())// if there is pain issue then add to count 100 points
            count=count+100;

        if (m.GetHasItch())// if there is itch issue then add to count 100 points
            count=count+100;

        if (m.GetHasBlood())// if there is blood issue then add to count 100 points
            count=count+100;


        if (m.GetHasAsymmetry())// if there is asymmetry issue then add to count 20 points
            count=count+20;

        if(m.GetDiameter()>60)// if there is big diameter issue then add to count 49 points
            count=count+49;

        if(m.GetDiameter()>65)// if there is bigger diameter issue then add to count 80 points
            count=count+80;
        if(m.GetBadBorder())// if there is badborder issue then add to count 50 points
            count=count+50;

        if(m.GetBadHistory(m,db))// if there is BadHistory issue then add to count 31 points
            count=count+31;


        if(m.GetColorCurve()>2)// if there is color curve issue then add to count 50 points
            count=count+50;



        if (count>49) // if the count is bigger than 49 then mole is maybe bad
             return  true;
    else
             return false;


    }



    //This function gets as input a mole instance and  prints to the xml's textview the mole's data
    //The data will print using greek or english language depending the active one

    //It creates a string var and step by step filling it with data
    private  void movetoid(Mole mole)
    {
        Configurations def =db.getDEf();
        Profile pro =db.getProfile(def.GetPID());
     String   DATA="";
        textname = (TextView) findViewById(R.id.molestat);

        StringBuilder sb = new StringBuilder();

        if (!langu) {
            sb.append(" Name:\t");
            sb.append(mole.GetName());
            sb.append(System.getProperty("line.separator"));
            //  sb.append(" Age:\t");
            //  sb.append(pro.GetAge());

            //   sb.append(System.getProperty("line.separator"));
            //   sb.append(" Gender:\t");
            //   sb.append(pro.GetSex());
            //  sb.append(System.getProperty("line.separator"));
            sb.append(" Doubtful Perimeter:\t");
            sb.append(mole.BadBorder);
            sb.append(System.getProperty("line.separator"));
            //   sb.append(" Bad history:\t");
//    sb.append(pro.GetHistory());
            //   sb.append(System.getProperty("line.separator"));
            sb.append(" Color Normal  :\t");
            if (mole.GetColorCurve() < 3)
                sb.append(true);
            else
                sb.append(false);

            sb.append(System.getProperty("line.separator"));
            sb.append(" Diameter:\t");
            sb.append(mole.GetDiameter() / 10);
            sb.append(System.getProperty("line.separator"));
            sb.append(" Evolving:\t");
            sb.append(mole.GetEvolving());
            sb.append(System.getProperty("line.separator"));
            sb.append(" Has Asymmetry:\t");
            sb.append(mole.GetHasAsymmetry());
            sb.append(System.getProperty("line.separator"));
            sb.append(" Has blood:\t");
            sb.append(mole.GetHasBlood());
            sb.append(System.getProperty("line.separator"));
            sb.append("Is Itching:\t");
            sb.append(mole.GetHasItch());
            sb.append(System.getProperty("line.separator"));
            sb.append(" It hurts:\t");
            sb.append(mole.GetInPain());
            sb.append(System.getProperty("line.separator"));

            sb.append(" It's Hard:\t");
            sb.append(mole.GetIsHard());
            sb.append(System.getProperty("line.separator"));
        } else {
            sb.append(" Όνομα:\t");
            sb.append(mole.GetName());
            sb.append(System.getProperty("line.separator"));
            // sb.append(" Ηλικία:\t");
            //  sb.append(pro.GetAge());

            //   sb.append(System.getProperty("line.separator"));
            //   sb.append(" Φύλο:\t");
            //   sb.append(pro.GetSex());
            //   sb.append(System.getProperty("line.separator"));
            sb.append(" Αμφίβολη Περίμετρος:\t");

            if (mole.BadBorder)
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
        }
            ;
        //  sb.append(" Όνομα:\t");             sb.append(mole.get(in));
        // sb.append(System.getProperty("line.separator"));


        textname.setText(sb.toString());// prints data to text view






       mole.SetIsbad(checkisbad(mole));// calls the function which makes diagnosis and
        // sets the mole 's status(healthy or nor)




        if (mole.GetIsbad())// if mole is bad , printing the correct image
        nothealthy();
            else
        healthy ();


        getWindow().getDecorView().findViewById(R.id.corframe).invalidate();//updating graphics

        if (!delete) {//if  the mole's record will stay in database then it will updates it (about the mole's health)
            Log.d("update",Boolean.toString(mole.GetIsbad()));

            db.updateMole(mole);
        }

    }
    //This function gets input of a mole instance . It sends thw mole data ( image and characteristics )
    // first the function creates a file to storage called INFO.nodata
    //then connects to the  ftp server and sends INFO.data and SAVED.data (image)
    public   void ftpimage(Mole mole){

        FTPClient ftpClient = new FTPClient(); // is the ftp client incant
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");//this date format will be used for creating timestamp
        Configurations def = db.getDEf();// loading app init configurations

        String tempstring =Getinfo(mole);//creates string of mole data
        String temp=  saveinfo(tempstring,mole.GetId());// creates INFO.no data file .the returns ver  "temp" contains   the saved dir
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
                ftpClient.connect("aigroup.ceid.upatras.gr", 21);//imports server's url and port
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
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//FTP.BINARY_FILE_TYPE, is used for file transfer
                    //     BufferedInputStream buffIn = null;
                    //    buffIn = new BufferedInputStream(new FileInputStream(mediaStorageDir.getPath()  + File.separator + "SAVED"+ Integer.toString(mole.GetId()) + ".nodata"));
                    ftpClient.enterLocalPassiveMode();//Set the current data connection mode to PASSIVE_LOCAL_DATA_CONNECTION_MODE .
                    //    Handler progressHandler=null;
                    //     ProgressInputStream progressInput = new ProgressInputStream(buffIn, progressHandler);
                    Log.d("ftp", mediaStorageDir.getPath() + File.separator + "SAVED" + Integer.toString(mole.GetId()) + ".nodata");
                    // sending the image

                    FileInputStream srcFileStream = new FileInputStream(mediaStorageDir.getPath() + File.separator + "SAVED" + Integer.toString(mole.GetId()) + ".nodata");
                    // the name of sending file includes timestamp on server side

                    boolean result = ftpClient.storeFile("SAVED" + dateFormat.format(new Date()) + Integer.toString(mole.GetId()), srcFileStream);
                    // sends the INFO

                    FileInputStream srcFileStream2 = new FileInputStream(temp);
                    boolean result2 = ftpClient.storeFile("INFO" + dateFormat.format(new Date()) + Integer.toString(mole.GetId()), srcFileStream2);
                    Log.d("ftp", Integer.toString(ftpClient.getReplyCode()));
                    // the name of sending file includes timestamp on server side


                    //   buffIn.close();
                    ftpClient.logout();///closes connections
                    ftpClient.disconnect();
                    if (result && result2) {
                        if (!langu)// prints message to user about the conclusion of ftp transfer
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
    //    Log.d("ftp",data);

        File   file = getOutputMediaFile(MEDIA_TYPE_IMAGE,id);//creating the file in app's directory
        NullPointerException e = null;
        if (file == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: "
                            + e.getMessage());
            return null;
        }

        try {
            // FileOutputStream fos = new FileOutputStream(file);
            writer = new PrintWriter(file );//writing data
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
