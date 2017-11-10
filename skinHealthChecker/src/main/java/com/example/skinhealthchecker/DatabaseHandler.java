package com.example.skinhealthchecker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chief on 2/4/2017.
 *
 * Το κομμάτι αυτό του κώδικα αφορά τον διαχειρηστή της βάσης δεδομένων.
 * Περιέχει κώδικα για την αρχικοποίηση δημιουργεία τραπεζιών
 *   εισαγωγή-ανανέωση- διαγραφή γραμμών και ότι άλλο είναι σχετικό με την βάση.
 *
 *   Περιέχονται 3 κύρια tables :
 *   Moles -> αφορά τα moles
 *  Profil->αφορά τα προφίλ
 *  Def-> αφορά τις πληροφορίες που πρέπει να διαθέτει- αποθηκευει η εφαρμογή.
 *
 *  * This piece of code refers to the database handler.
   * Contains code for initializing table creations
   * insertion-refresh-deletion of rows and another is relative staff to the base.
   *
   * 3 main tables are included:
   Moles ->refers to Moles
   * Profile->refers to Profiles
   * Def-> refers to the information it needs to store - the application stores.
 */

public class DatabaseHandler  extends SQLiteOpenHelper {

    //String FILENAME = "";
   // DatabaseHandler db;//possibly use asynctask
    //String sQuery = "";

        // All Static variables
        // Database Version
        private static final int DATABASE_VERSION = 9;

        // Database Name
        private static final String DATABASE_NAME = "Mole";

        // Contacts table name
        private static final String TABLE_CONTACTS = "Moles";
    private static final String TABLE_DEF = "Def";
    private static final String TABLE_PROFIL = "PROFIL";

        // Contacts Table Columns names
        public static final String Mole = "Mole";//First table name
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String AGE = "AGE";
    public static final String ITCH = "ITCH";
    public static final String ISHARD = "ISHARD";
    public static final String HASBLOD = "HASBLOD";
    public static final String ISBAD = "ISBAD";
    public static final String DLD = "DLD";

    public static final String INPAIN = "INPAIN";
    public static final String SEX = "SEX";
    public static final String REALWIDTH = "REALWIDTH";
    public static final String REALHEIGHT = "REALHEIGHT";
    public static final String EDGESPROBLEM = "EDGESPROBLEM";
    public static final String COLORPROBLEM = "COLORPROBLEM";
    public static final String EDGESSIMILATIRY = "EDGESSIMILATIRY";
    public static final String HISTORY = "HISTORY";
    public static final String EVOLVING = "EVOLVING";

    public static final String MORFOLOGY1 = "MORFOLOGY1";
    public static final String MORFOLOGY2 = "MORFOLOGY2";

    public static final String DIAMETER = "DIAMETER";
    public static final String PHOTO = "PHOTO";

    public static final String IDM = "IDM";
    public static final String PIDCOUnt="PIDC";

    public static final String PID = "PID";
    public static final String profilname ="PROFILNAME" ;
    public static final  String name ="NAME"  ;
    public static final String age ="AGE";
    public static final String mail="MAIL" ;
    public static final  String phone ="PHONE";
    public static final  String sex="SEX";
    public static final  String history="HISTORY";
    public static final String LANG="LANG";

    public DatabaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                    + ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
                    +   ITCH + " NUMERIC," + ISHARD + " NUMERIC," + HASBLOD + " NUMERIC," + INPAIN + " NUMERIC," +
                     REALWIDTH + " REAL," + REALHEIGHT + " REAL," + EDGESPROBLEM + " NUMERIC," +
                    COLORPROBLEM + " INTEGER," + EDGESSIMILATIRY + " NUMERIC," +MORFOLOGY1 + " BLOB," +MORFOLOGY2
                    + " BLOB," + DIAMETER + " REAL," + PHOTO + " TEXT," +ISBAD +" NUMERIC," + PID +" NUMERIC,"+ DLD +" NUMERIC," + EVOLVING
                    +" NUMERIC" +")";
            db.execSQL(CREATE_CONTACTS_TABLE);

            String CREATE_CONTACTS_TABLE2 = "CREATE TABLE " + TABLE_DEF + "("

                    + ID + " INTEGER PRIMARY KEY," + IDM +" INTEGER, "+PID +" INTEGER, "+ LANG + " NUMERIC,"+ PIDCOUnt +" INTEGER "+ ")";
            db.execSQL(CREATE_CONTACTS_TABLE2);

