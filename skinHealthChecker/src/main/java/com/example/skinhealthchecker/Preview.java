package com.example.skinhealthchecker;
/*
Εμφανίζει την προεπισκόπηση της φωτογραφίας που τράβηξε ο χρήστης.
Displays the preview of the photo taken by the user.

 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Preview extends Activity{
	static InputStream imageStream; // uses to load the image from storage
	public static  Bitmap value; // is the photo
	DatabaseHandler db;
static float  fl=0;  // camera informations that will pass to next intent
static float sensorh =0;
	private static final String TAG = "Preview";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new DatabaseHandler(this); // linking the database to handler
		Configurations def = db.getDEf(); // gets the app configurations

		if(def.GetLanguage()) // check the display language status and load the right xml
		setContentView(R.layout.preview);
    	else
			setContentView(R.layout.previewen);

		Intent intent = getIntent();
		 Bundle extras = getIntent().getExtras();
		    if (extras != null) {// gets the data from old intent
		   fl = intent.getFloatExtra("focal",0); //if it's a string you stored.
	          sensorh = intent.getFloatExtra("sensor",0); //if it's a string you stored.
				Log.d("pixel size",Float.toString(sensorh));

			}
		
		final Bitmap tmp= Getbitmap(); // loads the image
		doPhotoPrint (tmp);// print it to display
	//	doPhotoPrint (Getbitmap());

	  //  tmp.recycle();
		
		
		
		Button captureButton = (Button) findViewById(R.id.trymore); // if the trymore button pressed
		captureButton.setFocusable(true);
		captureButton.setFocusableInTouchMode(true);
		captureButton.requestFocus();
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// delete the image from memory
				tmp.recycle();

				//go to first activity
				 Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
				    startActivity(intent);
				//

			}
		});
		
		Button captureButton2 = (Button) findViewById(R.id.Analyze);// if the analyze button pressed
		captureButton2.setFocusable(true);
		//captureButton2.setFocusableInTouchMode(true);
		captureButton2.requestFocus();
		captureButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				tmp.recycle();				// delete the image from memory

				Intent intent2 = new Intent(getApplicationContext(), Randar.class);				//go to next activity

				//pass some data
				intent2.putExtra("focal", fl); //Optional parameters
				 //A = Angle of view, l = focal length, h = sensor height/2 => tan(A/2) = h/l
				 intent2.putExtra("sensor", sensorh); //Optional parameters
				    startActivity(intent2);
				//

			}
		});
		
		
	}




	// returns the image from storage

	
	public   Bitmap Getbitmap() {
		Log.e(TAG, "geting bitmap");

		String NOMEDIA=" .nomedia";

		//geting the apps private  path

		File mediaStorageDir = new File(
				this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());


 		/**
		String NOMEDIA=" .nomedia";

		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+NOMEDIA + "/skin");
 **/
		// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d("MyCameraApp", "failed to create directory");
				return null;
				}
			}
	// 	File pictureFile =  new File(mediaStorageDir.getPath() + File.separator + "IMGtmp" + ".jpg");

		// link the image file
		File pictureFile =  new File(mediaStorageDir.getPath() + File.separator + "IMGtmp" + ".nodata");

		try {// loads the image from storage and creating the image
				imageStream = new FileInputStream(pictureFile);
				value = BitmapFactory.decodeStream(imageStream);
				if (value.getWidth() >= value.getHeight()){
				value= Bitmap.createBitmap(
						value, 
						value.getWidth()/2 - value.getHeight()/2,
					     0,
					     value.getHeight(), 
					     value.getHeight()
					     );

}else{

						value = Bitmap.createBitmap(
									value,
										0, 
						value.getHeight()/2 - value.getWidth()/2,
							value.getWidth(),     value.getWidth() 
     );
						
}// Bitmap lastBitmap = null;
//lastBitmap = Bitmap.createScaledBitmap(value, 2000, 2000, true);


			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return value;
	}
	/* function which one set to display the image to screen
		input image
	 */
	private void doPhotoPrint(Bitmap bMap) {
		ImageView image = (ImageView) findViewById(R.id.myimg); // links the image view
		image.setImageBitmap(bMap);
		
		
		
	}
} 
