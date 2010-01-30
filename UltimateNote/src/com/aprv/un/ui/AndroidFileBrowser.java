package com.aprv.un.ui;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
//import android.net.ContentURI;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidFileBrowser extends ListActivity {
	
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List<String> directoryEntries = new ArrayList<String>();
	private File currentDirectory = new File("/");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		browseToRoot();
		//try {
			//Intent myIntent = new Intent("android.intent.action.VIEW", new ContentURI("geo:38.899533,-77.036476"));
			//startActivity(myIntent);
		//} catch (URISyntaxException e) { } 
	}
	
	/**
	 * This function browses to the 
	 * root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/"));
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
		if (aDirectory.isDirectory()){
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		}else{
			OnClickListener okButtonListener = new OnClickListener(){
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
						// Lets start an intent to View the file, that was clicked...
						openFile(aDirectory);
				}
			};
			OnClickListener cancelButtonListener = new OnClickListener(){
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Do nothing
				}
			};
			/*
			AlertDialog.show(this,"Question", "Do you want to open that file?\n" 
								+ aDirectory.getName(),
								"OK", okButtonListener,
								"Cancel", cancelButtonListener, false, null);
			 */
		}
	}
	
	private void openFile(File aFile){
		/*
		try {
			
			Intent myIntent = new Intent(android.content.Intent.VIEW_ACTION, 
				new ContentURI("file://" + aFile.getAbsolutePath()));
			startActivity(myIntent);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		*/
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();
		
		// Add the "." == "current directory"
		// and the ".." == 'Up one level'
		
		this.directoryEntries.add(getString(R.string.current_dir));		
		if(this.currentDirectory.getParent() != null)
			this.directoryEntries.add(getString(R.string.up_one_level));
		
		switch(this.displayMode){
			case ABSOLUTE:
				for (File file : files){
					this.directoryEntries.add(file.getPath());
				}
				break;
			case RELATIVE: // On relative Mode, we have to add the current-path to the beginning
				int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
				for (File file : files){
					this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght));
				}
				break;
		}
		
		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_row, this.directoryEntries);
		
		this.setListAdapter(directoryList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int selectionRowID = (int) id;
		String selectedFileString = this.directoryEntries.get(selectionRowID);
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
												+ this.directoryEntries.get(selectionRowID));
					break;
				case ABSOLUTE:
					clickedFile = new File(this.directoryEntries.get(selectionRowID));
					break;
			}
			if(clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
}