package com.example.skinhealthchecker;
/**
 //Οντότητα  που διαχειρίζεται τα προφίλ των χρηστών της εφαρμογής
 πχ  profilname:Το όνομα του προφιλ
 name :Το όνομα του χρήστη
 age:Η ηλικία του χρήστη
 mail: το e-mail του χρήστη
 phone: ο αριθμός τηλεφώνου του χρήστη
 sex: το φύλλο του χρήστη
 history: Το ιστορικό του χρήστη ( εχει κακό ? ναι/οχι )

 // An entity that manages app users profiles
   profilename: The profile name
   name: The user name
   Age: Age of the user
   mail: the user's e-mail
   phone: the phone number of the user
   sex: the user's gender
   history: User history (bad? yes / no)

 it Includes readings and writings function for the variables
 */

public class Profile {


    int id;

   String profilname ;
    String name   ;
    int age ;
    String mail ;
    String phone ;
    String sex;
    boolean history;


    public Profile(){}

    public Profile(   int id1,int age1, String sex1 ,boolean history1,String profilname1 , String name1 ,  String mail1 ,
            String phone1  ){
           id=id1;

          profilname= profilname1 ;
          name =name1  ;
          age =age1;
          mail =mail1;
          phone =phone1;
          sex=sex1;
          history=history1;

    }

    public int GetIdM(){


        return id;
    }
    public void idup() {
        id = id + 1;
    }


    public void SetIdM( int in){

        id=in;}

    public String GetName(){


        return name;
    }
    public void SetName( String in){

        name=in;}



    public int GetAge(){


        return age;
    }
    public void SetAge( int in){

        age=in;}




    public String GetMail(){


        return mail;
    }
    public void SetMail( String in){

        mail=in;}




    public String GetPhone(){


        return phone;
    }
    public void SetPhone( String in){

        phone=in;}



    public String GetSex(){


        return sex;
    }
    public void SetSex( String in){

        sex=in;}



    public boolean GetHistory(){


        return history;
    }
    public void SetHistory( boolean in){

        history=in;}


    public String GetProfilname(){


        return profilname;
    }
    public void SetProfilname( String in){

        profilname=in;}






}
