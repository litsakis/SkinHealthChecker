package com.example.skinhealthchecker;
/*
Είναι το κεντρικό  activity .Η περιέχει τον απαραίτητο κώδικα για τον χειρισμό της cameras .

•Στον παρακάτω κώδικα γίνεται:

   - ορισμός αυτόματης εστίασης
   -ορισμός ισορροπία λευκού στο ψυχρό αν υποστηρίζεται
   - υπολογίζεται η Εστιακή απόσταση του φακού
   - λαμβάνεται το φυσικό μεγεθος του εσθητήρα
	- υπολογίζονται τα υποστηριζόμενα μεγέθη προεπισκόπηση εικόνας της συσκευής.
•	-Τα υποστηριζόμενα μεγέθη λήψης  εικόνας της συσκευής.
   -Και στις 2 περιπτώσεις η εφαρμογή πέρνει το μεγαλύτερο υ-ποστηριζόμενο βρίσκοντάς το πρώτα υλοποιώντας γνωστό αλγόριθ-μο εύρεσης μεγαλύτερου από λίστα και μετά αυτό το ορίζεται στις ρυθμίσεις Ορίζεται  ότι η περιοχή εστίασης θα είναι στο κέντρο της εικόνας.
•	-Γίνεται έλεγχος αν υπάρχει φλάς στην συσκευή. Αν υπάρχει το ε-νεργοποιεί .
•	 -γίνεται αναζήτηση στα specs αν υπάρχει κάποια λειτουρ-γία κατά της ακούσιας κίνησης .
	-Γίνεται αναζήτηση στις ρυθμί-σεις που έχει δώσει ο κατασκευαστής της συσκευής αν υπάρχουν οι λέξεις ios η image-stabilizer αν βρεθούν αυτές οι λέξεις ενερ-γοποιεί την αντίστοιχη λειτουργεία .
	Γίνεται η δημιουργία της προεπισκόπησης και ορίζεται το σημείο του xml το οποίο θα προβάλλεται .
 	Ορίζεται ότι όταν εντοπιστεί αφή τότε θα γίνεται εστίαση
 	Γίνεται η λήψη εικόνας όταν γίνει εστίαση.

•	υπολογίζεται η θέση που γίνεται αποθήκευση των δεδομένων της εφαρμογής δίνε-ται από το λειτουργικό .
 Η θέση είναι μο-ναδική για κάθε εφαρμογή και καμία άλλη εφαρμογή δεν έχει πρό-σβαση στην συγκεκριμένη θέση .
  Το αρχείο που θα αποθηκευτεί θα έχει όνομα IMGtmp και κατάληξη .nodata για λόγους ασφαλείας.

It is the central activity. It contains the necessary code for handling the cameras.

• The code below is:

   - definition of autofocus
   -determination of white balance in the cold if supported
   - The focal length of the lens is calculated
   - obtain the natural size of the sensor
- the supported image previews of the device are calculated.
• -The supported image capture sizes of the device.
   -And in two cases, the application takes the largest support by first installing a known larger-than-list finder algorithm and then defining it in the settings. Specifies that the focus area will be in the center of the image.
• Check if there is a flash on the device. If it exists, it turns on.
• -can search for specs if there is any function against inadvertent movement.
-Search the settings provided by the manufacturer of the device if there are the words ios the image-stabilizer if these words are found they activate the corresponding function.
The preview is created and the xml point to be displayed.
 It is defined that when a touch is detected then focus will be on
 An image is taken when focusing.

• Calculate the location where the application data is stored is given by the operating data.
 The position is modular for each application and no other application has access to that location.
  The file to be saved will be named IMGtmp and a .nodata extension for security reasons.

 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.camera2.params.MeteringRectangle;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.SizeF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

@SuppressLint("NewApi")
public class CameraActivity extends Activity {
	private static final String TAG = "CameraActivity";
	public static int temp = 0; // temporary variable

	private static 	ToggleButton lang;  //the button to change the language
	private Camera mCamera;  // the variable referenced in the snapshot of the camera
	private CameraPreview mPreview; // the variable mentioned in the camera preview
	private Exception e;
  	static File pictureFile;// the variable that refers to the image file that will be created on disk
	static int press=0; //the variable that ensures that the photo button was pressed once.
	 static float fl;//the focal length of the camera
	// static double horizontalViewAngle;
	// static double verticalViewAngle ;
	static float sensorh; // the height of the sensor
	static	int biggerid =0; // variable that will display the id of the largest camera on the device



	//private boolean safeToTakePicture = false;

	private Configurations def;  // snapshot of the structure with the last settings used

	DatabaseHandler db; // the database handler
	private boolean lan = true; // lan value contains the display language of the application
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {// starting the activity
		super.onCreate(savedInstanceState);

		db = new DatabaseHandler(this);//
		def = db.getDEf(); //the last settings that were in place before the application was closed


		if(def.GetLanguage()) // the language is checked and the correct xml file is displayed

			setContentView(R.layout.activity_main);
		else
			setContentView(R.layout.activity_mainen);
// check if the device has a camera
		PackageManager pm = this.getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
 			Log.e("err", "Device has no camera!");
			return;
		}

		press=0;
		//create a snapshot of the camera




		SizeF size = new SizeF(0,0); // initialize Size

		double sizetemp =0;



// is monitored if there are cameras on the device.
//
// If there is more than one then the one with the largest sensor size is selected
		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			String[] cameraIds = manager.getCameraIdList();

			CameraCharacteristics character = manager.getCameraCharacteristics(cameraIds[0]);
			double biggersize  = character.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE).getHeight()*
					character.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE).getWidth();
// size = physical length X width
		  biggerid =0;
			Log.d("bigger id",Double.toString(sizetemp));
			Log.d("bigger id",Integer.toString(cameraIds.length));
			for (int i=1;i<cameraIds.length;i++){
				character = manager.getCameraCharacteristics(cameraIds[i]);
				sizetemp = character.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE).getHeight()*
						character.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE).getWidth();
				Log.d("bigger id",Double.toString(sizetemp));


				if ((sizetemp>biggersize) && 			character.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
					biggersize = sizetemp;
					biggerid=i;
				}

			}
			Log.d ("camera",Integer.toString(biggerid));
			character = manager.getCameraCharacteristics(cameraIds[biggerid]); // the camera features are taken
			size = character.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);



// The pixels that fit the sensor's height are taken
			int psize=	character.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE).getHeight();


			if (psize!=0)// Physical height / pixel height at height = physical height of each pixel
					sensorh= size.getHeight()/psize;
		}
		catch (CameraAccessException |NullPointerException e)
		{
			Log.e("YourLogString", e.getMessage(), e);
		}

		mCamera = getCameraInstance.getCameraInstance(biggerid); // takes the snapshot of the selected camera
	//	mCamera = getCameraInstance.getCameraInstance(0); // takes the snapshot of the selected camera

// adjusts the preview for auto-focus mode
		final Camera.Parameters params = mCamera.getParameters(); // camera parameters are taken
		setCameraDisplayOrientation(this,biggerid,mCamera);// o

		// *EDIT*//params.setFocusMode("continuous-picture");
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // definition of autofocus

		String TAG ="FocalLength";


	//	Log.i(TAG, "White Balance setting = " + params.get("whitebalance"));
       params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT); // set color temperature cold

        if(  params.isAutoWhiteBalanceLockSupported())
        {
           params.setAutoWhiteBalanceLock(true); // lock color change change



        }
		try {
			 
			 fl=params.getFocalLength(); // capturing the focal distance of the sensor
			// 		  Log.d(TAG,Float.toString(fl) );
// second way to calculate the camera's size
// 			  TAG ="horizontalViewAngle";
//		  horizontalViewAngle = params.getHorizontalViewAngle();
//			  Log.d(TAG,Double.toString(horizontalViewAngle) );
//				  TAG ="verticalViewAngle";
//			  verticalViewAngle = params.getVerticalViewAngle();
//			  Log.d(TAG,Double.toString(verticalViewAngle) );
//    sensorh= Math.tan(horizontalViewAngle/2*1000)*2*fl;



		    }
		    catch (NullPointerException e)
		    {
		        Log.e("YourLogString", e.getMessage(), e);
		    }
		
		

		List<Camera.Size> sizeList = params.getSupportedPreviewSizes();// list of supported viewing resolutions
		List<Camera.Size> sizeList2 = params.getSupportedPictureSizes();// list of supported image storage resolutions

		
		Size bestcameraSize = sizeList2.get(0);

