package com.aprv.un.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.aprv.un.Settings;
import com.aprv.un.model.Media;

public class NoteItemsAdapter extends BaseAdapter{
	private List<Media> mMedia;	
	private Context mContext;
	private int selectedPosition;
	private NoteEditor2 editor; //to change current position from here
	
	public NoteItemsAdapter(Context context, List<Media> media, NoteEditor2 editor) {
		super();
		mContext = context;
		this.mMedia = media;
		if (mMedia == null) 
			mMedia = new ArrayList<Media>();
		this.editor = editor;		
	}

	@Override
	public int getCount() {
		return mMedia.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mMedia.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		//Log.i(Settings.TAG, "getItemId: " + arg0);
		Media ret = null;
		return mMedia.get(arg0).getId();		
	}
	
	
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {		
		// Setup
		Log.i(Settings.TAG, "Get view called " + arg0);
		Media media = (Media)getItem(arg0);	
		View view = null;
		final int pos = arg0;
		if (media != null) {
			String type = media.getType();
			if (type.equals(Media.TYPE_TEXT)) { //Text								
				EditText text = new MediaTextEdit(mContext, media);
				text.setText(media.getSource());				
				view = text;				
			} else if (type.equals(Media.TYPE_IMAGE)) { //Image
				ImageView image = new ImageView(mContext);
				//TODO - Try to reuse
				Bitmap bm = BitmapFactory.decodeFile(media.getSource());
				
				/*
				if (convertView != null) {
					try {
						image = (ImageView) convertView;
						
					} catch (ClassCastException e) {
						Log.i(Settings.TAG, "Adapter: view " + arg0 + " is not an ImageView. Creating new one");
						image = new ImageView(mContext);	
						//image.setLayoutParams(params);
					}
				} else {
					image = new ImageView(mContext);
				}
				*/
				
				image.setImageBitmap(bm);
				//image.setFocusable(true);
				//image.setClickable(true);
				view = image;
			}
		}
		
		//This code below is to work around with selection in touch mode - very ugly...
		if (view!=null) {
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					editor.setCurrentPosition(pos);
					v.requestFocus();
					Log.i(Settings.TAG, "Clicked on: " + editor.getCurrentPosition());										
				}
			});						
		}		
		return view;
	}
	
	public int getSelectedPosition() {
		return selectedPosition;
	}
	
}
