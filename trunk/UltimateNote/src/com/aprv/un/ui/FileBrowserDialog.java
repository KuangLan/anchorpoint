package com.aprv.un.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aprv.un.Settings;

public class FileBrowserDialog extends Dialog {
	private ListView lview;
	private static String curDir;
	private static List<String> pathTree;	//path tree - always start with root /
	private static String UP = "..";
	private static String ROOT = "/sdcard";
	private static TextView mCurDirLabel;
	private NoteEditor noteEditor;
	
	public FileBrowserDialog(NoteEditor noteEditor)  {
		super(noteEditor);
		this.noteEditor = noteEditor;		
		Context context = this.getContext();
		setContentView(R.layout.file_browser);
		/*
		Button closeButton = (Button)findViewById(R.id.Button01);
		closeButton.setOnClickListener(new ButtonListener(this));
				
		Button selectButton = (Button)findViewById(R.id.Button02);
		selectButton.setOnClickListener(new ButtonListener(this));
		*/
		lview = (ListView)findViewById(R.id.dirList);
		curDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		lview.setAdapter(new FileListAdapter(this.getContext(),R.layout.list_element,getDirectoryList(curDir)));		
		
		pathTree = new ArrayList<String>();
		extractPathTreeFromString(curDir, pathTree);
		
		mCurDirLabel = (TextView)findViewById(R.id.curDirLabel);
		mCurDirLabel.setText(curDir);
		
		final Context c = context.getApplicationContext();
		
		lview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView arg0, View view,
                                           int position, long id) {
				// user clicked a list item, make it "selected" and browse further
				String selected = arg0.getItemAtPosition(position).toString();
				if(selected.equalsIgnoreCase(ROOT)){						
					curDir = Environment.getExternalStorageDirectory().getAbsolutePath();
					extractPathTreeFromString(curDir, pathTree);
				} 
				else if (selected.equalsIgnoreCase(UP)) {
					//only if not in root directory
					if (pathTree.size() > 1) {	
						pathTree.remove(pathTree.size()-1);						
						curDir = getAbsolutePathFromTree(pathTree);
					}
				} else {
					pathTree.add(selected);
					curDir = getAbsolutePathFromTree(pathTree);
				}
				mCurDirLabel.setText(curDir);
				Log.i(Settings.TAG, "Selected: " + curDir);
				
				//Check if selected file is indeed a file
				File file = new File(curDir);
				if (file.isFile()) {
					selectionFinished();
				}
				else {
					lview.setAdapter(new FileListAdapter(c,R.layout.list_element,getDirectoryList(curDir)));
				}
			}
	    });
	}		
	
	private void selectionFinished() {
		noteEditor.addExternalMedia(curDir);
		this.dismiss();
	}
	
	private String getAbsolutePathFromTree(List<String> tree) {
		if (tree == null || tree.size() == 0) {
			return Environment.getRootDirectory().getAbsolutePath();
		}		
		StringBuffer path = new StringBuffer();
		for (int i=0; i<tree.size(); i++) {
			if (i>1) {
				path.append("/");
			}
			path.append(tree.get(i));			
		}		
		return path.toString();
	}
	
	private void extractPathTreeFromString(String path, List<String> tree) {
		StringTokenizer tokens = new StringTokenizer(path, "/");
		if (tree == null) {
			tree = new ArrayList<String>();
		} else {
			tree.clear();
		}
		
		tree.add("/"); //root
		while (tokens.hasMoreTokens()) {
			tree.add(tokens.nextToken());
		}
	}
	
	public static String getCurDir() {
		return curDir;
	}
	
	private List<String> getDirectoryList(String path){
		Log.i(Settings.TAG, "getDirectoryList: " + path);
		File f = new File(path);
		List<String> res = new ArrayList<String>();
		res.clear();
		res.add(ROOT);
		res.add(UP);
		if(f.exists() && f.isDirectory() && f.canRead()){
			FileFilter dirFilter = new FileFilter() {
		        public boolean accept(File file) {
		        	String name = file.getName().toLowerCase();
		            return file.isDirectory() || name.contains(".jpg") || name.contains(".png");		        	
		        }
		    };
			File[] dirlist = f.listFiles(dirFilter);
			
			for(File i : dirlist) {
				res.add(i.getName());				
			}
		}
		return res;
	}
	
	protected class ButtonListener implements View.OnClickListener {
		private Dialog dialog;
		public ButtonListener(Dialog d){
			dialog = d;
		}
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.dismiss();		
		}	
	}
	
	protected class CreateListener extends ButtonListener {
		private EditText newDir;
		public CreateListener(Dialog d,EditText e) {
			super(d);
			newDir =e;
			// TODO Auto-generated constructor stub
		}
	
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String temp = newDir.getText().toString();
			if(!temp.equals("")){
				File newFile = new File(curDir,temp);
				if(!newFile.exists()) {
					Boolean test=newFile.mkdir();
					System.out.println("test : "+test);
				}
			}
			//super.onClick(v);
		}
	}
	
	protected class FileListAdapter extends ArrayAdapter{				
		public FileListAdapter(Context context, int textViewResourceId,
				Object[] objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}
		
		public FileListAdapter(Context context, int textViewResourceId,
				List objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return super.getView(position, convertView, parent);						
		}				
	}
}
