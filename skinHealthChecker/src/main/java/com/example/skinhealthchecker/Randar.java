package com.example.skinhealthchecker;
/*
 Κάνει επεξεργασία στην φωτογραφία ,αφαιρεί την άχρηστη πληροφορία και αποφασίζει
 πιο είναι το κομμάτι της πληροφορίας είναι χρήσιμο. Αφού εντοπίσει την ελιά εξάγει τα χαρακτηριστικά της .

 processes the photo, removes the useless information and decides
  more is the piece of information is useful. Once he locates the mole and exports its characteristics.

 •	Περικοπή εικόνας :
 crops the images
Rect roi = new Rect(Math.abs(x/2)-Randar.base,Math.abs( y/2)-Randar.base, 2*Randar.base, 2*Randar.base);
Mat cropped = new Mat(src, roi);


•	Αφαίρεση χρώματος εκτός ορίων
"Removes" the pixels that has out of limit color

Scalar lower = new Scalar(0, 0, 0, 0);
Scalar upper = new Scalar(180, 255, (int) values[2] * 1.75, 0);
   if (Math.abs(values[2] - 95) > 8)
      upper = new Scalar(170, 250, 94 * 1.75, 0);
Core.inRange(srchsv, lower, upper, srchsv);

•	Φίλτρα median και GaussianBlur
apply filters (median & gaussianblur)

Imgproc.medianBlur(srchsv, srchsv, 11);
Imgproc.GaussianBlur(srchsv, srchsv, new Size(3, 3), 0);

•	Διαδικασίες Canny edge , erosion και dilation
apply the Canny edge , erosion , dilation algorithms

Imgproc.Canny(srchsv, srchsv, 50, 100);
int erosion_size = 3;
int dilation_size = 3;
Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1, 2 * erosion_size + 1));
Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
Imgproc.dilate(srchsv, srchsv, element);
Imgproc.erode(srchsv, srchsv, element1);

Ο μέρος του κώδικα που υλοποιεί την ανίχνευση είναι ο παρακάτω :
The code that accomplishes the mole detections :

Imgproc.findContours(srchsv, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
int inContour[] = MyLib.insizecontor(contours);
if (inContour[0] != -1){
Mat[] intrestingresults = MyLib.firstmatresults(contours, inContour, srchsv2);
   int[] blackslikecon = MyLib.blacklikeresults(intrestingresults, inContour); try {
      if (blackslikecon[0] >= 0){         {
         Log.d(TAG, Integer.toString(blackslikecon.length));
         temp = MyLib.bestbigcontor(contours, srchsv, blackslikecon);
         int range = matrange(contours, temp);
         Rect rect = Imgproc.boundingRect(contours.get(temp));}}}

 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
 import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.MatOfInt4;
  import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.features2d.*;




 

 
/**
 import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_TREE;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
**/
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;



import static java.lang.Math.PI;


public class Randar extends Activity {
	static int MINBLACK = 30; // minimum pixels that obgect must have to be a mole
	static double resultx = 0;//the center coordinates of the mole
	static double resulty = 0;

	static double minsearchsize = 30; //size of the smallest object that can be found
	static double maxsearchsize = 10000;//size of the biggest object that can be found
	static String TAG = "loading";
 	static double[] minskin; // the minimum lightness color  skin has
	static double[] maxskin;// the maximum lightness color  skin has
	public static Bitmap value; // the photo will load here

	static InputStream imageStream;// stream used to read the photo from storange

	static int base = 0;//  used to crop the image
	static float fl;// focal length
	static float sensorh;// the height of the sensor
	static int[] blackpix; //array that contains the number of black pixels of objects
	static Bitmap next; // the edged half of mole
	static Bitmap last;//image containing the  circular mole
	static Bitmap next2;// the edged half of mole v2
	static Bitmap bmp32;// loaded image untouched
	static File pictureFile;// in this file used for load and save
	static boolean edgesproblem; // if the mole has bad edges
	static double[][] morfology; // stores the morphology of the mole
	static double colorproblem = 0; // how many curves had the histogramm of the mole
 static int maxsize=0 ;// contains the lines or the rows of rotated mole (what ever is bigger)
    DatabaseHandler db;// db handler
	static boolean edgessimilarityproblem; // if the mole s edge has asymmetry
	static double realwidth = 0;// the calculated physical width of mole
	static double realheight = 0;// the calculated physical height of mole
	public static final String MY_PREFS_NAME = "LitsakisCode";


