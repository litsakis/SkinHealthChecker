package com.example.skinhealthchecker;
/*
 Είναι μια κριμένη λειτουργία για τον χρήστη .Γίνεται εμφάνιση κάποιων στοιχείων μόνο για τον προγραμματιστή .

 It is a featured function for the user. It is used only from the  developer.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class Results extends Activity{
	
	
	
	private static String TAG = null;
static Bitmap next; //first half of the mole
static Bitmap next2;// second half of the mole
static Bitmap hist; // hsv histogram
static Bitmap histrgb; // rgb histogram
static Bitmap edged; // edged mole
static InputStream imageStream; // temp value used to read from storage
public static  Bitmap value; // temp bitmap


	DatabaseHandler db;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		TAG="RESULTS";
	    Log.i(TAG, "onCreate");

	    super.onCreate(savedInstanceState);
		// reading from storange
    	 next= Getbitmap(0); //if it's a string you stored.
    	 next2=Getbitmap(1);
    	 hist=Getbitmap(2);
    	 histrgb=Getbitmap(3);
    	 edged=Getbitmap(4);
	    Bundle extras = getIntent().getExtras();

		// get data from prev intent
	    double realwidth = 0;
	    double realheight = 0;
	    if (extras != null) {
	    	Intent intent = getIntent();
	     realwidth=	 intent.getDoubleExtra("realwidth",0)/10;
	     realheight=	 intent.getDoubleExtra("realheight",0)/10;

	        
	    }

	    // printing data to screen
	setContentView(R.layout.result);
	TextView myAwesomeTextView = (TextView)findViewById(R.id.measures);
	myAwesomeTextView.setText("Approximate width : "+ Double.toString(realwidth));

	TextView myAwesomeTextView2 = (TextView)findViewById(R.id.measures2);
	myAwesomeTextView2.setText("Approximate height : "+ Double.toString(realheight));
	
	Button captureButton = (Button) findViewById(R.id.trymore);
	captureButton.setFocusable(true);
	//captureButton.setFocusableInTouchMode(true);
	captureButton.requestFocus();
	captureButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//
// if the button is pressed go to first screen
			 gotostart();
			
			//

		}});
	
	edgecut1(next);// prints data to screen
	edgecut2(next2);
	histhsv(hist);
	histrgb(histrgb);
	edgefull(edged);
	
	
	
	}
	
	public  void edgefull(Bitmap bMap) {
		ImageView image = (ImageView) findViewById(R.id.edgefull);
		image.setImageBitmap(bMap);
	}
public  void edgecut1(Bitmap bMap) {
	ImageView image = (ImageView) findViewById(R.id.edgeimg);
	image.setImageBitmap(bMap);
}
public  void edgecut2(Bitmap bMap) {
	ImageView image = (ImageView) findViewById(R.id.edgeimg2);
	image.setImageBitmap(bMap);
}
public  void histhsv(Bitmap bMap ) {
	ImageView image = (ImageView) findViewById(R.id.hist);
	image.setImageBitmap(bMap);
}
public  void histrgb(Bitmap bMap ) {
	ImageView image = (ImageView) findViewById(R.id.histrgb);
	image.setImageBitmap(bMap);
}
//pigenei sthn prwto activity
public    void gotostart()
{
	
    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
    
    // going to first activity
    next.recycle(); //THOW THE TRASH 
	 next2.recycle();
	 hist.recycle();
	 histrgb.recycle();
	 edged.recycle();
    startActivity(intent);

	
}

// function that reads images from storange
	// input the number of image
	// ouputs the image
public   Bitmap Getbitmap(int h) {
	Log.e(TAG, "geting bitmap");

	String NOMEDIA=" .nomedia";

//	File mediaStorageDir = new File(
//				Environment
//						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES +NOMEDIA ),"MyCameraApp");

	File mediaStorageDir = new File(
			this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());

	// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
			return null;
			}
		}
		//generates url and links the file
 	File pictureFile =  new File(mediaStorageDir.getPath() + File.separator + "IMGtmp2" +Integer.toString(h)+ ".nodata");
	 try {// anoigma photos apo thn mnhmh
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



}
