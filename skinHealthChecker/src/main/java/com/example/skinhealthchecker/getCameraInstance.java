package com.example.skinhealthchecker;

import android.hardware.Camera;
/*
Περιέχει την κλάση που λαμβάνει το στιγμιότυπο της επιλεγμένης  της κάμερας  .Χρησιμοποιείτε από το CameraActivity.

Contains the class that receives the instance  of the selected camera. Used by CameraActivity.

					*/
public class getCameraInstance {
	public static Camera getCameraInstance(int id) {// id is the number of the selected camera
		Camera c = null;
		try {
			c = Camera.open(id); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

}