	static int temp;
	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) { //loads opencv manager
		@Override
		public void onManagerConnected(int status) {
			switch (status) { // if the library loaded successfully
				case LoaderCallbackInterface.SUCCESS: {
					Log.i(TAG, "OpenCV loaded successfully");

					List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); // will contain  points  lists of objects


					Mat hierarchy = new Mat();// hierarchy of obgects

					bmp32 = Getbitmap().copy(Bitmap.Config.ARGB_8888, true); // reads bitmap

// makes copies of image
					Mat srcc = new Mat(bmp32.getHeight(), bmp32.getWidth(), CvType.CV_8UC3);
					Mat dess = new Mat(bmp32.getHeight(), bmp32.getWidth(), CvType.CV_8UC3);
					Mat srchsvv = new Mat(bmp32.getHeight(), bmp32.getWidth(), CvType.CV_8UC3);
					Mat srchsvv3 = new Mat(bmp32.getHeight(), bmp32.getWidth(), CvType.CV_8UC3);

					maxsearchsize = bmp32.getHeight() / 5 * bmp32.getHeight() / 5;// calculates new min-max sizes

					minsearchsize = bmp32.getHeight() / 200 * bmp32.getHeight() / 200;


					Utils.bitmapToMat(bmp32, srcc);// passing images in mat vars
					Utils.bitmapToMat(bmp32, dess);
					Utils.bitmapToMat(bmp32, srchsvv);
					Utils.bitmapToMat(bmp32, srchsvv3);

					bmp32.recycle(); // deletes the bitmap from memmory

					//gets the center of image
					Mat srchsv2 = MyLib.getcenterareamat(srchsvv);// i use multiple copies of mat because i process the image
					//a lot of times for different reasons . some times it is  needed a fresh image to do the job
					Mat srchsv3 = MyLib.getcenterareamat(srchsvv);
					Mat srchsv=MyLib.getcenterareamat(srchsvv);
					Mat des = MyLib.getcenterareamat(dess);
					Mat untouched = MyLib.getcenterareamat(srchsvv3);
					Mat src = MyLib.getcenterareamat(srcc);

					Imgproc.cvtColor(des, srchsv, Imgproc.COLOR_RGB2HSV);// change color space to hsv
					Imgproc.cvtColor(des, srchsv2, Imgproc.COLOR_RGB2HSV);

					Imgproc.cvtColor(des, srchsv3, Imgproc.COLOR_RGB2HSV);

					double[] values = MyLib.rightedge(srchsv);// calculates the color of the skin near the mole

					TAG = "colors min near subgect";
					Log.d(TAG, Double.toString(minskin[0]));
					Log.d(TAG, Double.toString(minskin[1]));
					Log.d(TAG, Double.toString(minskin[2]));

					TAG = "colors max near subgect";
					Log.d(TAG, Double.toString(maxskin[0]));
					Log.d(TAG, Double.toString(maxskin[1]));
					Log.d(TAG, Double.toString(maxskin[2]));

					TAG = "colors min subgect";
					Log.d(TAG, Double.toString(minskin[2]));
					TAG = "colors avg subgect";
					Log.d(TAG, Double.toString(values[2]));

					 	Scalar lower = new Scalar(0, 0, 0, 0);// sets the low base

			 	Scalar upper = new Scalar(180, 255, (int) values[2] * 1.75, 0);// sets the high base


				 	if (Math.abs(values[2] - 95) > 8)// if the calculate of avg color of the skin goes out of limit
						 	upper = new Scalar(170, 250, 94 * 1.75, 0);// set a fixed high base


					Core.inRange(srchsv, lower, upper, srchsv); //remove the blacks

					Imgproc.medianBlur(srchsv, srchsv, 11);// apply filters to remove the noise of unnecessary data

					Imgproc.GaussianBlur(srchsv, srchsv, new Size(3, 3), 0);
					Imgproc.Canny(srchsv, srchsv, 50, 100); // finding the edges of objects
					//srchsv in now binary
					int erosion_size = 3;// apply more filters...to make them smooth
					int dilation_size = 3;
					Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1, 2 * erosion_size + 1));
					Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));


					Imgproc.dilate(srchsv, srchsv, element);


					Imgproc.erode(srchsv, srchsv, element1);

					src = srchsv.clone();// gets a clone of the proceeded binary image


					Imgproc.findContours(srchsv, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
					// finds objects inside the image
					TAG = "contours size";
					Log.d(TAG, Integer.toString(contours.size()));

					temp = -1;
					int inContour[] = MyLib.insizecontor(contours); // keep the object with the right size
 					if (inContour[0] == -1) {//if nothing found try an alternative  image process
						contours = MyLib.findcontours(srchsv3);
						inContour = MyLib.insizecontor(contours); // keep the object with the right size
						srchsv = srchsv3; // recover the mat
					}

					if (inContour[0] != -1) // if countours found

					{
						//creating small mat obgects by croping the big one using hsv color space

						Mat[] intrestingresults = MyLib.firstmatresults(contours, inContour, srchsv2);
					//	returns the number of images that contain dark color
					//	and acceptable  height to width ratio
						int[] blackslikecon = MyLib.blacklikeresults(intrestingresults, inContour);
						TAG = "blacksinsize";
						try {
							if (blackslikecon[0] >= 0) // if black objects found

								{
								Log.d(TAG, Integer.toString(blackslikecon.length));

								temp = MyLib.bestbigcontor(contours, srchsv, blackslikecon); // choose the mole

								/////////////////////////////
									// calculates the pixel diameter

									int range = matrange(contours, temp);
								TAG = "best is";

								Log.d(TAG, Integer.toString(temp));

								TAG = "range in px  is";

								Log.d(TAG, Integer.toString(range));


//

								////////////////////////////////////////////////////////////////////////////////////////////////
// Get bounding box for contour i
									Rect rect = Imgproc.boundingRect(contours.get(temp));

									Rect R = rect;
								Mat MatToSave=untouched.clone();// make a ocpy of the untouched colored mat


								untouched = MyLib.cropskin(untouched, contours, temp);//crops the skins near the mole
								Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2RGB);//change color space from binary to rgb

								Mat untouched2 = MyLib.cropskin(src, contours, temp);// crops also the skin
								Mat ROI = untouched.submat(R); //Set ROI on source image

								float[] radius = new float[1];// the radius of the mole
								Point center = new Point();// center point
									// calculates radius and center for the untouched photo
								Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(temp).toArray()), center, radius);
								// calculates the center  for the cropped photo
								center.x = srcc.height() / 2 + center.x - base;
								center.y = srcc.width() / 2 + center.y - base;

								//cyrcle the mole
 								Imgproc.circle(dess, center, (int) radius[0], new Scalar(0, 255, 0), 15, 8, 0);

								// applay filters to cropped mole
								Imgproc.GaussianBlur(ROI, ROI, new Size(9, 9), 0);
								Imgproc.medianBlur(ROI, ROI, 11);

								// calculates histogram
								Mat hist = MyLib.histogram(ROI);
								// checks how many curves has the histogram for hsv
								colorproblem = MyLib.HistogramCheck(ROI);
								//change color space to rgb
								Imgproc.cvtColor(ROI, ROI, Imgproc.COLOR_HSV2RGB);


								// calculates the histogram for rgb
								Mat histrgb = MyLib.histogramrgb(ROI);


							Mat roitemp = new Mat(dess.height(), dess.width(), CvType.CV_8UC3);
								roitemp = dess;
							 	last = Bitmap.createBitmap(roitemp.width(), roitemp.height(), Bitmap.Config.RGB_565);
								//creates bitmap and pass  the image containing the circular mole
							 	Utils.matToBitmap(roitemp, last);
									// prints the circular mole

							  	doPhotoPrint(last);
 						Imgproc.cvtColor(untouched2, untouched2, Imgproc.COLOR_HSV2RGB);// change color space to RGB
								// crops the edged mat to the mole shape
								Mat unrotMAt = untouched2.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);// 8a ginei ena mikro girisma sto edged mat
								// calculates the angle that the image must rotate to be right aligned
								double angle = MyLib.FindAngle(contours, temp, unrotMAt);
								TAG = "angle  is";
								Log.d(TAG, Double.toString(angle));
								Mat unclearedged = new Mat();


								if (angle != 0)
									unclearedged = MyLib.rotMat(unrotMAt, angle);// rotates the mat if it must
								else
									unclearedged = unrotMAt;

									realwidth=		sensorh*unclearedged.width()/fl *1400;// calculates the real size of the mole
									//using the calculated camera info (camera pixel real size and focal length) and
									//the camera - mole  known distance 140 mm

									//realwidth && realheight unit of measure is mm/10
									// so to get realwidth&&realheight in   mm ,both must be multiplied by 10
									// the reason for that is that all is set to work with measure is mm/10 in this app
									realheight=		sensorh*unclearedged.height()/fl *1400;

									TAG = "subject realheight  is";
									Log.d(TAG, Double.toString(realheight));
									TAG = "subject realwidth  is";

									Log.d(TAG,Double.toString(realwidth));

								Mat edged = MyLib.removedges(unclearedged);// crops the images in the way that every one of
									//the four edges of the image
									//touches the perimeter of the mole
 						Bitmap fulledged = Bitmap.createBitmap(edged.width(), edged.height(), Bitmap.Config.RGB_565);
								Utils.matToBitmap(edged, fulledged);// creates an bitmap and passes the mat img in bitmap
 					int cols;
								Mat edgedhalf1;//divides the mat img into two halfs
								Mat edgedhalf2;
								if (edged.cols() % 2 > 0) {//if the number of cols is Even Number

									cols = edged.cols() - 1;
									edgedhalf1 = edged.submat(0, edged.rows() - 1, 0, cols / 2);

									edgedhalf2 = edged.submat(0, edged.rows() - 1, cols / 2, cols);

								} else {
									cols = edged.cols();
									edgedhalf1 = edged.submat(0, edged.rows() - 1, 0, cols / 2 - 1);
									edgedhalf2 = edged.submat(0, edged.rows() - 1, cols / 2 - 1, cols - 1);

								}

								//   edgedhalf2 = src.submat(0, src.rows()-1, cols/2-1, cols-1);
								Core.flip(edgedhalf2, edgedhalf2, 0);// between top-left and bottom-left image origin
								Core.flip(edgedhalf2, edgedhalf2, -1);//Simultaneous horizontal and vertical flipping
								morfology = MyLib.morfology(edgedhalf1, edgedhalf2);// creates the morphology of two images
								edgessimilarityproblem = MyLib.dangerousedges(morfology, edgedhalf1);//checks if there is asymmetry problem


								TAG = "edges similarity problem ";
								Log.d(TAG, Boolean.toString(edgessimilarityproblem));
								TAG = "edges  problem ";


							 maxsize = 0;
								if (edged.cols() >= edged.rows())// finds whats is bigger rows of cols
									maxsize = edged.cols();//there will be error if there is a gap of 5%*maxsize height
								else
									maxsize = edged.rows();

								edgesproblem = MyLib.convexity(contours, temp, maxsize);// finds if there is a perimeter error


								Log.d(TAG, Boolean.toString(edgesproblem));
							Configurations def=	db.getDEf();//gets apps configuration
