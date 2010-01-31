package com.aprv.un.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.aprv.un.Settings;
import com.aprv.un.model.Media;

public class FileBrowserDialog2 extends Dialog {
	
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }
	
    protected static final int SUB_ACTIVITY_REQUEST_CODE = 1337;

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private File currentDirectory = Environment.getExternalStorageDirectory().getAbsoluteFile();
	//private File currentDirectory = new File("/sdcard");
	private NoteEditor noteEditor;
	private Context mContext;
	private ListView mListView;	
	private Button mBtnCancel;

	/** Called when the activity is first created. */
	public FileBrowserDialog2(NoteEditor noteEditor) {
		super(noteEditor);
		this.noteEditor = noteEditor;
		
		setContentView(R.layout.file_browser);		
		mContext = getContext();
		mBtnCancel = (Button)findViewById(R.id.btnCancel);
		mListView = (ListView)findViewById(R.id.dirList);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog2.this.dismiss();
			}
		});		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView arg0, View view,
                    int position, long id) {						
				int selectionRowID = (int) id;
				String selectedFileString = directoryEntries.get(selectionRowID).getText();
				if (selectedFileString.equals(mContext.getString(R.string.current_dir))) {
					// Refresh
					browseTo(currentDirectory);
				} else if(selectedFileString.equals(mContext.getString(R.string.up_one_level))){
					upOneLevel();
				} else {
					File clickedFile = null;
					switch(displayMode){
						case RELATIVE:
							clickedFile = new File(currentDirectory.getAbsolutePath() 
														+ directoryEntries.get(selectionRowID).getText());
							break;
						case ABSOLUTE:
							clickedFile = new File(directoryEntries.get(selectionRowID).getText());
							break;
					}
					Log.i(Settings.TAG, "Clicked file: " + clickedFile.getAbsolutePath());
					if(clickedFile != null)
						browseTo(clickedFile);
				}
				
				Log.i(Settings.TAG, "Clicked " + currentDirectory.getAbsolutePath());
			}
		});				
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//setTheme(android.R.style.Theme_Black);
		browseToRoot();
		//this.setSelection(0);
	}
	
