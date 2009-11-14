package com.android.apps;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewPict extends Activity 
	implements OnItemClickListener,OnGesturePerformedListener{
    protected static final int CREATE_REQ = 1;
    private GestureLibrary mLibrary;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            finish();
        }
    }

    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
    	gestures.addOnGesturePerformedListener(this);
		GridView gridView = (GridView)findViewById(R.id.gridview);
	    //gridView.setColumnWidth(gridView.getWidth()/4);
	    gridView.setAdapter(new ImageAdapter(this));
	    gridView.setOnItemClickListener(this);
    }
    
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		// TODO Auto-generated method stub
		String imagename = getResources().getResourceName((Integer)parent.getItemAtPosition(pos));
		imagename = imagename.substring(imagename.indexOf('/')+1,imagename.length());
		Toast.makeText(getApplicationContext(),"pic "+imagename, Toast.LENGTH_SHORT).show();
	  	
		Intent i = new Intent(this, OneImageViewer.class);
		i.putExtra("image",(Integer)parent.getItemAtPosition(pos));
		i.putExtra("imagename",imagename);
    	startActivityForResult(i, CREATE_REQ);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
	    if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
	        String action = predictions.get(0).name;
	        Toast.makeText(this, "Event "+ action, Toast.LENGTH_SHORT).show();
	    }
	}
}