// From the above lists we choose the largest resolution
		for(int i = 1; i < sizeList2.size(); i++){
		    if((sizeList2.get(i).width * sizeList2.get(i).height) > (bestcameraSize.width * bestcameraSize.height)){
		    	bestcameraSize = sizeList2.get(i);
		    }
		}

		Size bestSize = sizeList.get(0);
		for(int i = 1; i < sizeList.size(); i++){
		    if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
		        bestSize = sizeList.get(i);
		    }
		}
		params.setPictureSize(bestcameraSize.width, bestcameraSize.height);
		TAG = "best preview size";
		  Log.d(TAG,Double.toString(bestSize.width)+"x"+Double.toString(bestSize.height) );
		//	 Toast.makeText(getApplicationContext(), Double.toString(bestcameraSize.width)+"x"+Double.toString(bestcameraSize.height), Toast.LENGTH_LONG).show();

		// specifies that the focus area is on the center-facing object
 		Rect newRect = new Rect(-100, -100, 100, 100);

		Camera.Area focusArea = new Camera.Area(newRect, 1000);

		List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
		focusAreas.add(focusArea);
 		params.setFocusAreas(focusAreas);
		// Check if the device has a flashlight and if it is turned on


		// Camera.open();
		List<String> flashModes = params.getSupportedFlashModes();
		if (flashModes == null) {
		//	Toast.makeText(getApplicationContext(), "LED Not Available", Toast.LENGTH_LONG).show();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);


		} else {
		//
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);



		}

		// definition of luminance control in the center (if the center point is brighter or darker
