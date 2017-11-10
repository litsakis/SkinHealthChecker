package com.example.skinhealthchecker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.PI;

/**
 * Περιέχει μεθόδους που χρησιμοποιούνται σε όλη την εφαρμογή .  Είναι συγκεντρωμένες σε ένα αρχείο java για καλύτερη οργάνωση του κώδικα.

 * Contains methods used throughout the application. They are gathered in a java file for better organization of the code.

 */
public class MyLib {

   static String TAG;

//crops center of the images ->input mat  output cropped mat
    public static Mat getcenterareamat (Mat src){


        int  x=src.rows();
        int y=src.cols();

        Randar.base =Math.abs(src.cols()/10);// calculating new image side
        Log.i(TAG, "center base");
        Log.d(TAG,Integer.toString((int) Randar.base) );


        Log.i(TAG, "image size");
        Log.d(TAG,Integer.toString((int) x) );

        // Rect roi = new Rect(x/2-2*x/5, y/2-2*y/5, 2*x/(3), 2*y/(3));

        //   Rect roi = new Rect(500-x/2, 500-y/2, 2*x/(3), 2*y/(3));

       // masks for crop
        Rect roi = new Rect(Math.abs(x/2)-Randar.base,Math.abs( y/2)-Randar.base, 2*Randar.base, 2*Randar.base);

        Mat cropped = new Mat(src, roi);// creating new image.
        // new image site will be : oldimage.hight/10^2
        //   out = src.getSubimage(x, y, w-2*x, h-2*y);
        return cropped;

    }
//returns objects within size limits
// επιστρέφει τα αντικείμενα που είναι εντός ορίων μεγέθους
// input countours list -> output  id list of items in size limit
    public static int []  insizecontor ( List<MatOfPoint> contours)
    {int [] Contourofinterest;

        int [] largestContour = new int [contours.size()];// creating the list
        int num=0;
        double area = 0;
        int seclarge = 0;
        double Area2=0;
        int z=0;
        TAG ="contors size";
        for (int i = 0; i < contours.size(); i++)
        {

            double cArea =
                    Imgproc.contourArea(contours.get(i));
            if (cArea > Randar.minsearchsize && cArea<Randar.maxsearchsize) // if the item size is in limit
            {

                //		  des=targetcircle(des,new Point(resultx,resulty),range);
                Log.d(TAG,Integer.toString((int)cArea) );
                area = cArea; largestContour[z]=i;z++; // save  the item , make the counter z bigger

            }
        }



        if (z>0){/// creating new list filled by ids of items
            Contourofinterest = new int [z];

            for(int i=0;i<z-1;i++)

            {Contourofinterest[i]=largestContour[i];

            }}
        else // is there is no countor in size then  return  error "-1"
        {	  Contourofinterest = new int [1];

            Contourofinterest[0] = -1;



        }
        return Contourofinterest;

    }
    //creating small mat obgects by croping the big one using hsv color space
    // dimiourgw mikra mat arxeia se morfh hsv etsi wste na ta eksetasw ena ena
    //
    //input list of countours , list of obgect in size , image in hsv color space
    //output : list of Mats (found items mats)
    public static Mat []  firstmatresults ( List<MatOfPoint> contours,int [] list,Mat srchsv)

