package com.aprv.un.ui;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aprv.un.Settings;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;   
    PictureCallback mCallbackRaw;
    PictureCallback mCallbackJpeg;
        
    String targetFile;
    
    /*
    Preview(Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);        
    }*/
    
    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);        
    }
          
    public String getTargetFile() {
    	return targetFile;
    }
    
    public void setTargetFile(String targetFile) {
    	this.targetFile = targetFile;
    }
    
    public void setupCallback() {    	    	
    	mCallbackJpeg = new PictureCallback() {
    		public void onPictureTaken(byte[] data, Camera camera) {
    			//TODO: Pass by bundle to save on memory and calculations
    			Log.e(Settings.TAG,"callback JPEG is called.");
    			    	   
    	    	try {
    	    		Camera.Parameters params = mCamera.getParameters();        		    	    	        	
    	    		params.setPictureSize(320, 240);		
    	        	params.setPictureFormat(PixelFormat.JPEG);    	        	
    	        	mCamera.setParameters(params);
    	        	
	    	    	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);	    	    	
    	    		ImageUtil.saveBitmap(bitmap, CompressFormat.JPEG, targetFile);
    	    		
    	    		Log.i(Settings.TAG,"Saved photo to " + targetFile);
    	    	} catch (Exception e) {
    	    		Log.e(Settings.TAG,"onPictureTaken() Exception: " + e);
    	    		cleanUp();
    	    	}
    		}
    	};
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();
                
        Camera.Parameters params = mCamera.getParameters();        		    	
    	params.setPreviewFormat(PixelFormat.JPEG);
    	mCamera.setParameters(params);
    	
        setupCallback();
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
    	Log.i("vinh","Cam size: " + w + " - " + h);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
    
    public void captureImage() {
    	//mCamera.stopPreview();
    	try {
    		mCamera.takePicture(null, null, mCallbackJpeg);
    	} catch (Exception e) {
    		Log.e(Settings.TAG, "Preview.captureImage(): " + e);
    		cleanUp();
    	}
    	//mCamera.startPreview();
    	
    	/*
    	mCamera.autoFocus(new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Log.i(Settings.TAG,"Capture Image!");
		    	
		    	if (mCamera == null) {
		    		Log.e(Settings.TAG,"camera NULL");
		    	}
		    	try {		    		
		    		camera.stopPreview();
		        	camera.takePicture(null, null, mCallbackJpeg);
		        	camera.startPreview();		    				    		
		    	} catch (Exception e) {
		    		Log.e(Settings.TAG, "captureImage() " + targetFile + " exception: " + e);
		    		cleanUp();    		
		    	}
			}
		});
		*/    	    	
    }
        
    public void cleanUp() {
    	try {
	    	mCamera.stopPreview();
	    	mCamera.release();
	    	mCamera = null;
    	} catch (Exception e) {
    		Log.e(Settings.TAG, "cleanUp() exception: " + e);
    	}
    }
}