//	// ===========================================================
//	// Menu Creation & Reaction
//	// ===========================================================
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0,0, getString(R.string.menu_new_folder));
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onMenuItemSelected(int featureId, Item item) {
//		switch (item.getId()) {
//			case 0:
//				startSubActivity(new Intent(this, DirNameInputActivity.class), SUB_ACTIVITY_REQUEST_CODE);
//				return true;
//		}
//		return false;
//	}
//	
//	// ===========================================================
//	// Reacting on started subactivities that return
//	// ===========================================================
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, 
//								String data, Bundle extras) {
//		super.onActivityResult(requestCode, resultCode, data, extras);
//		// Here We identify the subActivity we starte 
//		if(requestCode == SUB_ACTIVITY_REQUEST_CODE){
//			// And show its result
//			try{
//				if(data.length() > 0){
//					String dirName = this.currentDirectory.getPath() + "/" + data + "/";
//					dirName = dirName.replace("//", "/");
//					File f  = new File(dirName);
//					boolean success = f.mkdir();
//					if(!success)
//						showAlert("Error", "Could not create the directory: " + data + "\nin: " + this.currentDirectory.getPath(), "Ok",false);
//				}
//			}catch (Exception e){
//				showAlert("Error", "Could not create the directory: " + data + "\nin: " + this.currentDirectory.getPath(), "Ok",false);
//			}
//		}
//	}

	/**
	 * This function browses to the 
	 * root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/sdcard"));
    }
	
	/**
	 * This function browses up one level 
	 * according to the field: currentDirectory
	 */
	private void upOneLevel(){
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	
	private void browseTo(final File aDirectory){
		try {
			// On relative we display the full path in the title.
			if(this.displayMode == DISPLAYMODE.RELATIVE)
				this.setTitle(aDirectory.getAbsolutePath() + " :: " + mContext.getString(R.string.app_name));
			if (aDirectory.isDirectory()){
				this.currentDirectory = aDirectory;
				fill(aDirectory.listFiles(new FileFilter() {
					public boolean accept(File file) {
			        	String type = Media.getMediaType(file.getName());
			        	return file.isDirectory() || type.equals(Media.TYPE_AUDIO) || type.equals(Media.TYPE_IMAGE);
			        }
				}));				
			}else{
				openFile(aDirectory);
				/*
				OnClickListener okButtonListener = new OnClickListener(){
					// @Override
					public void onClick(DialogInterface arg0, int arg1) {
							// Lets start an intent to View the file, that was clicked...
							//TODO
							openFile(aDirectory);
					}
				};
				OnClickListener cancelButtonListener = new OnClickListener(){
					// @Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Do nothing ^^
					}
				};
				*/
				// Show an Alert with the ButtonListeners we created
				/*AlertDialog.show(this,"Question", "Do you want to open that file?\n" 
									+ aDirectory.getName(),
									"OK", okButtonListener,
									"Cancel", cancelButtonListener, false, null);*/
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "Cannot access.", 3).show();
		}
	}
	
	private void openFile(File aFile){
		/*
		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, 
				Uri.parse("file://" + aFile.getAbsolutePath()));
			startActivity(myIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		noteEditor.addExternalMedia(aFile.getAbsolutePath());
		dismiss();
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();
		
		// Add the "." == "current directory"
		this.directoryEntries.add(new IconifiedText(
				mContext.getString(R.string.current_dir), 
				mContext.getResources().getDrawable(R.drawable.folder)));		
		// and the ".." == 'Up one level'
		if(this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(
					mContext.getString(R.string.up_one_level), 
					mContext.getResources().getDrawable(R.drawable.uponelevel)));
		
		Drawable currentIcon = null;
		for (File currentFile : files){
			if (currentFile.isDirectory()) {
				currentIcon = mContext.getResources().getDrawable(R.drawable.folder);
			}else{
				String fileName = currentFile.getName();
				/* Determine the Icon to be used, 
				 * depending on the FileEndings defined in:
				 * res/values/fileendings.xml. */
				if(checkEndsWithInStringArray(fileName, mContext.getResources().
								getStringArray(R.array.fileEndingImage))){
					currentIcon = mContext.getResources().getDrawable(R.drawable.image); 
				}else if(checkEndsWithInStringArray(fileName, mContext.getResources().
								getStringArray(R.array.fileEndingWebText))){
					currentIcon = mContext.getResources().getDrawable(R.drawable.webtext);
				}else if(checkEndsWithInStringArray(fileName, mContext.getResources().
								getStringArray(R.array.fileEndingPackage))){
					currentIcon = mContext.getResources().getDrawable(R.drawable.packed);
				}else if(checkEndsWithInStringArray(fileName, mContext.getResources().
								getStringArray(R.array.fileEndingAudio))){
					currentIcon = mContext.getResources().getDrawable(R.drawable.audio);
				}else{
					currentIcon = mContext.getResources().getDrawable(R.drawable.text);
				}				
			}
			switch (this.displayMode) {
				case ABSOLUTE:
					/* On absolute Mode, we show the full path */
					this.directoryEntries.add(new IconifiedText(currentFile
							.getPath(), currentIcon));
					break;
				case RELATIVE: 
					/* On relative Mode, we have to cut the
					 * current-path at the beginning */
					int currentPathStringLenght = this.currentDirectory.
													getAbsolutePath().length();
					this.directoryEntries.add(new IconifiedText(
							currentFile.getAbsolutePath().
							substring(currentPathStringLenght),
							currentIcon));

					break;
			}
		}
		Collections.sort(this.directoryEntries);
		
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(mContext);
		itla.setListItems(this.directoryEntries);		
		mListView.setAdapter(itla);
	}
	
	/*
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int selectionRowID = (int) this.getSelectedItemId();
		String selectedFileString = this.directoryEntries.get(selectionRowID).getText();
		if (selectedFileString.equals(getString(R.string.current_dir))) {
			// Refresh
			this.browseTo(this.currentDirectory);
		} else if(selectedFileString.equals(getString(R.string.up_one_level))){
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch(this.displayMode){
				case RELATIVE:
					clickedFile = new File(this.currentDirectory.getAbsolutePath() 
												+ this.directoryEntries.get(selectionRowID).getText());
					break;
				case ABSOLUTE:
					clickedFile = new File(this.directoryEntries.get(selectionRowID).getText());
					break;
			}
			if(clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
	*/
	
	/** Checks whether checkItsEnd ends with 
	 * one of the Strings from fileEndings */
	private boolean checkEndsWithInStringArray(String checkItsEnd, 
					String[] fileEndings){
		for(String aEnd : fileEndings){
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
}