    {
        Rect[] rect = new Rect [list.length ];  // list of rects / masks for crop
        Mat [] roi = new Mat [list.length ];//list of croped temp mats
        // we need a list of temp mat and not a mat because the size of temp mat is not stable
        Mat [] mats= new Mat [list.length];// the returning mats of obgects

        for(int i=0;i< list.length ; i++)
        {	  // srchsv
            rect[i] = Imgproc.boundingRect(contours.get( list[i] )); // getting the borders of a rect near the object

            roi[i] = srchsv.submat(rect[i].y, rect[i].y + rect[i].height, rect[i].x, rect[i].x + rect[i].width);// crops the init image

            mats[i] = new Mat (roi[i].height(), roi[i].width(), CvType.CV_8UC3);// just copy the cropped to mat

            mats[i]= roi[i];

        }

        return mats;//returs the list


    }
    // επιστρέφει τον αριθμό απο τις εικόνες που περιέχουν σκούρο χρώμa και σωστη αναλογία
    /*
    epistréfei ton arithmó apo tis eikónes pou periéchoun skoúro chróma
    returns the number of images that contain dark color
    and acceptable  height to width ratio

    input: obgects mats ,list with positions of oobjects in contours list

    */
    public static int [] blacklikeresults (  Mat [] matlist ,int [] list )
    {  int temp=2;
        int [] templist  = new int [matlist.length];// list of possitions of obgects (with right blacklike pixels and
        //height / weight ratio
        Randar.blackpix  = new int [matlist.length]; // contains the size of blacklike pixels in oobjects



        int z=-1; // just a counter
        //Mat [] nonZeroCoordinates = new Mat [matlist.length];
        Mat [] nonZeroCoordinates = new Mat [matlist.length];  //will contain the map of obgect pixel that is
        // in black range (E.G if a pixel is non zero then will have value 1 )
        Mat [] tempmat = new Mat [matlist.length]; //will contain the obgect img but  if somepixel
        //is not in black range -> those will be 0
        for(int i=0;i<matlist.length;i++)
        {
            if (matlist[i].height()>=matlist[i].width()) // checking the ratio
            {
                temp=matlist[i].height()/matlist[i].width();

            }
            else

            {				temp=matlist[i].width()/matlist[i].height();
            }

            //elenxw an oi diastaseis einai konta se diastaseis tetragwnou
            // checking if the ratio is less than 3
            if (temp<3)
            {


                nonZeroCoordinates[i]=new Mat (matlist[i].height(), matlist[i].width(), CvType.CV_8UC3);
                tempmat[i]=	new Mat (matlist[i].height(), matlist[i].width(), CvType.CV_8UC3);
                Scalar lower = new Scalar(0, 0, 0);/// lower hsv range

                //    Scalar upper = new Scalar(180, 255, 90,0); //blacks
                Scalar upper = new Scalar(179, 255, 150); //blacks // upper hsv range
                // Scalar upper = new Scalar (255,255,minskin[2]);

                //    Scalar upper = new Scalar(180, 255, 90,0); //blacks
                Core.inRange(matlist[i], lower, upper, tempmat[i]); // all pixels not in ranges gets value of 0

                Core.findNonZero(tempmat[i], nonZeroCoordinates[i]);// counting non-zero pixels

                TAG= "nonzero";
                Log.d(TAG,Integer.toString((int) nonZeroCoordinates[i].total()) );

                //orizw oti ta epithimita xrwmata 8a prepei na einai mesa sthn komenh eikonas
                //	if (nonZeroCoordinates.total()>matlist[i].total()*1/1000)
                if (Randar.MINBLACK<=nonZeroCoordinates[i].total() ) // if non pixel values is many


                {
              //      if(matlist[i].total()>=Randar.MINBLACK){
                        z++;// up the counter
                        templist[z]=list[i]; // get the id of the obgect
                        Randar.blackpix[z]=(int) nonZeroCoordinates[i].total(); // note the number of black pixels

                        Log.e(TAG, "pixels blacks");
                        Log.d(TAG,Integer.toString((int) nonZeroCoordinates[i].total()) );

                        Log.e(TAG, "numbest");
                        Log.d(TAG,Integer.toString((int) list[i]) );
               //     }
                }


            }




        }


        if (z==-1){// if non object found
            int [] returnlist  = new int [1];
            returnlist[0]=-1;// return error (-1)
            return returnlist;}
        else {
            int [] returnlist  = new int [z+1];//else  return the list

            for (int i=0;i<=z;i++)
            {

                returnlist[i]=templist[i];

            }





            //return list;

            return returnlist;

        }


    }
    /*
    επιστρέφει το καλύτερο εντοπισμό αντικείμενο με βάση το μέγεθος το χρώμα και την θέση του
    returns the best targeting object based on its size and location in the init image = the mole
    input :all contours, input image , the list with black obgects
    output : the id of mole (luckily)
   */
    public static int   bestbigcontor (   List<MatOfPoint> contours ,Mat src,int[] blackslikecon)
    {
        int bestContour = blackslikecon[0];// init the returning var with the id of the first object of the list
        double  bestx=0,besty=0; // init the best coordinates

        int bestnumblack=0; //blacks of selected object


        Point v;//temp var of points
        Point[]   points_contour; // list of points
        int nbPoints=0; // length of points of each obgect.
        double centerx=0 ,centery=0 ;// init  object coordinates

        double size =0 ;
        for (int i = 0; i <blackslikecon.length; i++)
        {

            centerx=0 ;centery=0 ;// init  object coordinates

            MatOfPoint 	best = contours.get(blackslikecon[i]);// gets points of the checking object
            points_contour = best.toArray();
            nbPoints = points_contour.length;


            for( int  j=0; j< nbPoints;j++)
            {
                v=points_contour[j];
                // for each point getting the sums of x's and y's
                centerx =centerx+v.x;
                centery =centery+v.y;


            }

            if(nbPoints!=0 )
            {centerx=centerx/nbPoints;// // for each object calculates  the avg's of x's and y's
                //the avg of each one will be the center point of the obgect
                centery=centery/nbPoints;

                //calculating the distance of the center point and the center of the image
                //if the distance is smaller comparing to best selected (for the moment ) object
                if( 2.3* Math.sqrt(Math.pow(Math.abs(bestx-src.rows()/2),2)+Math.pow(Math.abs(besty-src.cols()/2),2))>Math.sqrt(Math.pow(Math.abs(centerx-src.rows()/2),2)+Math.pow(Math.abs(centery-src.cols()/2),2))  ) {
                    //if the object center is somewhere near image center Eg in a rect at center with size src.cols*src*rows
                    if (((Math.abs(centerx - src.rows()/2))<=(src.rows()/8))&&((Math.abs(centery - src.cols()/2))<=(src.cols()/8))) {
                        // the new best must be at least 0.68 times as big as the old best
                        if (Imgproc.contourArea(contours.get(blackslikecon[i])) >= Imgproc.contourArea(contours.get(bestContour))*0.68)
                            if (bestnumblack <= Randar.blackpix[i] * 0.8) {// and must have at least 0.8 times the black pixels of old best

                                bestx = centerx;// There is new best !!
                                besty = centery;
                                bestnumblack = Randar.blackpix[i];
                                bestContour = blackslikecon[i];
                            }

                    }
                }

            }





        }

        if ((blackslikecon.length==1 ) && (blackslikecon!=null))
        {
//if there is only one object then calculates the centet x,y and returns the id number

            MatOfPoint 	best = contours.get(blackslikecon[0]);
            points_contour = best.toArray();
            nbPoints = points_contour.length;


            for( int  j=0; j< nbPoints;j++)
            {
                v=points_contour[j];

                centerx =centerx+v.x;
                centery =centery+v.y;


            }

            if(nbPoints!=0 )
            {centerx=centerx/nbPoints;
                centery=centery/nbPoints;


                if( Math.sqrt(Math.pow(Math.abs(bestx-src.width()/2),2)+Math.pow(Math.abs(besty-src.height()/2),2))>Math.sqrt(Math.pow(Math.abs(centerx-src.width()/2),2)+Math.pow(Math.abs(centery-src.height()/2),2) ) )
                {
                    bestx=centerx;
                    besty=centery;



                }

                //	  retur[0]=blackslikecon[0];
                //	  retur[0]=1;

                // retur[1]=(int) centerx;
                //		retur[0]=(int) centery;
                Randar.resultx=centerx;
                Randar.resulty=centery;
                return blackslikecon[0];
            }
        }
        //	  }


        Log.e(TAG, "best");
        Log.d(TAG,Integer.toString((int) bestContour) );


        Log.e(TAG, "blacks on best");
        Log.d(TAG,Integer.toString((int) bestnumblack) );

        //
        //  retur[0]=bestContour;
        //  retur[1]=(int) centerx;
        //	retur[0]=(int) centery;
        Randar.resultx=centerx;
        Randar.resulty=centery;

        return bestContour;
    }
    // αναλύει τις τιμές του χρώματος γύρω απο την ελιά
    //analyzes the color values around the mole
    // input: the image -> outputs avg lightness
    public static double[] rightedge (Mat img)
    {
        int x =0; int y=0 ;

        Imgproc.medianBlur ( img, img, 11 ); // apply median filter to reduce noice

        Randar.minskin =img.get(0,0);//init vars
        Randar.maxskin =img.get(0,0);
 // color sums
        double[] datasum = img.get(img.height()/2,img.height()/2);



 // init
        datasum[0]=0 ;
        datasum[1]=0 ;
        datasum[2]=0 ;

  // gets color value on each of 4  edges (gets 20 values of each edge and sums them )
        for (int i=0;i<img.rows()*4;i=i+img.rows()/20){
            if (i<img.rows())
            {
                x=img.rows()/30;

                y=i;
            }
            else if (i<img.rows()*2)

            {
                x=img.rows()-(int)(img.rows()*0.3);

                y=i-img.rows();


            }
            else if (i<img.rows()*3)
            {


                y=img.rows()/30;

                x=i-img.rows()*2;

            }
            else if (i<img.rows()*4)
            {


                y=img.rows()-(int)(img.rows()*0.3);

                x=i-img.rows()*3;

            }



            double[] data = img.get(x, y);
            // the v (data[2]) of pixel on the hsv color space is the lightness of each pixel
            data[0] = data[0] / 2;// gets hsv data
            data[1] = data[1] / 2;
            data[2] = data[2] / 2;
            if (data[2]<Randar.minskin[2]) //the lowest  value of hsv samples
            {
                if (data[2]>63){ //the sample must not be too black

                    Randar.minskin[2]=data[2];
                }

            }

            if (data[2]>Randar.maxskin[2])
            {

                Randar.maxskin[0]=data[2]; // the biggest value of lightness sample


            }

            if (data[1]<Randar.minskin[1])
            {

                // min saturation
                Randar.minskin[1]=data[1];



            }

            if (data[1]>Randar.maxskin[1])
            {
                // max saturation
                Randar.maxskin[1]=data[1];



            }


            if (data[0]<Randar.minskin[0])
            {
            // min hue
                Randar.minskin[0]=data[0];



            }

            if (data[0]>Randar.maxskin[0])
            {// max hue
                Randar.maxskin[0]=data[0];


            }

            // calculates  sums for  each of h s v

            datasum[0]=datasum[0]+data[0] ;
            datasum[1]=datasum[1]+data[1] ;
         //   if (data[2]<30)
           //     data[2]=50;
            datasum[2]=datasum[2]+data[2] ;
        }

// calculates avg on each h s v
        datasum[0]=datasum[0]/80;
        datasum[1]=datasum[1]/80 ;
        datasum[2]=datasum[2]/80 ;

        TAG="minskin";
                Log.d(TAG,Double.toString(Randar.minskin[2])) ;
      //  Randar.minskin[2]= datasum[2];
//  returns the avg of each of h s v
        return datasum;
    }

// εναλακτικός εντοπισμός αντικειμένων
//alternative object tracking
//the function gets as input an image  and returns a list of points of tracked  objects
    public static List<MatOfPoint> findcontours  (Mat srchsv3)