if(!def.GetLanguage())// prints
								Toast.makeText(getApplicationContext(), "similarity " + Boolean.toString(edgessimilarityproblem) + "\n" + "legs problem " + Boolean.toString(edgesproblem) + "\n  curves :" + Double.toString(colorproblem), Toast.LENGTH_LONG).show();

// creates bitmaps and saves them to storage
								next = Bitmap.createBitmap(edgedhalf1.width(), edgedhalf1.height(), Bitmap.Config.RGB_565);
								Utils.matToBitmap(edgedhalf1, next);
								next2 = Bitmap.createBitmap(edgedhalf2.width(), edgedhalf2.height(), Bitmap.Config.RGB_565);
								Utils.matToBitmap(edgedhalf2, next2);


								Bitmap histbit = Bitmap.createBitmap(hist.width(), hist.height(), Bitmap.Config.RGB_565);
								Utils.matToBitmap(hist, histbit);

								Bitmap histbitrgb = Bitmap.createBitmap(histrgb.width(), histrgb.height(), Bitmap.Config.RGB_565);
								Utils.matToBitmap(histrgb, histbitrgb);



                                Bitmap Mole = Bitmap.createBitmap(MatToSave.width(), MatToSave.height(), Bitmap.Config.RGB_565);
                                Utils.matToBitmap(MatToSave, Mole);
                                savebitmap(Mole, 5);
          				         savebitmap(next, 0);
								savebitmap(next2, 1);
								savebitmap(histbit, 2);
								savebitmap(histbitrgb, 3);
								savebitmap(fulledged, 4);