            Configurations mole = new Configurations();
            ContentValues cv = new ContentValues();
            cv.put(ID, Integer.toString(1));

         //   cv.put(AGE, Integer.toString( 0));
         //   cv.put(SEX, "Γυναίκα");
         //   cv.put(HISTORY, Boolean.toString(false));
            cv.put(IDM,String.valueOf(1000)); // starting value of id
            cv.put(PID,String.valueOf(0));
            cv.put(LANG,Boolean.toString(false));
            cv.put(PIDCOUnt,String.valueOf(0));

            Log.d("SQLlite","created");

            long rowID = db.insertOrThrow(TABLE_DEF, null, cv);


            String CREATE_CONTACTS_TABLE3 = "CREATE TABLE " + TABLE_PROFIL + "("
                    + PID + " INTEGER PRIMARY KEY,"  + AGE + " INTEGER,"+  SEX + " TEXT,"  +HISTORY + " NUMERIC," + profilname +" TEXT ,"
                    + name +" TEXT ,"+ mail +" TEXT, "+ phone +" TEXT "+ ")";
            db.execSQL(CREATE_CONTACTS_TABLE3);


            Profile prof = new Profile();
            ContentValues cv2 = new ContentValues();
            cv2.put(PID, Integer.toString(0));
            cv2.put(profilname, "DEFAULT");// creates default profile

            cv2.put(AGE, Integer.toString( 0));
            cv2.put(SEX, "Γυναίκα");
            cv2.put(HISTORY, Boolean.toString(false));
            cv2.put(name,"DEFAULT");
            cv2.put(mail,"DEFAULT@def.gr");
            cv2.put(phone,"phone");

            Log.d("SQLlite","created");

            long rowID2 = db.insertOrThrow(TABLE_PROFIL, null, cv2);




        }



        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEF);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFIL);


            // Create tables again
            onCreate(db);
        }


        // + PID + " INTEGER PRIMARY KEY,"  + AGE + " INTEGER,"+  SEX + " TEXT,"  +HISTORY + " NUMERIC," + profilname +" TEXT "
      //              + name +" TEXT "+ mail +" TEXT "+ phone +" TEXT "+ ")";