    {       //init list of points of tracked  objects
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();;
            //init hierarchy of tracked objects
        Mat hierarchy = new Mat();
            // apply median filter to image
        Imgproc.medianBlur ( srchsv3, srchsv3, 11 );
            // apply gaussian filter to image
        Imgproc.GaussianBlur(srchsv3, srchsv3, new Size(3, 3), 0);
        //srchsv3 = removewhites(srchsv3);
            // converts image to grayscale (to binary image )
        Imgproc.cvtColor(srchsv3, srchsv3, Imgproc.COLOR_BGR2GRAY);

        // searchs for objects
        Imgproc.findContours(srchsv3, contours, hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        TAG ="contours2 size";
        Log.d(TAG,Integer.toString(contours.size()) );
//returns a list of points of tracked  objects
        return contours;
    }
//





    public static final int MEDIA_TYPE_IMAGE = 1;


/*

    /creates the image histogram in the RGB space

    // δημιουργεί το ιστόγραμμα της εικόνας στον χωρο RGB
    geting input of mat image in rgb color space
    returns  mat images of histogram

*/
    public static Mat histogramrgb (Mat hsvSource )
    {
        List<Mat> images = new ArrayList<Mat>();
        Core.split(hsvSource, images);

        // set the number of bins at 40
        MatOfInt histSize = new MatOfInt(40);
        // only one channel
        MatOfInt channels = new MatOfInt(0);
        // set the ranges
        MatOfFloat histRange = new MatOfFloat(0, 256);

        // compute the histograms for the B, G and R components
        Mat hist_b = new Mat();
        Mat hist_g = new Mat();
        Mat hist_r = new Mat();

        // B component or gray image
        Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);

        // G and R components (if the image is not in gray scale)

        Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
        Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);


        // draw the histogram
        int hist_w = 500; // width of the histogram image
        int hist_h = 400; // height of the histogram image
        int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

        Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
        // normalize the result to [0, histImage.rows()]
        Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

        // for G and R components

        Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());


        // effectively draw the histogram(s)
        for (int i = 1; i < histSize.get(0, 0)[0]; i++)
        {

            // B component or gray image
            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
            // G and R components (if the image is not in gray scale)

            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
                    0);
            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
                    0);



        }
        return histImage;

    }
