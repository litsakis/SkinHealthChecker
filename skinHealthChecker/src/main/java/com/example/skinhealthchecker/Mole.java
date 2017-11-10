package com.example.skinhealthchecker;



/**
 //Οντότητα  με πληροφορίες για κάθε ελιά
Οι πληροφορίες περιλαμβάνουν :


 double Diameter;  // η διάμετρος mole
 double width;// υψος mole
 double height;// πλάτος mole
 double [][] morfology;//πινακας μορφολόγίας
 double [] morfology1;// αριστερή μορφολογία mole
 double [] morfology2;// δεξιά μορφολογία mole
 boolean Isbad =false;//αν δεν φαίνεται υγιής
 String mole;  // η θέση της φωτογραφίας στο storage
 double colorcurves; //πόσες καμπύλες έχει το ιστόγραμμα
 String Name;// το όνομα του mole
 int PID;// σε πιο id προφίλ ανοίκει το mole

 int id;// το μοναδικό id του mole
 boolean BadBorder;//αν έχει κακή περίμετρο
 boolean HasAsymmetry;//αν έχει ασσυμετρία
 boolean Evolving; // αν εξελίσεται

 boolean DLD;// αν έχει ανέβει στον server



 Περιλαμβάνονται συναρτίσεις για την εγραφή και ανάγνωση των τιμών

 double Diameter; // the moles diameter
   double width; // moles height
   double height; // width of mole
   double [] [] morphology; //moles morphology table
   double [] morphology1; // left moles morphology
   double [] morphology2; // right moles morphology
   boolean Isbad = false; // if the mole does not look healthy
   String mole; // the moles photo location
   double colorcurves; // how many curves the histogram of mole has
   String Name; // the name of the mole
   int PID; // the id of profile the mole is set

   int id; // the unique id of the mole
   boolean BadBorder; // if it has a bad perimeter
   boolean HasAsymmetry; // if it has asymmetry
   boolean Evolving; // if it evolves

   boolean DLD; // if it's upgraded to the server

 An object that manipulates  the necessary information that the application must have
 eg Language: this object has information about the language that is active,
 PID: Active profile id
 PIDCOUnt: Last ganarated profile's s
 ididc: Last ganarated mole's id

 it Includes readings and writings function for the variables
 */
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.*;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Mole {

    double Diameter;
    double width;
    double height;
    double [][] morfology;
    double [] morfology1;
    double [] morfology2;
    boolean Isbad =false;
    String mole;
    double colorcurves;
     String Name;
    int PID;

int id;
    boolean BadBorder;
    boolean HasAsymmetry;
    boolean Evolving;

boolean DLD;

  //  boolean IsLiquid = false;
    boolean HasBlood=false;
    boolean HasItch=false;
    boolean IsHard=false;
    boolean BadHistory=false;
    String Gender; //0 man 1 woman
    boolean InPain= false;
    int Age;


    //   ID NAME AGE ITCH ISHARD INPAIN SEX REALWIDTH REALHIGTH EDGESPROBLEM COLORPROBLEM EDGESSIMILATIRY MORFOLOGY1 MORFOLOGY2 DIAMETER PHOTO
   // public   Mole (int id1, String Name1,int Age1,boolean HasItch1,boolean InPain1,boolean ishard1,boolean HasBlood1, String Gender1,
  //                 double width1,double hight1,  boolean BadBorder1,double colorcurves1,  boolean HasAsymmetry1,boolean BadHistory1,
  //                 String morfology11,String morfology21 , double Diameter1, String mole1,Boolean isbad1){


    public   Mole (int id1, String Name1,boolean HasItch1,boolean ishard1,boolean HasBlood1,boolean InPain1,
                      double width1,double height1,  boolean BadBorder1,double colorcurves1,  boolean HasAsymmetry1,
                      String morfology11,String morfology21 , double Diameter1, String mole1,Boolean isbad1,int PID1 ,boolean DLD1,boolean Evolving1){


        SetMorfologyAr(morfology11,morfology21);
        id= id1; Name= Name1;
       // Age= Age1;
        HasItch= HasItch1;
        InPain= InPain1;
        //Gender= Gender1;
        HasBlood =HasBlood1;
        width= width1;height = height1;
        BadBorder= BadBorder1;
        colorcurves = colorcurves1;
        HasAsymmetry= HasAsymmetry1;
        Diameter= Diameter1; mole= mole1;
       // BadHistory=BadHistory1;
        IsHard=ishard1;
        Isbad=isbad1;
        PID=PID1;
        DLD=DLD1;
        Evolving=Evolving1;
    }
    public   Mole (){}

/**
    public boolean GetIsLiquid ( ){
        return IsLiquid;

    } public void SetIsLiquid (boolean in){
        IsLiquid=in;

    } **/

public void SetDLD (boolean in){
    DLD=in;

}
    public boolean GetDLD ( ){
        return DLD;

    }
public void SetPID (int in){
    PID=in;

}
    public int GetPID ( ){
        return PID;

    }
public void SetIsbad (boolean in){
    Isbad=in;

}
    public boolean GetIsbad ( ){
        return Isbad;

    }



    public void SetHasBlood (boolean in){
        HasBlood=in;

    }
    public boolean GetInPain ( ){
        return InPain;

    } public void SetInPain (boolean in){
        InPain=in;

    }

    public boolean GetHasBlood ( ){
        return HasBlood;

    } public void SetHasItch (boolean in){
        HasItch=in;

    }

    public boolean GetHasItch ( ){
        return HasItch;

    } public void SetIsHard (boolean in){
        IsHard=in;

    }

    public boolean GetIsHard ( ){
        return IsHard;

    } public void SetBadHistory (boolean in){
        BadHistory=in;

    }

    public boolean GetBadHistory (Mole mol, DatabaseHandler db ){

        Profile test =db.getProfile(mol.GetPID());
        return test.GetHistory();

    } public void SetGender (String in){
        Gender=in;

    }

    public String GetGender ( ){
        return Gender;

    } public void SetAge (int in){
        Age=in;

    }

    public int GetAge ( ){
        return Age;

    }

    public void SetEvolving (boolean in){
        Evolving=in;

    }

    public boolean GetEvolving ( ){
        return Evolving;

    }


    public void SetHasAsymmetry (boolean in){
        HasAsymmetry=in;

    }

    public boolean GetHasAsymmetry ( ){
        return HasAsymmetry;

    }



    public void SetBadBorder (boolean in){
        BadBorder=in;

    }

    public boolean GetBadBorder (  ){
        return BadBorder;

    }

  public void SetDiameter(double in){
      Diameter=in;

  }





    public void SetMorfology(double in [][]){


        morfology=in;

    }

    public void SetMole (String in){

        mole=in;
    }

    public void SetColorCurve (double in)
    {

        colorcurves=in;

    }

    public void SetId(int in){

        id=in;
    }

    public void SetName (String in){

        Name=in;
    }


    public double GetDiameter(){
        return Diameter;

    }
    public void SetWidth( double in){
        width= in;

    }

    public void SetHeight(double in){
        height=in;
    }

    public double GetWidth(){
        return width;

    }

    public double Getheight(){
        return height;

    }
    public void SetMorfologyAr(String in,String in1){

        String[] lines = in.split(System.getProperty("line.separator"));
        String[] lines1 = in1.split(System.getProperty("line.separator"));

        morfology=  new double[lines.length][2];

        for(int i=0; i<lines.length; i++) {
            morfology[i][0]= Double.parseDouble(lines[i]);
            morfology[i][1]= Double.parseDouble(lines1[i]);

        }



    }


        public String GetMorfologyAr1(){



        StringBuilder sb = new StringBuilder();
        for (int i=0;i<morfology.length;i++)
        {
            String s=Double.toString(morfology[i][0]);
            sb.append(s);
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();

    }
    public String GetMorfologyAr2(){

        StringBuilder sb = new StringBuilder();
        for (int i=0;i<morfology.length;i++)
        {
            String s=Double.toString(morfology[i][1]);
            sb.append(s);
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();

    }



    public double [][] GetMorfology(){


        return morfology;

    }

    public String GetMole (){

       return mole;
    }

    public double GetColorCurve ()
    {

        return colorcurves;

    }

    public int GetId(){

        return id;
    }

    public String GetName (){

        return Name;
    }







}