//
    //adding a new profile
    public void addprofile(Profile mole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PID, Integer.toString(mole.GetIdM()));
        values.put(AGE,Integer.toString( mole.GetAge()));
        values.put(SEX, mole.GetSex());
        values.put(HISTORY,  Boolean.toString(mole.GetHistory()));
        values.put(profilname, mole.GetProfilname());



        values.put(name, mole.GetName());

        values.put(mail, mole.GetMail());
        values.put(phone, mole.GetPhone());


        // Inserting Row
        db.insert(TABLE_PROFIL, null, values);
        db.close(); // Closing database connection
    }

    // Adding new mole


    public void addMole(Mole mole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, Integer.toString(mole.GetId()));
        values.put(NAME, mole.GetName());
        //values.put(AGE,Integer.toString( mole.GetAge()));
        values.put(ITCH, Boolean.toString(mole.GetHasItch()));
        values.put(ISHARD,  Boolean.toString(mole.GetIsHard()));
        values.put(HASBLOD,  Boolean.toString(mole.GetHasBlood()));

        values.put(INPAIN, Boolean.toString( mole.GetInPain()));
       // values.put(SEX, mole.GetGender());
        values.put(REALWIDTH, Double.toString(mole.GetWidth()));
        values.put(REALHEIGHT,Double.toString( mole.Getheight()));
        values.put(EDGESPROBLEM, Boolean.toString(mole.GetBadBorder()));
        values.put(COLORPROBLEM,Double.toString( mole.GetColorCurve()));
        values.put(EDGESSIMILATIRY,Boolean.toString( mole.GetHasAsymmetry()));
        values.put(MORFOLOGY1, mole.GetMorfologyAr1());
        values.put(MORFOLOGY2, mole.GetMorfologyAr2());
        values.put(DIAMETER, Double.toString(mole.GetDiameter()));
        values.put(PHOTO, mole.GetMole());
       // values.put(HISTORY,  Boolean.toString(mole.GetBadHistory()));
        values.put(ISBAD, Boolean.toString( mole.GetIsbad()));
        values.put(PID, Integer.toString(mole.GetPID()));
        values.put(DLD, Boolean.toString(mole.GetDLD()));
        values.put(EVOLVING, Boolean.toString(false));

     // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }


    //adding def row <--- this function never used
    public void addDef(Configurations mole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, Integer.toString(1));

     //   values.put(AGE, Integer.toString( mole.GetAge()));
     //      values.put(SEX, mole.GetGender());
     //            values.put(HISTORY, Boolean.toString(mole.GetHistory()));
        values.put(IDM, Integer.toString(mole.GetIdM()));
        values.put(PID, Integer.toString(mole.GetPID()));
        values.put(LANG, Boolean.toString(mole.GetLanguage()));
        values.put(PIDCOUnt,Integer.toString(mole.GetPIDCOUnt()));
        // Inserting Row
        db.insert(TABLE_DEF, null, values);
        db.close(); // Closing database connection
    }

    // Getting single mole
    public Mole getMole(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { ID, NAME, ITCH, ISHARD ,HASBLOD, INPAIN,  REALWIDTH,
                        REALHEIGHT, EDGESPROBLEM, COLORPROBLEM ,EDGESSIMILATIRY ,MORFOLOGY1, MORFOLOGY2, DIAMETER, PHOTO ,ISBAD,PID,DLD,EVOLVING}, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null );
        if (cursor != null)
            cursor.moveToFirst();
        //   ID NAME AGE ITCH ISHARD HASBLOD INPAIN SEX REALWIDTH REALHEIGHT EDGESPROBLEM COLORPROBLEM EDGESSIMILATIRY MORFOLOGY1 MORFOLOGY2 DIAMETER PHOTO
        //Mole mole = new Mole(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2));
        Mole mole1 = new Mole(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
             //   Integer.parseInt(cursor.getString(2)),
                 Boolean.parseBoolean(cursor.getString(2)),
                Boolean.parseBoolean(cursor.getString(3)),

                Boolean.parseBoolean(cursor.getString(4)),
                Boolean.parseBoolean(cursor.getString(5)),
              //  cursor.getString(7),
                Double.parseDouble(cursor.getString(6)),
                Double.parseDouble(cursor.getString(7)),
                Boolean.parseBoolean(cursor.getString(8)),
                Double.parseDouble(cursor.getString(9)),
                Boolean.parseBoolean(cursor.getString(10)),
          //     Boolean.parseBoolean(cursor.getString(13)),

                cursor.getString(11),
                cursor.getString(12),
                Double.parseDouble(cursor.getString(13)),
                cursor.getString(14),
        Boolean.parseBoolean(cursor.getString(15)),
                Integer.parseInt(cursor.getString(16)),
                Boolean.parseBoolean(cursor.getString(17)),
                Boolean.parseBoolean(cursor.getString(18))

        );
        // return contact
        return mole1;
    }
// getting a single profile by using id

    // + PID + " INTEGER PRIMARY KEY,"  + AGE + " INTEGER,"+  SEX + " TEXT,"  +HISTORY + " NUMERIC," + profilname +" TEXT "
    //              + name +" TEXT "+ mail +" TEXT "+ phone +" TEXT "+ ")";
    public Profile getProfile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROFIL, new String[] { PID, AGE, SEX, HISTORY, profilname ,name, mail, phone}, PID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null );
        if (cursor != null)
            cursor.moveToFirst();

        Profile mole1 = new Profile(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                cursor.getString(2),
                Boolean.parseBoolean(cursor.getString(3)),
                 cursor.getString(4) ,

                cursor.getString(5) ,
                 cursor.getString(6) ,
                cursor.getString(7)
            );
        // return contact
        return mole1;
    }
