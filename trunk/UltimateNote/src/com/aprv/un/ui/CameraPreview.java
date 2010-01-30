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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aprv.un.Settings;
import com.aprv.un.helper.SaveFileHelper;

// ----------------------------------------------------------------------

public class CameraPreview extends Activity {    
    private Preview mPreview;    
    private ImageButton mCaptureButton;
    private ImageButton mCancelButton;
    
    private String savedMediaLocation;
    private static String saveFile;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            
        //
        setContentView(R.layout.photo_capture);
        // Create our Preview view and set it as the content of our activity.        
        mPreview = (Preview)findViewById(R.id.cameraPreview);        
        
        //Pass settings from bundle
        Intent intent = getIntent();
        if (intent != null) {        	
        	String saveDir = intent.getStringExtra(Settings.KEY_SAVED_MEDIA_LOCATION);
        	saveFile = setupTargetFile(saveDir);
        	mPreview.setTargetFile(saveFile);
        }                        
        
        mCaptureButton = (ImageButton)findViewById(R.id.captureButton);
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
        
        mCancelButton = (ImageButton)findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				setResult(RESULT_CANCELED);				
				Log.i(Settings.TAG, "Home sweet home! Saved to " + saveFile);				
				finish();
			}
		});      
                
        Toast.makeText(this, "Touch Capture or press trackball", 10).show();        
    }

    public static void setSaveFileName(String fileName) {
    	saveFile = fileName;    
    }
    
    public static String getSaveFileName() {
    	return saveFile;
    }       
    
    private String setupTargetFile(String savedMediaLocation) {    	
		File dir = new File(savedMediaLocation);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String path = SaveFileHelper.generateName(savedMediaLocation, "/", "IMG_" + SaveFileHelper.generateDateString(), "JPG");
		return path;
    }

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mPreview.captureImage();
			Intent i = new Intent();
			Bundle data = new Bundle();
			data.putString(Settings.KEY_PATH, saveFile);
			i.putExtras(data);
			setResult(RESULT_OK, i);
			
			Log.i(Settings.TAG, "Home sweet home! Saved to " + saveFile);
			
			finish();
		}
		return super.onTrackballEvent(event);
	}       		
}

// ----------------------------------------------------------------------