// the image is adjusted
		if (params.getMaxNumMeteringAreas() > 0){
			List<Camera.Area> areas = new ArrayList<Camera.Area>();
			areas.add(new Camera.Area(newRect, 1000));
			params.setMeteringAreas(areas);

		}

		mCamera.setParameters(params);
		int ios=0;
		String iosparameter ;




		// Checks if there is support to improve the image in case of unintentional traffic
// search words in camera features for ios image-stabilizer
	 String goodysios = mCamera.getParameters().flatten();
	 List<String> goodys = mCamera.getParameters().getSupportedSceneModes();
	   StringTokenizer tokenizer = new StringTokenizer(goodysios, ";");
	
	 
	 while(tokenizer.hasMoreTokens()) {
		 iosparameter = tokenizer.nextToken();

		 if (iosparameter.toLowerCase().contains("ios") || iosparameter.toLowerCase().contains("image-stabilizer")) {
			 ios = 1;

			 params.set(iosparameter, "ios");
			 params.set("image-stabilizer", "ois");


		 }

	 }


		// previews the image on the mobile screen
		mPreview = new CameraPreview(this, mCamera);
		final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		//  the viewfinder for the mole inserted
		mybox box = new mybox(this);

		// το κουμπί γλώσσας πέρνει την τιμή της ενεργής γλώσσας
		  lang = (ToggleButton) findViewById(R.id.language);

		lang.setChecked(def.GetLanguage());
		lang.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{//definition that the button presses will be taken into account
			lan=lang.isChecked();
			//	if the language button is pressed, the new language is set and intent is restarted
						def.SetLanguage(lan);
				db.updateDef(def);
				finish();
				startActivity(getIntent());

			//	Toast.makeText(getApplicationContext(), "Click!", Toast.LENGTH_SHORT).show();
			}
		});
		//View myView = findViewById(R.id.camera_preview); //refocus on touch 
		preview.setOnTouchListener(new OnTouchListener() {
		      public boolean onTouch(View v, MotionEvent event) {
		          // ... Respond to touch events   
// If a part of the screen is pressed, focus is refocused
		    	   Log.d("down", "focusing now");

		           mCamera.autoFocus(null);
		           
		           return true;
		      }
		  });
// the snapshot of the target is entered on the screen

		addContentView(box, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setFocusable(true);
		//captureButton.setFocusableInTouchMode(true);
		captureButton.requestFocus();
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
// If the button with the lens is pressed, the following function is executed
				focusandspray();
// the function starts the image acquisition process.
				//

			}
		});

		Button captureButton32 = (Button) findViewById(R.id.info);
		captureButton32.setFocusable(true);
		//captureButton.setFocusableInTouchMode(true);
		captureButton32.requestFocus();
		captureButton32.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
// if the button for the manual is pressed the corresponding intent is started

				Intent intent23 = new Intent(getApplicationContext(), Manual.class);
				startActivity(intent23);
				//

			}
		});

		Button captureButtonU = (Button) findViewById(R.id.profil);
		captureButtonU.setFocusable(true);
		//captureButton.setFocusableInTouchMode(true);
		captureButtonU.requestFocus();
		captureButtonU.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