// geting the app's configurations
    public Configurations getDEf() {
        int id=1;
        SQLiteDatabase db = this.getReadableDatabase();
    //    Cursor cursor = db.query(TABLE_DEF, new String[] { ID, AGE, SEX,HISTORY ,IDM}, ID + "=?",
                Cursor cursor = db.query(TABLE_DEF, new String[] { ID, IDM , PID,LANG ,PIDCOUnt}, ID + "=?",

                        new String[] { String.valueOf(id) }, null, null, null, null );
        if (cursor != null)
            cursor.moveToFirst();

        //   ID NAME AGE ITCH ISHARD INPAIN SEX REALWIDTH REALHEIGHT EDGESPROBLEM COLORPROBLEM EDGESSIMILATIRY MORFOLOGY1 MORFOLOGY2 DIAMETER PHOTO
        //Mole mole = new Mole(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2));
        Configurations mole1 = new Configurations(
             //   Integer.parseInt(cursor.getString(1)),
             //   cursor.getString(2),
             //   Boolean.parseBoolean(cursor.getString(3)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                Boolean.parseBoolean(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4))

        );


        // return contact
        return mole1;
    }


    // Getting All Moles -> return a list filled with moles
    public List<Mole> getAllMoles() {

             List<Mole> contactList = new ArrayList<Mole>();
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to lis
        //   ID NAME AGE ITCH ISHARD INPAIN SEX REALWIDTH REALHEIGHT EDGESPROBLEM COLORPROBLEM EDGESSIMILATIRY MORFOLOGY1 MORFOLOGY2 DIAMETER PHOTO


        if (cursor.moveToFirst()) {
                do {
                    Mole mole = new Mole();
                   mole.SetId(                    Integer.parseInt(cursor.getString(0))               );
                    mole.SetName(cursor.getString(1));
                  //  mole.SetAge(Integer.parseInt(cursor.getString(2)));
                    mole.SetHasItch(Boolean.parseBoolean(cursor.getString(2)));
                    mole.SetIsHard(Boolean.parseBoolean(cursor.getString(3)));
                    mole.SetHasBlood(Boolean.parseBoolean(cursor.getString(4)));

                    mole.SetInPain(Boolean.parseBoolean(cursor.getString(5)));
                //    mole.SetGender( cursor.getString(7));
                    mole.SetWidth(                            Double.parseDouble(cursor.getString(6)));
                    mole.SetHeight( Double.parseDouble(cursor.getString(7)));
                    mole.SetBadBorder(Boolean.parseBoolean(cursor.getString(8)));
                    mole.SetColorCurve(Double.parseDouble(cursor.getString(9)));
                    mole.SetHasAsymmetry(Boolean.parseBoolean(cursor.getString(10)));
                   // mole.SetHasAsymmetry(Boolean.parseBoolean(cursor.getString(11)));

                    mole.SetMorfologyAr( cursor.getString(11),cursor.getString(12));
                    mole.SetDiameter(Double.parseDouble(cursor.getString(13)));
                    mole.SetMole(cursor.getString(14));
                    mole.SetIsbad(Boolean.parseBoolean(cursor.getString(15)));
                    mole.SetPID(                    Integer.parseInt(cursor.getString(16))               );
                    mole.SetDLD(                    Boolean.parseBoolean(cursor.getString(17))               );
                    mole.SetEvolving(                    Boolean.parseBoolean(cursor.getString(18))               );

                    Log.d("hard",Boolean.toString(mole.GetIsbad()));
                    Log.d("is",String.valueOf(mole.GetId()));

                    // Adding contact to list
                    contactList.add(mole);
                } while (cursor.moveToNext());
            }

            // return contact list
            return contactList;

    }


