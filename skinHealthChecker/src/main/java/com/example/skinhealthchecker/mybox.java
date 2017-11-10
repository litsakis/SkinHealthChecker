package com.example.skinhealthchecker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
/*
Είναι μια οντότητα γραφικών . Τα γραφικά που σχεδιάζονται είναι ο στόχος που εμφανίζεται για να γίνει η στόχευση σωστά η ελιά κατά την λήψη της φωτογραφίας .

 It is a graphic entity. The graphics that are designed has goal to  make
  the mole targeted correctly when taking the photo.

 */
public class mybox extends View {

	private Paint paint = new Paint();

	mybox(Context context) {//A Context object is your gateway to much of the underlying Android system
		super(context);
		// TODO Auto-generated constructor stub

	}

	@Override
	protected void onDraw(Canvas canvas) { // Override the onDraw() Method
		super.onDraw(canvas);

		paint.setStyle(Paint.Style.STROKE);// style of paint is stroke
		paint.setColor(Color.GREEN);// color  is green
		paint.setStrokeWidth(10); // the width of line  is 10

		// init the vars
		int x0  =-1;

		int y0 =-1;
		int dx  =-1;
		int dy  =-1;
		if (canvas.getHeight()>canvas.getWidth()){// calculating the two points
			// in case the phone is in portrait mode
			  x0 = canvas.getWidth() / 2;
			  y0 = canvas.getHeight() / 2;
			  dx = canvas.getHeight() / 15;
			  dy = canvas.getHeight() / 15;


		}else
		{
			// in case the phone is in landscape mode

			  x0 = canvas.getWidth() / 2;
			  y0 = canvas.getHeight() / 2;
			  dx = canvas.getHeight() / 8;
			  dy = canvas.getHeight() / 8;

		}

		// draw guide box
		canvas.drawRect(x0 - dx, y0 - dy, x0 + dx, y0 + dy, paint);


		// draw a min rect in the center like a dot
		canvas.drawRect(canvas.getWidth() / 2-canvas.getWidth() / 200, canvas.getHeight() / 2-canvas.getHeight() / 200, canvas.getWidth() / 2+canvas.getWidth() / 200, canvas.getHeight() / 2+canvas.getHeight() / 200, paint);

	}

}