/*
//  δημιουργεί το ιστόγραμμα της εικόνας στον χωρο HSV
    creates the histogram of the image in the HSV space


      geting input of mat image in hsv color space
    returns  mat images of histogram
  */  public static Mat histogram (Mat hsvSource )
    {
        Mat hsvRef =hsvSource.clone();
        List<Mat> images = new ArrayList<Mat>();
        Core.split(hsvSource, images);


        // sets the bins 60 for h and  64 for S and V
        int bin =60;
        int bin2=64;

        MatOfInt histSize1 = new MatOfInt(bin);
        MatOfInt histSize2 = new MatOfInt(bin2);
        MatOfInt channels = new MatOfInt(0);

        // sets the ranges  180 for h because it gets values from 0 to 180
        MatOfFloat histRange1 = new MatOfFloat(1, 180);
        // sets the ranges  for SV ->256  because it gets values from 0 to 255
        // skips the first 10 values because  the 0.0.255 in hsv is white!!
        // but the device flash creates a lot of whites and those whites must be skipped
        MatOfFloat histRange2 = new MatOfFloat(10, 255);
        Mat hist_h = new Mat();
        Mat hist_s = new Mat();
        Mat hist_v = new Mat();

        // H component

        Imgproc.calcHist(images.subList(0, 1),
                channels,
                new Mat(),
                hist_h,
                histSize1,
                histRange1,
                false);
        // S component
        Imgproc.calcHist(images.subList(1, 2),
                channels,
                new Mat(),
                hist_s,
                histSize2,
                histRange2,
                false);
        // V component
        Imgproc.calcHist(images.subList(2, 3),
                channels,
                new Mat(),
                hist_v,
                histSize2,
                histRange2,
                false);
        int hiw = 500; // width of the histogram image
        int hih = 400; // height of the histogram image
        int biw1 = (int) Math.round(hiw / histSize1.get(0, 0)[0]);

        int biw2 = (int) Math.round(hiw / histSize2.get(0, 0)[0]);
        Mat histImage = new Mat(hih, hiw, CvType.CV_8UC3, new Scalar(0, 0, 0));
        // normalize the result to [0, histImage.rows()]

        Core.normalize(hist_h, hist_h, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_s, hist_s, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_v, hist_v, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        double fullsize= hsvSource.cols()*hsvSource.rows() *15/600;


// ignores values smaller than fullsize and makes them zero

        Imgproc.threshold(hist_h,hist_h,fullsize,0,Imgproc.THRESH_TOZERO		);

        Imgproc.threshold(hist_s,hist_s,fullsize,0,Imgproc.THRESH_TOZERO		);

        for (int i = 1; i < histSize2.get(0, 0)[0]; i++)
        {
            // h component or gray image
            if (i<bin)

                Imgproc.line(histImage, new Point(biw1 * (i - 1), hih - Math.round(hist_h.get(i - 1, 0)[0])),
                        new Point(biw1 * (i), hih - Math.round(hist_h.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
            // s and v components (if the image is not in gray scale)

            Imgproc.line(histImage, new Point(biw2 * (i - 1), hih - Math.round(hist_s.get(i - 1, 0)[0])),
                    new Point(biw2 * (i), hih - Math.round(hist_s.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
                    0);
            //	 /**
            Imgproc.line(histImage, new Point(biw2 * (i - 1), hih - Math.round(hist_v.get(i - 1, 0)[0])),
                    new Point(biw2 * (i), hih - Math.round(hist_v.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
                    0);
            //	 **/
        }




        TAG ="histtest";
        for (int i=0;i<64;i++)
            Log.d(TAG,Double.toString(hist_s.get(i,0)[0]));





        return histImage;

    }

// not used but working
/*


// αφαιρεί ότι πιξελ είναι φωτεινο και το μετατρέπει σε άσπρο
removes that pixel is bright and turns it into white

//input: Mat image
//output: processed mat image

 */

    public static Mat removeskin (Mat img ){

        double[] data  ;
        Mat C = img.clone();// copy the image


        for (int i=0 ; i< img.height();i++)
        {

            for (int j=0; j< img.width() ;j++)
            {
// for every pixel of the image if value of V is bigger than min of skin color
                // make tha pixel white
                data  = img.get(i,j);
                if (data[2]/2>Randar.minskin[2]*0.85 )
                {

                    data[0] = 0;
                    data[1] = 0;
                    data[2] = 255;

                    C.put(i, j, data);


                }

                C.put(i, j, data);


            }





        }




// possessed image

        return C;


    }
// αφερεί οτι βρισκεται εξω απο τα όρια της ελιάς
/*
remove whatever is outside the mole
// inputs the image of the object , list of countours points , the position of the ccontourbject
// returns possessed image

 */
    public static Mat cropskin (Mat img,List<MatOfPoint> contours ,int pos) {


        Imgproc.cvtColor(img,img,Imgproc.COLOR_RGB2HSV);
        // change the color of the image to hsv

        Mat mask = Mat.zeros(img.rows(), img.cols(), CvType.CV_8UC1); // creating empty mat the same size as input image
        Imgproc.drawContours(mask, contours, pos,  new Scalar(255), Core.FILLED); //drawing the object
        Mat crop = new Mat (img.rows(), img.cols(), CvType.CV_8UC3);
        crop.setTo(new Scalar(0,0,255));//create a new mat filled by white colors pixels

        img.copyTo(crop, mask);// copy the object to crop mat


 //returns the only the mole . outside the edges of the mole will be white
        return crop;
    }
// υπολογίζει το πόσες μοίρες πρέπει να περιστραφεί η ελιά
    /*
//

// inputs the edges   of the object (as mat binary image), list of contours points , the position of the contour object
// returns the angle

calculates how many degrees the mole must rotate . The mole must be on upright position->
->calculates   the positions of two the most away pixels of mole and also the angle that the
mole must rotate to make those two pixels to be aligned

     */


    public static double  FindAngle ( List<MatOfPoint> contours,int temp ,Mat src) {
        double data [];

        //	/**

        int x1 = 0;//x1y1 x2y2 is the positions of most far away pixels of mole
        int x2 = 0;
        int y1 = 0;
        int y2 = 0;
        int [][] pos;// will contains the pos of edge on each row
        // pos[i][0] for right edge
        //pos[i][1] for left edge
        double deltaX; // x2-x1
        double deltaY; // y2 - y1
         if (src.rows()>src.cols()) { //if the rows are bigger than the cols . make the search by rows

            pos= new int[src.rows()][2];

            for (int i = 0; i < src.rows(); i++) {//first side -> for every row find the coordinate of the edge
                for (int j = 0; j < src.cols(); j++) {

                    data = src.get(i, j);//reads every pixel
                    if (data[0] == 0.0D) { // the edge will be a white line


                        pos[i][0] = j;// save pos and go to next line
                        break;
                    }


                }

                for (int j = src.cols() - 1; j >= 0; j--) { //second side -> for every row find the coordinate of the edge

                    data = src.get(i, j);//reads every pixel
                    if (data[0] == 0.0D) {// the edge will be a white line

                        pos[i][1] = j;// save pos and go to next line
                        break;
                    }

                }


            }
            double dist = 0; // calc the distance of each pear
            double biggerdist = 0;//   distance of best pear

            Point[] points1 = new Point[src.rows()];
            Point[] points2 = new Point[src.rows()];

            for (int i = 0; i < src.rows(); i++) {// creates points from prev data


                points1[i] = new Point(i, pos[i][0]);
                points2[i] = new Point(i, pos[i][1]);


            }
            for (int i = 0; i < src.rows(); i++) {
                for (int j = 0; j < src.rows(); j++) {// calculates each distance keeps best

                    dist = Math.hypot(i - j, pos[i][0] - pos[j][1]);

                    if (biggerdist <= dist) {
                        biggerdist = dist;
                        x1 = i;
                        x2 = j;


                    }
                }
            }



            deltaY = pos[x2][1] - pos[x1][0];// dif's of ys
            deltaX = x2 - x1;// difs of x's
        }//if the cols are bigger than the cols . make the search by cols
        else{
            pos= new int[src.cols()][2];

            for (int i = 0; i < src.cols(); i++) {//first side -> for every cols find the coordinate of the edge
                for (int j = 0; j < src.rows(); j++) {

                    data = src.get(j, i);//reads every pixel
                    if (data[0] == 0.0D) {// the edge will be a white line

                        pos[i][0] = j;// save pos and go to next col
                        break;
                    }


                }

                for (int j = src.rows() - 1; j >= 0; j--) { //second side -> for every col find the coordinate of the edge

                    data = src.get(j, i);//reads every pixel
                    if (data[0] == 0.0D) {// the edge will be a white line

                        pos[i][1] = j;// save pos and go to next col
                        break;
                    }

                }


            }

            double dist=0;
            double	biggerdist=0;
            for (int i = 0; i < src.cols(); i++) {// calculates each distance keeps best
                for (int j = 0; j < src.cols(); j++) {

                    dist = Math.hypot(pos[i][0] -pos[j][1], i - j);
                    if (biggerdist <= dist) {
                        biggerdist = dist;
                        x1 = i;
                        x2 = j;
                    }
                }
            }

            deltaX = pos[x2][1] - pos[x1][0];// dif's of x's
            deltaY = x2 - x1;// dif's of ys
       int temp1 ,temp2;
             temp1 =pos[x1][0];
             temp2 =pos[x2][1];// swapping the values of x's and y's
             pos[x1][0]=x1;
             pos[x2][1]=x2;
             x1=temp1;
             x2=temp2;
        }



        double blob_angle_deg = Math.atan2(deltaY, deltaX) * 180 / PI;
        blob_angle_deg=90-blob_angle_deg;// calculates the angle for them to be aligned
        TAG ="angle";
        Log.d(TAG,Double.toString( blob_angle_deg));

        if ((pos[x2][1]<src.cols()/2)&&(x2<src.rows()/2)) {// finding in which quartile is the position
            if (blob_angle_deg < 90)// and fixes the angle  . all mole s must always analyzed being in same "direction "
                blob_angle_deg = 90 + blob_angle_deg;
            else if (blob_angle_deg > 270)
                blob_angle_deg = blob_angle_deg + 90;

        }else if
                ((pos[x2][1]>src.cols()/2)&&(x2<src.rows()/2)){
            if (blob_angle_deg > 90 || blob_angle_deg < 270)
                blob_angle_deg = 90 + blob_angle_deg;}
        else if
                ((pos[x2][1]>src.cols()/2)&&(x2>src.rows()/2)){
            if (blob_angle_deg > 270 || blob_angle_deg < 90)
                blob_angle_deg = 90 + blob_angle_deg;}
        else if
                ((pos[x2][1]<src.cols()/2)&&(x2>src.rows()/2)){
            if (blob_angle_deg < 270 || blob_angle_deg > 90)
                blob_angle_deg = 90 + blob_angle_deg;}
//blob_angle_deg = 90 + blob_angle_deg;
        double upperpixels =0;
        double lowerpixels =0;


        for (int i=0; i< src.height()/2 ;i++) { // calculates the upper side mole's edges pixels

            for (int j=0; j< src.width() ;j++) {

                data =src.get(i,j);
                if (data[0]==0.0D) {
                    upperpixels++;

                }

            }
        }


        for (int i=src.height()/2-1; i< src.height() ;i++) {// calculates the lower side mole's edges pixels

            for (int j=0; j< src.width() ;j++) {

                data =src.get(i,j);
                if (data[0]==0.0D) {
                    lowerpixels++;

                }

            }
        }


        Log.d("\nupper pixels "+Double.toString(upperpixels)+"\n","lower pixels "+Double.toString(lowerpixels)+"\n");

        if (lowerpixels<upperpixels)//if lower edges pixels less than the appears then pivots the mole
        {

            if (blob_angle_deg>180)
                blob_angle_deg=blob_angle_deg-180;
            else
                blob_angle_deg=blob_angle_deg+180;


        }



        if (blob_angle_deg>360) // if the angle is out of limit -> fixes it  (if it is bigger than 360 or lower than 0)
            blob_angle_deg=blob_angle_deg-360;
        else if (blob_angle_deg<0)
            blob_angle_deg=blob_angle_deg+360;




        return blob_angle_deg;


    }
/*
rotates the mat at some angle

input: src  img and angle

//output prossesed image
 */

    public static Mat rotMat (Mat src,double angle){

        Point src_center = new Point (src.cols()/2.0F, src.rows()/2.0F); // the center of the mat
        Mat rot_mat = Imgproc.getRotationMatrix2D(src_center, angle, 1.0);//Calculates an affine matrix of 2D rotation.


        Mat dst = new Mat();
        //	Imgproc.warpAffine(src, dst, rot_mat, src.size());
        //	Imgproc.warpAffine(src, dst, rot_mat, src.size(),Imgproc.INTER_LINEAR,Core.	BORDER_WRAP	,new Scalar(255, 255, 255));


        Imgproc.warpAffine(src, dst, rot_mat, src.size(),Imgproc.INTER_CUBIC, Core.		BORDER_CONSTANT,new Scalar(255, 255, 255));
        // rotates the mat and the outcome goes to dst mat
        //the new pixels that will appear after the rot will be black !

        //	TAG ="img type   is";
        //	Log.d(TAG,Double.toString(dst.type()) );


        return  dst;
    }
// κάνει crop την εικόνα της ελιάς έτσι ώστε η ελιά να βρίσκεται σε κάθε ακρη της εικόνας

    /*
    crops the image as all edge of the image will contain pixel of mole
     */
    public static Mat removedges (Mat src)

    {
        Mat clear = new Mat();


        double data [];
        int possitionupx=-1;// inits all 4 edges coordinates
        int possitionupy=-1;

        int possitiondownx=-1;
        int possitiondowny=-1;

        int possitionleftx=-1;
        int possitionlefty=-1;

        int possitionrightx=-1;
        int possitionrighty=-1;


        data =src.get(src.rows()/2,src.cols()/2); //init  the temp data

        TAG ="up";
        Log.d(TAG,Double.toString(data[0])+"+"+Double.toString(data[1])+"+" +Double.toString(data[2]));


        for (int i=0; i< src.cols() ;i++) {// for each edge finding the coordinates of the nearest pixel of the mole

            for (int j=0; j< src.rows() ;j++) {

                data =src.get(j,i);
                if (data[0]==0.0D) {
                    possitionleftx=j;
                    possitionlefty=i;
                    break;
                }

            }
            if (possitionleftx>-1)
                break;

        }

        for (int i=0; i< src.rows() ;i++) {

            for (int j=0; j< src.cols() ;j++) {

                data =src.get(i,j);
                if (data[0]==0.0D) {
                    possitionupx=i;
                    possitionupy=j;
                    break;
                }

            }
            if (possitionupx>-1)
                break;
        }



        for (int i=src.cols()-1; i>=0  ;i--) {

            for (int j=src.rows()-1; j>=0 ;j--) {

                data =src.get(j,i);
                if (data[0]==0.0D) {
                    possitionrightx=j;
                    possitionrighty=i;
                    break;
                }

            }
            if (possitionrightx>-1)
                break;
        }

        for (int i=src.rows()-1; i>=0  ;i--) {

            for (int j=src.cols()-1; j>=0 ;j--) {

                data =src.get(i,j);
                if (data[0]==0.0D) {
                    possitiondownx=i;
                    possitiondowny=j;
                    break;
                }

            }

            if (possitiondownx>-1)
                break;
        }

        TAG ="up";
     //  Log.d(TAG,Double.toString(possitionupx)+Double.toString(possitionlefty) );

        TAG ="down";
   //     Log.d(TAG,Double.toString(possitiondownx)+Double.toString(possitionrighty) );

        if((possitionupx<possitiondownx) && (possitionlefty<possitionrighty) ) { // if the coordinates seems to be right

            //	Point up = new Point(possitionupx, possitionlefty);
            //	Point down = new Point(possitiondownx, possitionrighty);


            //	Rect rect = new Rect(up, down);
            //	Rect rect = new Rect(possitionupx,possitionlefty,possitionrighty-possitionlefty,possitiondownx-possitionupx);

            //clear = src.submat(rect);
            clear = src.submat(possitionupx,possitiondownx,possitionlefty,possitionrighty); // crops the images
            return clear;
        }
        return  src;




    }






// εξάγει την μορφολογία της ελιάς σε πίνακα
    // calculates the morphology of moles's edges  and returns it to double

    // input 2 mats of cropped mole rotated to same way
    // output  double  containing the morphology

    //the morphology is the position of the mole edge for every line

    public static double[][] morfology(Mat src1,Mat src2)
//	public void morfology(Mat src1,Mat src2)


    {
        double data1 [];// temp data for first mat
        double data2 [];// temp data for the second mat

        double morph [][] = new double[src1.rows()][2] ; // the returning morphology
        int rows =0;//the biggest num of rows and cols of image because one of to mats maybe have one more col  .
        // this situation happens when the pre- cropeed image has odd namber of col or row
        int cols =0;

        if (src1.rows()>src2.rows())
            rows=src1.rows();
        else	 	rows=src2.rows();

        if (src1.cols()>src2.cols())
            cols=src1.cols();
        else	 	cols=src2.cols();

        for (int i=0;i<rows;i++)
        {int stop1=0;
            int stop2=0;
//		 for (int j=src1.cols()-1; j>=0;j--)

            for (int j=0; j<cols;j++)  // for every row saves the position of edge for both mats
            {

                if ((i< src1.rows())&& (j< src1.cols())){
                    data1 = src1.get(i, j);


                    if (data1[0]==0.0D && stop1<1){
                        stop1=1;
                        morph [i][0] =j;

                    }}
                if ((i< src2.rows())&& (j< src2.cols())) {

                    data2 = src2.get(i, j);

                    if (data2[0] == 0.0D && stop2 < 1) {
                        stop2 = 1;
                        morph[i][1] = j;

                    }

                }
                if ((stop1>0)&&(stop2>0)) // if both found the edge for the same line then stop the search of the line
                {break;}
            }
        }
        double [][] movements =movements(morph,12); //compression of the morphology to 12 pieces

        return movements;// returning the compressed morphology
    }
// ελέγχει αν υπάρχει συμμετρία
    /*
    check for asymmetry
    inputs movements of mole sides and image of mole
    outputs true if there is asymmetry
     */
    public static boolean dangerousedges (double [][] morfology ,Mat src )
    {


        TAG = "10% error  is";
        double error=src.cols()*0.18; // the difference between two sides of mole can be up to 18%
        Log.d(TAG, 	 Double.toString(error));




 //       TAG = "morfology  is";
        int times =0;
        for (int i=0;i<morfology.length;i++) {
         //   Log.d(TAG, Double.toString(morfology[i][0])+"+"+morfology[i][1]);
            if (Math.abs(morfology[i][0]-morfology[i][1])>=(error)) // if the compare def is bigger than the error margin
            {times=times+1;// counts how many times this happens

            }
        }



        if (times>3) // if it happens 4 times (we have 12 pieces of each side .3 times error meaning that  1/3 of those pieces have different morphology -> the 1/3 of sides is different )
        {
            return true;

        }
        return false;

    }



// συμπιέζει τον πίνακα μορφολογίας στον αριθμό pieces
/*compresses the morphology table to the number of pieces
 *
 * inputs : morphology and pieces to cut the mophology
  * outputs the compressed morphology
 */
    public static double[][] compress( double[][] morph,int pieces)
//	public void morfology(Mat src1,Mat src2)


    {
        int step1 = morph.length / pieces;//calculates steps
        int step2 = morph.length / pieces;

        double limitedmorf[][] = new double[pieces][2];


        for (int i = 0; i < pieces; i++) {
            int avg = 0;
            int avg2 = 0;
            for (int j = i * step1; j < i * step1 + step1; j++) // for every piece
                avg = avg + (int) morph[j][0];// sums the morphology
            limitedmorf[i][0] = avg / step1;// calculate the avgs for right edge

            for (int j = i * step2; j < i * step2 + step2; j++)
                avg2 = avg2 + (int) morph[j][1];// sums the morphology
            limitedmorf[i][1] = avg2 / step2;// calculate the avgs for left edge
        }


        return limitedmorf;
    }
// εντοπίζει τις <κινήσεις> των πλευρών της ελίάς
    /*
    identifies the <moves> of the sides of the matrix
    calculates the avg of the morph for every piece
    and from every value delete its previous one
    inputs the morphology   and pieces
    and output movements
     */
    public static double[][] movements( double[][] morph,int pieces)
//	public void morfology(Mat src1,Mat src2)


    {
        int step1 = morph.length/pieces;//calculates steps
        int step2 = morph.length/pieces;

        double limitedmorf [][] = new double[pieces][2] ;//creating array of doubles having in the avg of every piece of morph
        double movements [][] = new double[pieces][2] ;// the returning array


        for (int i=0;i<pieces;i++)// for every piece calc the avg
        {
            int avg =0;
            int avg2=0;
            for (int j =i*step1; j<i*step1+ step1 ;j++)// for right
                avg=avg+(int)morph [j][0];
            limitedmorf[i][0]= avg/step1;

            for (int j =i*step2; j<i*step2+ step2 ;j++)// for left
                avg2=avg2+(int)morph [j][1];
            limitedmorf[i][1]= avg2/step2;
        }

        movements[0][0]=0;
        movements[0][1]=0;
        for (int i=1;i< pieces ; i++)
        {
            movements[i][0]=limitedmorf[i][0]-limitedmorf[i-1][0];// for every value removes the prev in line
            movements[i][1]=limitedmorf[i][1]-limitedmorf[i-1][1];



        }

        return movements;
    }
// ελέγχει την περίμετρο της ελιάς
    /*
        checks the perimeter of the mole->
        checking if the mole has curves on perimeter using opencv functions
        input:
        list of countours
        countour possition
        and the image
        Output:
       true  if there is perimeter problem
       false if not


     */
    public static boolean convexity (List<MatOfPoint> contours , int pos, int src){

        MatOfInt convexHullMatOfInt = new MatOfInt();//contain indices of the contour points
        ArrayList<Point> convexHullPointArrayList = new ArrayList<Point>();
        MatOfPoint convexHullMatOfPoint = new MatOfPoint();
        ArrayList<MatOfPoint> convexHullMatOfPointArrayList = new ArrayList<MatOfPoint>();
        MatOfInt4 mConvexityDefectsMatOfInt4 = new MatOfInt4();// stores the list of defects

        Imgproc.convexHull( contours.get(pos), convexHullMatOfInt, false );
        //Convex hull obtained using convexHull() that should contain indices of the contour points that make the hull.

        for(int j=0; j < convexHullMatOfInt.toList().size(); j++)//adding contour to hull
            convexHullPointArrayList.add(contours.get(pos).toList().get(convexHullMatOfInt.toList().get(j)));

        convexHullMatOfPoint.fromList(convexHullPointArrayList);
        convexHullMatOfPointArrayList.add(convexHullMatOfPoint);

        if( convexHullMatOfInt.rows() > 0)
            Imgproc.convexityDefects(contours.get(pos), convexHullMatOfInt,   mConvexityDefectsMatOfInt4);
        List<Integer> ConvexityList = mConvexityDefectsMatOfInt4.toList();
        Point data[] = contours.get(pos).toArray();

        for (int i=0; i<ConvexityList.size();i=i+4){
            //	depth =ConvexityList.get(i);
            //		Point start = data[ConvexityList.get(i)];
            //		Point end = data[ConvexityList.get(i+1)];
            //		Point defect = data[ConvexityList.get(i+2)];
            //Point  depth = data[ConvexityList.get(i+3)];
            double  depth = ConvexityList.get(i+3)/256 ; // the farthest from the convex hull point within the defect

            // get the floating-point value of the depth will be fixpt_depth/256.0.

            //	TAG ="depth";			Log.d(TAG,Double.toString(depth)  );


            double dis=src *0.10;
          //  if (src)

// checks if there is big defect on perimeter of the mole
            if (src<100) {//if the mole is to small then the defect must be at least 40% of size of it

                if (depth > src * 0.40) {

                    return true;
                }
            }else
            if (depth> src *0.11){//if the mole is to big then the defect must be at least 11% of size of it

                return true;
            }
        }
        //	TAG ="max permited";


        //	Log.d(TAG,Double.toString( src *0.25));


        return false;
    }
    //ελενχος πολυχρομίας του ιστογράμματοσ
    /*
    this function checks if h or s or v has 2 curves in historam
    input histogram mat and mode (mode can be h or s or v )
    and output % of second color
    The app does not use this function for the time.

     */
    public static double MultiColorCheck (Mat hist, char mode){
          String TAG;
        int pos=-1;// init position
        double data []= hist.get(0, 0); // init data
        int found ; // is how many times the function meets a line
        switch (mode){
            case 'h':

                for (int i=0;i<hist.rows();i++){
                    found=0;
                    for (int j=0;j<hist.cols();j++){
                        data=hist.get(i, j);

                        if (data[0]==255)
                        {
                            found++;
                            if (found>2){// if the function finds the line more than 3 times then save the pos and exit
                                pos=i;
                                break;
                            }
                            if (pos>0)
                                break;
                        }
                    }if (pos>0)
                        break;

                }


                break;

            case 's':


                for (int i=0;i<hist.rows();i++) {
                    found = 0;

                    for (int j = 0; j < hist.cols(); j++) {
                        data = hist.get(i, j);


                        TAG = "data 0";
                        //		Log.d(TAG, 	 Double.toString(data[0]));
                        TAG = "data 1";
                        //		Log.d(TAG, 	 Double.toString(data[1]));
                        TAG = "data 2";
                        //		Log.d(TAG, 	 Double.toString(data[2]));

                        if (data[1] == 255) {


                            found++;
                            TAG = "i";
                            Log.d(TAG, Double.toString(i));
                            TAG = "data 1";
                            Log.d(TAG, Double.toString(data[1]));

                            if (found > 2) {// if the function finds the line more than 3 times then save the pos and exit


                                pos = i;

                                break;
                            }


                        }
                    }
                    if (pos > 0)
                        break;


                }
                break;


            case 'v':

                for (int i=0;i<hist.rows();i++){
                    found=0;

                    for (int j=0;j<hist.cols();j++){
                        data=hist.get(i , j);

                        if (data[2]==255)
                        {
                            found++;
                            if (found>2){// if the function finds the line more than 3 times then save the pos and exit
                                TAG = "data 0";
                                Log.d(TAG, 	 Double.toString(i));
                                pos=i;
                                break;
                            }

                        }

                    }if (pos>0)
                        break;}



                break;
        }


// 		 double status =pos/hist.rows()*100;
        double div=1.0/hist.rows(); // calculates % of second color using the pos that second color is found
                double status =(hist.rows()-pos)*div;

        TAG = "color distortion %";
        Log.d(TAG, 	 Double.toString(status));



        return status;
    }


    //ελενχος πολυχρομίας του ιστογράμματος σε hs
    //checking the multicolor by using a histogram
    // input hsv image -> output the detected curves count for h and s
    public static double HistogramCheck (Mat hsvSource )
    {
         List<Mat> images = new ArrayList<Mat>();
        Core.split(hsvSource, images);//Divides a multi-channel array into several single-channel arrays.

        // sets the bins 60 for h and  64 for S and V


        int bin =60;
        int bin2=64;

        MatOfInt histSize1 = new MatOfInt(bin);
        MatOfInt histSize2 = new MatOfInt(bin2);
        MatOfInt channels = new MatOfInt(0);
        // sets the ranges  180 for h because it gets values from 0 to 180

        MatOfFloat histRange1 = new MatOfFloat(1, 175);

        MatOfFloat histRange2 = new MatOfFloat(10, 256);
        // sets the ranges  for SV ->256  because it gets values from 0 to 255
        // skips the first 10 values because  the 0.0.255 in hsv is white!!
        // but the device flash creates a lot of whites and those whites must be skipped
        Mat hist_h = new Mat();
        Mat hist_s = new Mat();
        // H component

        Imgproc.calcHist(images.subList(0, 1),
                channels,
                new Mat(),
                hist_h,
                histSize1,
                histRange1,
                false);
        // S component

        Imgproc.calcHist(images.subList(1, 2),
                channels,
                new Mat(),
                hist_s,
                histSize2,
                histRange2,
                false);

        int hiw = 500; // width of the histogram image
        int hih = 400; // height of the histogram image

        Mat histImage = new Mat(hih, hiw, CvType.CV_8UC3, new Scalar(0, 0, 0));
        // normalize the result to [0, histImage.rows()]

        Core.normalize(hist_h, hist_h, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_s, hist_s, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());





double max=0;
        for (int i=0;i<histSize2.get(0, 0)[0]; i++)
            if (max<hist_s.get(i,0)[0])
                max=hist_s.get(i,0)[0]; // finds max value


       double fullsize= max*0.25;// ignores the values  smaller than max*0.25 and makes them 0
        Imgproc.threshold(hist_s,hist_s,fullsize,0,Imgproc.THRESH_TOZERO		);

        for (int i=0;i<histSize1.get(0, 0)[0]; i++)
            if (max<hist_h.get(i,0)[0])
                max=hist_h.get(i,0)[0];// finds max value

          fullsize= max*0.25;// ignores the values  smaller than max*0.25 and makes them 0

        Imgproc.threshold(hist_h,hist_h,fullsize,0,Imgproc.THRESH_TOZERO		);






        TAG = "threshold ";
    Log.d(TAG,Double.toString(fullsize));


        TAG = "color distortion %";

        double [][] matrix = new double [bin2][2];//the data from histogram
        double [][] movements = new double [bin2][2];//the movements of lines
   //     double [][] compress = new double [bin2][2];//compressed matrix
        for (int i = 1; i < histSize2.get(0, 0)[0]; i++)
        {
            // h component or gray image
            if (i<bin)
                matrix[i][0]=hist_h.get(i,0)[0];//filling matrix with data
                else
                matrix[i][0]=hist_h.get(bin-1,0)[0];// h hist has range up to 180  .. the others till 255 will fill with 0

            matrix[i][1]=hist_s.get(i,0)[0];


        }

      // int pieces=Math.abs(bin2/2);
        int pieces=bin2;
        movements=movements(matrix,pieces);//find the line movements
     //   compress=compress(matrix,pieces);





        int final0 = curves( movements ,matrix,hsvSource.rows(),0);// checks the number of curves
        int final1 = curves( movements ,matrix,hsvSource.rows(),1);



        return   final0+final1;  //for one color curves must be 2

    }


    /*
    //η συνάρτηση έλενχει τον πίνακα με τις κινήσεις της γραμμής του ιστογράμματος αν έχει παραπάνω απο
μια καμπύλη. γίνεται έλενχος στα ιστογράμματα για h και s tou hsv.
the function checks the table with the histogram line movements. it checks if it has more than one
curve.

inputs :  movements array , the array of histogram values  , hsvSource is the size of mole ,  channel
 (0 for hue and 1 for saturation )

 outputs the number of curves found
     */
    public static int curves (double movements[][], double matrix[][] ,int hsvSource ,int channel)
    {
        double u=0;//u is the sum for up movements
        double d=0;//d is the sum for down movements


        int before=-2; //  -2 means that  the line was going down  before / 2 that  the line was going up before
        int after =0;//  -2 means that  the line will go down after / 2 that  the line will go up after
        int temp=-2; // temp value for checking if the previews position the movements of the line was up or down

        TAG = "color distortion %";


        double [] result = new double [movements.length];  //when the line stops  going for multiple steps up / down
        // the result saves what kind of movement was (up or down)
        double [] poss= new double [movements.length] ; // keeps the value of the hist position when line stops  going for multiple steps up / down
        double [] high= new double [movements.length] ;//when the line stops  going for multiple steps up / down
        // the result saves how far the line gone up or down



        // keeps the value of how  high value had the line when  stops  going for multiple steps up / down

        int possition=0;// init the number of step counter

        for (int i = 1; i < movements.length-1; i++) {
            Log.d(TAG, Double.toString(movements[i][channel]));

            //    Log.d(TAG, Double.toString(matrix[i][1]));
            if (movements[i+1][channel]<0)//checking the movement of next step if negative -2 otherwise +2
                after=-2;
            else if (movements[i+1][channel]>=0)
                after=2;
            //TAG = "color after";
            // Log.d(TAG,Integer.toString(after));
            if (movements[i][channel]>0.0){//checking the movement of this step is it is possitive

                u=u+movements[i][channel];// saving how far was the movement to u
                //       if (d<hih*0.10)
                if (u<15)  // if u is not big enough
                {//d=0;
                    //  before=0;

                    if(d>=u) // maybe the going up of the line was temporary   set d= d-u and check again next round
                        d=d-u;
                    else        //else the line seems to keep going up so  zero the d
                        d=0;
                }
                else  if ((before<1 )&& (temp +1 !=i)) { // if the move of line was big positive one then




                    Log.d(TAG,"ok"); // there is start of curve
                    poss[possition] = i; //saves the position of start the Uphill

                    before=2; // say to nexr round that this round the line goes up
                    temp=i; // //saves the position of start the Uphill
                    high[possition]=d; // if there was down before then it saves it
                    result[possition]=before; // saves the result
                    possition=possition+1; //up the counter
                    d=0; // zero the downhill meter
                }

                else if (u>20) // if the jump is big enough but  temp had the previous i
                // or before was possitive  then just zero the downhill meter
                // and we will see the next round if the line stops going up

                    d=0;



            }else {
                if (movements[i][channel] <= 0.0) { // if the movemnent is negative

                    d = d + Math.abs(movements[i][channel]);// saving how far was the movement to d
                    //              if (u < hih * 0.10){
                    if ((d < 15)&&(movements[i+1][channel]!=0)){ // if d u is not big enough
                        // and the next move is 0

                        if(u>=d)// maybe the going down of the line was temporary   set u= u-d and check again next round
                            u=u-d;
                        else
                            u=0;//else the line seems to keep going down so  zero the u
                        //         before=0;

                        //   u = 0;

                    }
                    else   if ((before > 1) &&(after>1)&&(temp+1!=i)&&(matrix[i][channel]<150)) {
                        // if before the line gone up
                        //after will  go up or 0
                        // temp is not the previous i
                        // and the value of the matrix is height enough

                        TAG = "color distortion %";
                        Log.d(TAG,"ok2");
                        poss[possition] = i;//saves the position of start the downhill
                        temp=i;// //saves the position of start the downhill
                        before = -2;// say to nexr round that this round the line goes down
                        result[possition]=before;// saves the result

                        high[possition] = u;// if there was up before then it saves it

                        possition = possition + 1;//up the counter

                        u = 0;// zero the uphill meter
                    }
                    else if (d>20)
                    {
                        u=0;// if the jump is big enough but  temp had the previous i
                        // or before was negative  or the value of the matrix is  not height enough
                        // or after  will be -2 again (down again )
                        // then just zero the uphill meter
                        // and we will see the next round if the line stops going down

                    }
                }

            }
        }

        double distance=0; //for every curve calculates the distance
        for (int i=1;i<possition;i++)
        {

            if (result[i-1]>1){

                distance=distance+poss[i]-poss[i-1];


                TAG = "distance";
                Log.d(TAG, 	 Double.toString(distance));
            }


        }

        //  if (possition2>possition)
        //     return possition2;


        if (distance>(movements.length-1)/2.3) // if the distance is long enough (bigger than the half hist)
            possition=possition+100; //then there is multi colors


        possition=(possition+1)/2; // curvers (because every 2 possitions is a curve(one uphill & one downhill) )


        if(hsvSource  < 50) // if the mole has very small size , is not save to say if there is multi color
            possition=0;
        return possition;





    }



    // ελέγχει αν υπάρχει συμμετρία
    /*
    check for asymmetry
    inputs movements of mole sides and image of mole
    outputs true if there is asymmetry
     */
    public static boolean dangerousedges (double [][] morfology,double [][] morfologyplus ,int  src )
    {


        TAG = "10% error  is";
        double error=src *0.09; // the difference between two sides of mole can be up to 18%
        Log.d(TAG, 	 Double.toString(error));




        //       TAG = "morfology  is";
        int times =0;
        for (int i=0;i<morfology.length;i++) {
            //   Log.d(TAG, Double.toString(morfology[i][0])+"+"+morfology[i][1]);
            if (Math.abs(morfology[i][0]-morfologyplus[i][0])>=(error)) // if the compare def is bigger than the error margin
            {times=times+1;// counts how many times this happens

            }
        }
        if (times<=3) {
            times = 0;
            for (int i = 0; i < morfology.length; i++) {
                //   Log.d(TAG, Double.toString(morfology[i][0])+"+"+morfology[i][1]);
                if (Math.abs(morfology[i][1] - morfologyplus[i][1]) >= (error)) // if the compare def is bigger than the error margin
                {
                    times = times + 1;// counts how many times this happens

                }

            }
        }



        if (times>3) // if it happens 4 times (we have 12 pieces of each side .3 times error meaning that  1/3 of those pieces have different morphology -> the 1/3 of sides is different )
        {Log.d("evolving",Integer.toString(times));
            return true;

        }
        return false;

    }


}