///Getting All Profiles -> return a list filled with Profiles
    public List<Profile> getAllProfiles() {

        List<Profile> contactList = new ArrayList<Profile>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFIL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // + PID + " INTEGER PRIMARY KEY,"  + AGE + " INTEGER,"+  SEX + " TEXT,"  +HISTORY + " NUMERIC," + profilname +" TEXT "
        //              + name +" TEXT "+ mail +" TEXT "+ phone +" TEXT "+ ")";

        if (cursor.moveToFirst()) {
            do {
                Profile mole = new Profile();
                mole.SetIdM(                    Integer.parseInt(cursor.getString(0))               );
                mole.SetAge(Integer.parseInt(cursor.getString(1)));
                mole.SetSex(cursor.getString(2));
                mole.SetHistory(Boolean.parseBoolean(cursor.getString(3)));
                mole.SetProfilname(cursor.getString(4));
                mole.SetName(cursor.getString(5));

                mole.SetMail(cursor.getString(6));
                mole.SetPhone( cursor.getString(7));

    //            Log.d("hard",Boolean.toString(mole.GetIsbad()));
     //           Log.d("is",String.valueOf(mole.GetId()));

                // Adding contact to list
                contactList.add(mole);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;

    }





    // Getting Moles Count
    public int getMolesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int retu =cursor.getCount();
        cursor.close();

        // return count
        return retu;

    }

    // Getting Profiles Count

    public int getProfileCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROFIL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int retu =cursor.getCount();
        cursor.close();

        // return count
        return retu;

    }
    // Updating single mole
    public int updateMole(Mole mole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, Integer.toString(mole.GetId()));
        values.put(NAME, mole.GetName());
     //   values.put(AGE,Integer.toString( mole.GetAge()));
        values.put(ITCH, Boolean.toString(mole.GetHasItch()));
        values.put(ISHARD,  Boolean.toString(mole.GetIsHard()));
        values.put(HASBLOD,  Boolean.toString(mole.GetHasBlood()));

        values.put(INPAIN, Boolean.toString( mole.GetInPain()));
     //   values.put(SEX, mole.GetGender());
        values.put(REALWIDTH, Double.toString(mole.GetWidth()));
        values.put(REALHEIGHT,Double.toString( mole.Getheight()));
        values.put(EDGESPROBLEM, Boolean.toString(mole.GetBadBorder()));
        values.put(COLORPROBLEM,Double.toString( mole.GetColorCurve()));
        values.put(EDGESSIMILATIRY,Boolean.toString( mole.GetHasAsymmetry()));
        values.put(MORFOLOGY1, mole.GetMorfologyAr1());
        values.put(MORFOLOGY2, mole.GetMorfologyAr2());
        values.put(DIAMETER, Double.toString(mole.GetDiameter()));
        values.put(PHOTO, mole.GetMole());
        values.put(PID, Integer.toString(mole.GetPID()));
        values.put(EVOLVING, Boolean.toString(mole.GetEvolving()));

        Log.d("inupdatemole",Boolean.toString(mole.GetDLD()));
        values.put(DLD,Boolean.toString( mole.GetDLD()));

        //  values.put(HISTORY,  Boolean.toString(mole.GetBadHistory()));



        values.put(ISBAD,  Boolean.toString(mole.GetIsbad()));


        // updating row
        return db.update(TABLE_CONTACTS, values, ID + " = ?",
                new String[] { String.valueOf(mole.GetId()) });}

    // Updating single profile
    public int updateProfil(Profile mole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
   //     values.put(PID, Integer.toString(mole.GetIdM()));
        values.put(AGE,Integer.toString( mole.GetAge()));
        values.put(SEX, mole.GetSex());
        values.put(HISTORY,  Boolean.toString(mole.GetHistory()));
        values.put(profilname, mole.GetProfilname());



        values.put(name, mole.GetName());

        values.put(mail, mole.GetMail());
        values.put(phone, mole.GetPhone());




        // updating row
        return db.update(TABLE_PROFIL, values, PID + " = ?",
                new String[] { String.valueOf(mole.GetIdM()) });}