// clear memory
								histbit.recycle();
								histbitrgb.recycle();
								fulledged.recycle();



							}
							else
							{//if there is no suitable objects  then prints the tryagain image
								ImageView img= (ImageView) findViewById(R.id.myimg);
								Configurations def = db.getDEf();
								if (def.GetLanguage())
									img.setImageResource(R.drawable.tryagain);
								else
									img.setImageResource(R.drawable.tryagainen);
// updates graphics
								getWindow().getDecorView().findViewById(R.id.frameid).invalidate();

							}

//		  } catch(NullPointerException e) {
						} catch (ArithmeticException e) {//if there is no black objects  then prints the tryagain image


							Log.d(TAG, " Den brethike kati  mesa sta xrwmata");

							ImageView img= (ImageView) findViewById(R.id.myimg);
							Configurations def = db.getDEf();
							if (def.GetLanguage())
							img.setImageResource(R.drawable.tryagain);
							else
								img.setImageResource(R.drawable.tryagainen);

// updates graphics
							getWindow().getDecorView().findViewById(R.id.frameid).invalidate();

                            break;

						}


					} else { // no objects found -> prints the tryagain image
						Log.d(TAG, "den uparxoun apotelesmata");
						ImageView img= (ImageView) findViewById(R.id.myimg);
						img.setImageResource(R.drawable.tryagain);
					//	Bitmap last = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.RGB_565);
						//Imgproc.cvtColor(src, src, Imgproc.COLOR_HSV2RGB);
                        getWindow().getDecorView().findViewById(R.id.frameid).invalidate();// updates graphics

					//	Utils.matToBitmap(src, last);

					//	doPhotoPrint(last);
					}
				}


				break;


				default: {
					super.onManagerConnected(status);
				}
				break;
			}
		}
	};
	static {
		if (!OpenCVLoader.initDebug()) {// if opencv non loaded successfully
			// Handle initialization error
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {//on crate
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {//gets data from previous activity
			Intent intent = getIntent();
			fl = intent.getFloatExtra("focal", 0); //if it's a string you stored.
			 sensorh = intent.getFloatExtra("sensor", 0); //if it's a string you stored.
			Log.d("pixel size",Float.toString(sensorh));


		}

        db = new DatabaseHandler(this);//links the db
        Configurations def = db.getDEf();// gets apps configuration
        if (def.GetLanguage())// load the right language xml
		setContentView(R.layout.view);
        else
            setContentView(R.layout.viewen);

        // Add a listener to the Capture button



        Button captureButton = (Button) findViewById(R.id.trymore); //if try again button is pressed
		captureButton.setFocusable(true);
		//		captureButton.setFocusableInTouchMode(true);
		captureButton.requestFocus();
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//

				if (temp >= 0) {// cleans ram ang goes to start screen
					last.recycle();
					next.recycle();
					next2.recycle();
					//	   bmp32.recycle();
				}
				gotostart();

				//

			}
		});


		Button captureButton2 = (Button) findViewById(R.id.bingo); // if the success button is pressed then
		captureButton2.setFocusable(true);
		//	captureButton2.setFocusableInTouchMode(true);
		captureButton2.requestFocus();
		captureButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "going to results");

				if (temp >= 0) {
					//	setContentView(R.layout.questionmark);
					Intent intent2 = new Intent(getApplicationContext(), Questionmark.class);
					last.recycle();//clean ram
					next.recycle();
					next2.recycle();
					//	   bmp32.recycle();


					String[][] tableString = new String[morfology.length][2]; //passes the data to next activity

					for (int i = 0; i < morfology.length; i++) {

						tableString[i][0] = String.valueOf(morfology[i][0]);
						tableString[i][1] = String.valueOf(morfology[i][1]);

					}

					Bundle b = new Bundle();
					b.putSerializable("tableString", tableString);

					intent2.putExtras(b);

					intent2.putExtra("realwidth", realwidth); //Optional parameters
					intent2.putExtra("realheight", realheight); //Optional parameters

					intent2.putExtra("edgesproblem", edgesproblem); //Optional parameters
					intent2.putExtra("colorproblem", colorproblem); //Optional parameters

					intent2.putExtra("edgessimilarityproblem", edgessimilarityproblem); //Optional parameters

                    intent2.putExtra("src", maxsize); //Optional parameters

					startActivity(intent2); //starts next activity



				}
				//

			}
		});


		Log.i(TAG, "Trying to load OpenCV library"); // loads opencv
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mOpenCVCallBack)) {
			Log.e(TAG, "Cannot connect to OpenCV Manager");
		}


	}

