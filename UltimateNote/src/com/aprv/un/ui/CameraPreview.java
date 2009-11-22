/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aprv.un.ui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aprv.un.Settings;
import com.aprv.un.helper.SaveFileHelper;

// ----------------------------------------------------------------------

public class CameraPreview extends Activity {    
    private Preview mPreview;    
    private Button mCaptureButton;
    private Button mCancelButton;
    
    private String savedMediaLocation;
    private static String saveFile;
    private static Bitmap bitmap;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
            
        //
        setContentView(R.layout.photo_capture);
        // Create our Preview view and set it as the content of our activity.
        saveFile = setupTargetFile();
        mPreview = new Preview(saveFile, this);                
        
        LinearLayout layout = (LinearLayout)findViewById(R.id.camera_linear_layout);
        layout.addView(mPreview);
        
        //Pass settings from bundle
        Intent intent = getIntent();
        if (intent != null) {
        	mPreview.setMediaLocation(intent.getStringExtra(Settings.KEY_SAVED_MEDIA_LOCATION));
        }                        
        
        mCaptureButton = (Button)findViewById(R.id.captureButton);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPreview.captureImage();
				Intent i = new Intent();
				Bundle data = new Bundle();
				data.putString(Settings.KEY_PATH, saveFile);
				i.putExtras(data);
				setResult(RESULT_OK, i);
				
				Log.i(Settings.TAG, "Home sweet home! Saved to " + saveFile);
				
				finish();
			}
		});
        
        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				setResult(RESULT_CANCELED);				
				Log.i(Settings.TAG, "Home sweet home! Saved to " + saveFile);				
				finish();
			}
		});      
        
        /* TODO - Allow trackball click
        mPreview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPreview.captureImage();
				Intent i = new Intent();
				Bundle data = new Bundle();
				data.putString(Settings.KEY_PATH, saveFile);
				i.putExtras(data);
				setResult(RESULT_OK, i);
				
				Log.i(Settings.TAG, "Home sweet home! Saved to " + saveFile);
				
				finish();
			}
		});        
        mPreview.requestFocus();                       
        */
        Toast.makeText(this, "Touch Capture or press trackball to capture image", 10).show();        
    }

    public static void setSaveFileName(String fileName) {
    	saveFile = fileName;    
    }
    
    public static String getSaveFileName() {
    	return saveFile;
    }       
    
    private String setupTargetFile() {
    	//File dir = new File(NoteList.getImageDir());
		if (savedMediaLocation == null) {
			Log.e(Settings.TAG, "Camera Preview savedMediaLocation == null");
			savedMediaLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UltimateNote/media";
		}
		File dir = new File(savedMediaLocation);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String path = SaveFileHelper.generateName(savedMediaLocation, "/", "IMG_" + SaveFileHelper.generateDateString(), "JPG");
		return path;
    }
}

// ----------------------------------------------------------------------

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;   
    PictureCallback mCallbackRaw;
    PictureCallback mCallbackJpeg;
    
    String savedMediaLocation;
    String targetFile;
    
    Preview(String targetFile, Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.targetFile = targetFile;
    }
    
    public void setMediaLocation(String location) {
    	this.savedMediaLocation = location;
    }
        
    
    public void setupCallback() {    	    	
    	mCallbackJpeg = new PictureCallback() {
    		public void onPictureTaken(byte[] data, Camera camera) {
    			//TODO: Pass by bundle to save on memory and calculations
    			Log.e(Settings.TAG,"callback JPEG is called.");
    			    	   
    	    	try {    	    		    	    		    	    		    	    		    	    	
    	    		File outputFile = new File(targetFile);    	    	    	    	
	    	    	if (!outputFile.exists()) {	    	    		
	    	    		outputFile.createNewFile();	    	    		
	    	    	}
	    	    	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    	    		FileOutputStream outputStream = new FileOutputStream(outputFile);    	    		
    	    		bitmap.compress(CompressFormat.JPEG, 100, outputStream);
    	    		outputStream.flush();
    	    		outputStream.close();
    	    		
    	    		Log.i(Settings.TAG,"Saved photo to " + targetFile);
	    	    	
    	    	} catch (IOException e) {
    	    		Log.e("io","Exception: " + e);
    	    	}    	    	    	    	    	    	    	  
    		}
    	};
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();
        
        
        Camera.Parameters params = mCamera.getParameters();        
		params.setPictureSize(240, 320);
    	params.setPictureFormat(PixelFormat.JPEG);
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
    	Log.e(Settings.TAG,"Capture Image!");
    	
    	if (mCamera == null) {
    		Log.e(Settings.TAG,"camera NULL");
    	}
    	try {
	    	mCamera.stopPreview();
	    	mCamera.takePicture(null, null, mCallbackJpeg);
	    	mCamera.startPreview();
    	} catch (Exception e) {
    		cleanUp();    		
    	}
    }    
    
    public void cleanUp() {
    	mCamera.stopPreview();
    	mCamera.release();
    	mCamera = null;
    }
}