//updating apps Configurations
    public int updateDef(Configurations mole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ID, Integer.toString(1));

        //values.put(AGE, Integer.toString( mole.GetAge()));
       // values.put(SEX, mole.GetGender());
       // values.put(HISTORY, Boolean.toString(mole.GetHistory()));
        values.put(IDM, Integer.toString(mole.GetIdM()));
        values.put(PID, Integer.toString(mole.GetPID()));
        values.put(LANG, Boolean.toString(mole.GetLanguage()));
        values.put(PIDCOUnt, Integer.toString(mole.GetPIDCOUnt()));



        // updating row
        return db.update(TABLE_DEF, values, ID + " = ?",
                new String[] { String.valueOf(1) });}

    // Deleting single mole
    public void deleteMole(Mole mole) {


        File file = new File(mole.GetMole());
        try {
            boolean deleted = file.delete();
        }
        catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, ID + " = ?",
                new String[] { String.valueOf(mole.GetId()) });
        db.close();

    }

    // Getting All moles of selected profile. Gets as input  a Profile and returns the list of moles of the profile
    public List<Mole> getAllProfileMoles(Profile prof) {

        List<Mole> contactList = new ArrayList<Mole>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+PID +" = " + Integer.toString(prof.GetIdM());

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to lis
        //   ID NAME AGE ITCH ISHARD INPAIN SEX REALWIDTH REALHEIGHT EDGESPROBLEM COLORPROBLEM EDGESSIMILATIRY MORFOLOGY1 MORFOLOGY2 DIAMETER PHOTO


        if (cursor.moveToFirst()) {
            do {
                Mole mole = new Mole();
                mole.SetId(                    Integer.parseInt(cursor.getString(0))               );
                mole.SetName(cursor.getString(1));
                //  mole.SetAge(Integer.parseInt(cursor.getString(2)));
                mole.SetHasItch(Boolean.parseBoolean(cursor.getString(2)));
                mole.SetIsHard(Boolean.parseBoolean(cursor.getString(3)));
                mole.SetHasBlood(Boolean.parseBoolean(cursor.getString(4)));

                mole.SetInPain(Boolean.parseBoolean(cursor.getString(5)));
                //    mole.SetGender( cursor.getString(7));
                mole.SetWidth(                            Double.parseDouble(cursor.getString(6)));
                mole.SetHeight( Double.parseDouble(cursor.getString(7)));
                mole.SetBadBorder(Boolean.parseBoolean(cursor.getString(8)));
                mole.SetColorCurve(Double.parseDouble(cursor.getString(9)));
                mole.SetHasAsymmetry(Boolean.parseBoolean(cursor.getString(10)));
                // mole.SetHasAsymmetry(Boolean.parseBoolean(cursor.getString(11)));

                mole.SetMorfologyAr( cursor.getString(11),cursor.getString(12));
                mole.SetDiameter(Double.parseDouble(cursor.getString(13)));
                mole.SetMole(cursor.getString(14));
                mole.SetIsbad(Boolean.parseBoolean(cursor.getString(15)));
                mole.SetPID(                    Integer.parseInt(cursor.getString(16)));
                mole.SetDLD(                    Boolean.parseBoolean(cursor.getString(17)));
                mole.SetEvolving(                    Boolean.parseBoolean(cursor.getString(18)));

                Log.d("hard",Boolean.toString(mole.GetIsbad()));
                Log.d("is",String.valueOf(mole.GetId()));

                // Adding contact to list
                if (prof.GetIdM()==mole.GetPID())
                contactList.add(mole);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;

    }


    // Deleting All profiles moles . Gets as input a profile id and deletes its moles from the database
    public void DeleteAllProfileMoles(int id) {

       // List<Mole> contactList = new ArrayList<Mole>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to lis
        //   ID NAME AGE ITCH ISHARD INPAIN SEX REALWIDTH REALHEIGHT EDGESPROBLEM COLORPROBLEM EDGESSIMILATIRY MORFOLOGY1 MORFOLOGY2 DIAMETER PHOTO


        if (cursor.moveToFirst()) {
            do {
                Mole mole = new Mole();
                mole.SetId(                    Integer.parseInt(cursor.getString(0))               );
                mole.SetName(cursor.getString(1));
                //  mole.SetAge(Integer.parseInt(cursor.getString(2)));
                mole.SetHasItch(Boolean.parseBoolean(cursor.getString(2)));
                mole.SetIsHard(Boolean.parseBoolean(cursor.getString(3)));
                mole.SetHasBlood(Boolean.parseBoolean(cursor.getString(4)));

                mole.SetInPain(Boolean.parseBoolean(cursor.getString(5)));
                //    mole.SetGender( cursor.getString(7));
                mole.SetWidth(                            Double.parseDouble(cursor.getString(6)));
                mole.SetHeight( Double.parseDouble(cursor.getString(7)));
                mole.SetBadBorder(Boolean.parseBoolean(cursor.getString(8)));
                mole.SetColorCurve(Double.parseDouble(cursor.getString(9)));
                mole.SetHasAsymmetry(Boolean.parseBoolean(cursor.getString(10)));
                // mole.SetHasAsymmetry(Boolean.parseBoolean(cursor.getString(11)));

                mole.SetMorfologyAr( cursor.getString(11),cursor.getString(12));
                mole.SetDiameter(Double.parseDouble(cursor.getString(13)));
                mole.SetMole(cursor.getString(14));
                mole.SetIsbad(Boolean.parseBoolean(cursor.getString(15)));
                mole.SetPID(                    Integer.parseInt(cursor.getString(16)));
                mole.SetDLD(                    Boolean.parseBoolean(cursor.getString(17)));
                mole.SetEvolving(                    Boolean.parseBoolean(cursor.getString(18)));

                Log.d("hard",Boolean.toString(mole.GetIsbad()));
                Log.d("is",String.valueOf(mole.GetId()));
                if (id==mole.GetPID()) {
                    deleteMole(mole);
                    File file = new File(mole.GetMole());
                    try {
                        boolean deleted = file.delete();
                    }
                    catch (NullPointerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // Adding contact to list
        //        contactList.add(mole);
            } while (cursor.moveToNext());
        }

        // return contact list
    //    return contactList;

    }


// deleting a profile. gets as input a Profile and deletes it
    public void deleteProfile(Profile mole) {

/**

 DeleteAllProfileMoles
 **/DeleteAllProfileMoles(mole.GetIdM());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFIL, PID + " = ?",
                new String[] { String.valueOf(mole.GetIdM()) });
        db.close();

    }
}