/*
this function  loads and return the captured image from storage
 */
	public Bitmap Getbitmap() {
		Log.e(TAG, "geting bitmap");
		String NOMEDIA = " .nomedia";



		File mediaStorageDir = new File(//gets the apps privet dir
				this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}// links the image file
		File pictureFile = new File(mediaStorageDir.getPath() + File.separator + "IMGtmp" + ".nodata");
		try {// load the file to stream
			imageStream = new FileInputStream(pictureFile);
			value = BitmapFactory.decodeStream(imageStream);// decode the stream into bitmap
			if (value.getWidth() >= value.getHeight()) {//crops the image . the new image will be square
				value = Bitmap.createBitmap(
						value,
						value.getWidth() / 2 - value.getHeight() / 2,
						0,
						value.getHeight(),
						value.getHeight()
				);

			} else {

				value = Bitmap.createBitmap(
						value,
						0,
						value.getHeight() / 2 - value.getWidth() / 2,
						value.getWidth(), value.getWidth()
				);

			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

/*
this function gets a bitmap and print it to R.id.myimg (xml image)
 */
	private void doPhotoPrint(Bitmap bMap) {
		ImageView image = (ImageView) findViewById(R.id.myimg);
		image.setImageBitmap(bMap);
	}

	// this function starts start screen activity
	public void gotostart() {

		Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
		startActivity(intent);


	}
// calculates the pixel diameter
// upologismos diametrou
	/*
	gets as input the list of found contours points and the pos of contour
	and returns the pixel diameter
	 */
	public static int matrange(List<MatOfPoint> contours, int matty)

	{
		int range = 0;
		Rect rect = new Rect();
		rect = Imgproc.boundingRect(contours.get(matty));
		if (rect.width >= rect.height) // returns what is bigger
			range = rect.width;
		else
			range = rect.height;


		return range * 101 / 100 / 2;//= range * 2 * 101%== diameter

	}





	@Override
	public void onBackPressed() {//when back button is pressed starts first screens activity
		Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
		startActivity(intent);

	}
//this function returns apps private dir
	public File getFile()
	{


		return  new File(this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
	}

	public static final int MEDIA_TYPE_IMAGE = 1;

/*
This function gets as input a bitmap and an int number
creates a dir file using the int number
and saves the image to file
 */
	public   void savebitmap(Bitmap data, int h) {

		Randar.pictureFile = getOutputMediaFile(h);// gets  dir
		NullPointerException e = null;
		if (Randar.pictureFile == null) {
			Log.d(TAG,
					"Error creating media file, check storage permissions: "
							+ e.getMessage());
			return;
		}

		try {
			FileOutputStream fos = new FileOutputStream(Randar.pictureFile); //gets the file into stream

			data.compress(Bitmap.CompressFormat.JPEG, 100, fos);//compresses the image
			fos.flush();//writes the image to file

			fos.close();

		} catch (FileNotFoundException e1) {
			Log.d(TAG, "File not found: " + e1.getMessage());
		} catch (IOException e1) {
			Log.d(TAG, "Error accessing file: " + e1.getMessage());
		}


	}
/*
this function gets an int  number as input
creates the apps private dir if not exist,
// generates a dir name using the int,
 creates the file
 and returns the dir name
 */
	public     File getOutputMediaFile( int h) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		String NOMEDIA=" .nomedia";
	File mediaStorageDir = getFile();
        if(!mediaStorageDir.exists())

	{
		if (!mediaStorageDir.mkdirs()) {
			Log.d("skinhealthchecker", "failed to create directory");
			return null;
		}
	}

	// Create a media file name
	// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
	// Date());
	File mediaFile;



		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMGtmp2" + Integer.toString(h) + ".nodata");
// the name of the file will be IMGtmp2+h+.nodata


        return mediaFile;// returns the file dir

}












}



