package com.example.skinhealthchecker;

/**
//Οντότητα  που διαχειρίζεται τις απαραίτιτες πληροορίες που πρέπει να διατηρεί η εφαρμογή  κατα την λειτουργεία της
 πχ  Language:στην οντότητα αυτή υπάρχουν πληροφορίες για την γλώσσα που είναι ενεργή ,
PID :Το ενεργό  profile id
 PIDCOUnt:Τελευταίο ganarated  profile id
idc:Τελευταίο ganarated  mole id
Περιλαμβάνονται συναρτίσεις για την εγραφή και ανάγνωση των τιμών


 An object that manipulates  the necessary information that the application must have
 eg Language: this object has information about the language that is active,
 PID: Active profile id
 PIDCOUnt: Last ganarated profile's s
 ididc: next available   mole's id

 it Includes readings and writings function for the variables
 */

public class Configurations {

//ondotita gia default ruthmiseis ths efarmoghs

 //  int AGE=0;
  // String SEX="Άνδρας";
   //       boolean HISTORY =false;
   // int idc=1000;
    boolean Language;
    int PID;
    int  PIDCOUnt;
  //   int  AGE;
 //    String SEX;
 //    boolean HISTORY;
    int idc;
    public Configurations(){}

    public Configurations(  int IDm ,int PID1,boolean language1 ,int count){
    //public Configurations(int AGE1 , String Sex1 ,Boolean History1 , int IDm){
     //   AGE=AGE1;
    //    SEX=Sex1;
    //    HISTORY=History1;
        idc=IDm;PID=PID1;  Language=  language1;PIDCOUnt= count;
    }

    public boolean GetLanguage(){

        return Language;
    }

    public void SetLanguage(boolean in){

        Language=in;
    }

    public int GetPIDCOUnt(){


        return PIDCOUnt;
    }


    public void PIDCOUntup() {
        PIDCOUnt = PIDCOUnt + 1;
    }

    public void idup() {
        idc = idc + 1;
    }


    public void SetPIDCOUnt( int in){

        PIDCOUnt=in;}

    public int GetIdM(){


        return idc;
    }


    public void SetIdM( int in){

        idc=in;}
////////
public int GetPID(){


    return PID;
}
    public void PIDup() {
        PID = PID + 1;
    }


    public void SetPID( int in){

        PID=in;}



/**
    public int GetAge(){

        return AGE;
    }

    public void SetAge(int in){

          AGE=in;
    }
    public void SetGender(String in){

        SEX=in;
    }
    public String GetGender(){

        return SEX;}


    public boolean GetHistory(){

        return  HISTORY;
    }
    public void SetHistory(boolean in){

           HISTORY=in;
    }
*/}
