package com.aprv.un.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

public class UltimateNote extends Activity {		
	private static final int ADD_NOTE = Menu.FIRST;
	private static final int ACTIVITY_ADD_NOTE = 0;	
	private static String workingDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UltimateNote";
	private static String imageDir = workingDir + "/Images";
	private static int photoNum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);		
		// This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, ADD_NOTE, 0, R.string.menu_add_note);                
		
		Intent intent = new Intent(null, getIntent().getData());        
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NoteEditor.class), null, intent, 0, null);
		
		return true;
	}
	
	/*
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {        
        case ADD_NOTE:
            Intent i = new Intent(this, NoteEditor.class);
            startActivityForResult(i, ACTIVITY_ADD_NOTE);
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    */	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case ADD_NOTE:
            // Launch activity to insert a new item
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public static String getWorkingDir() {
		return workingDir;
	}
	
	public static String getImageDir() {
		return imageDir;
	}
	
	public static String getNextPhotoName() {
		return "Image" + (photoNum + 1) + ".jpg";
	}
	
	public static void incrementPhotoNum() {
		photoNum++;
	}
}