// if the button for the profile manager is pressed, the corresponding intent is started

				Intent intentU = new Intent(getApplicationContext(), Users.class);
				startActivity(intentU);
				//

			}
		});

		if (flashModes != null) { // if there is a flash on the device then the buttons will be taken into account
			//

			ToggleButton captureButton2 = (ToggleButton ) findViewById(R.id.flash);
			captureButton2.setFocusable(true);
			//captureButton2.setFocusableInTouchMode(true);
			captureButton2.requestFocus();
			captureButton2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//if pressed on it will be done off otherwise the opposite
					if (params.getFlashMode().equals(Parameters.FLASH_MODE_TORCH))

					{
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
						mCamera.setParameters(params);
					}
					else
					{
						params.setFlashMode(Parameters.FLASH_MODE_TORCH);
						mCamera.setParameters(params);

					}

					//

				}
			});

			Button captureButton3 = (Button) findViewById(R.id.data);//if the button for the gallery is pressed
// the corresponding intent is started
			captureButton3.setFocusable(true);
			//     captureButton3.setFocusableInTouchMode(true);
			captureButton3.requestFocus();
			captureButton3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent intent2 = new Intent(getApplicationContext(), AppGallery.class);
					startActivity(intent2);

				}
			});

		}


	}
// when the picture is captured the data is written to the storage
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
CameraPreview.safeToTakePicture = true;    //disable the lock of the camera( camera will capture only one frame)

			 //Creating a file at MEDIA_TYPE_IMAGE directory
			 	 	 pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);//

			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: "
								+ e.getMessage());
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
		//		data.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.write(data);

		//		fos.flush();
				fos.close();

			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			
		}
	};

	
	@Override
	protected void onPause() {


		releaseCamera(); // release the camera immediately on pause event
		finish();
		super.onPause();
		/**	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	public static final int MEDIA_TYPE_IMAGE = 1;

	/** Create a file Uri for saving an image or video */
	private   Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private   File getOutputMediaFile(int type) {

		String NOMEDIA=" .nomedia";
// the saving directory will be the app private directory

		File mediaStorageDir = new File(
				this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());


		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
		// Date());
		File mediaFile;
// the image file name will be : IMGtmp.nodata
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMGtmp" + ".nodata");

		}

/**
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMGtmp" + ".jpg");

		}
		**/
		else {
			return null;
		}

		return mediaFile;
	}

	
	@SuppressWarnings("deprecation")
	void focusandspray() {
		if(press==0){//it is controlled if the button is pressed once, the first time it is pressed will focus and take the picture
			press=1;

			 
 			mCamera.autoFocus(new Camera.AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {

					Log.d(TAG, "onAutoFocus() " + success);
// check if taking picture lock is active . if it is not then enable the lock and capture picture
					if (CameraPreview.safeToTakePicture)
					mCamera.takePicture(null, null, mPicture);
					CameraPreview.safeToTakePicture = false;// lock the safe

					SystemClock.sleep(800);// the app is waiting 1,5 sec to end the saving procedure
					 Intent intent = new Intent(getApplicationContext(), Preview.class); // jumping to next intent
					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					//this flag will cause any existing task that would be associated with the activity to be cleared before the activity is started.
			//		 Intent.FLAG_ACTIVITY_NEW_TASK
					// sending the parameters to the next activity
					 intent.putExtra("focal", fl); //Optional parameters
					 intent.putExtra("sensor", sensorh); //Optional parameters
					    startActivity(intent); // starting next activity

 
				}

			});

	 
			
		}
		   
		    


		 
	}
/**
	protected void setDisplayOrientation(Camera camera, int angle){
		Method downPolymorphic;
		try
		{
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		}
		catch (Exception e1)
		{
		}
	}
**/
//defining-correcting the appropriate rotation of the preview

public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int result;
		//int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		// do something for phones running an SDK before lollipop
		//if (!(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT))
		/**		(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else **/
			 // back-facing
			result = (info.orientation - degrees + 360) % 360;  // adding the right angle

		camera.setDisplayOrientation(result);
	}


	public void closeCamera() {  //the app set the camera  free
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.lock();
			mCamera.release();
			mCamera=null;

		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}



	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {  // on vol+ or vol- capture image
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (action == KeyEvent.ACTION_DOWN) {
					//TODO

					// calling the image capture function
					focusandspray();

				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (action == KeyEvent.ACTION_DOWN) {
					//TODO
					// calling the image capture function

					focusandspray();
				}
				return true;
			default:
				return super.dispatchKeyEvent(event);
		}
	}


	@Override
	public void onBackPressed() {// if the android back button is pressed , the app closing
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.lock();
			mCamera.release();
			mCamera=null;

		}
		finish();

	}

}
