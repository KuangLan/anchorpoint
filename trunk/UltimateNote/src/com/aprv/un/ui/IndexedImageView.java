package com.aprv.un.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.aprv.un.Settings;
import com.aprv.un.model.Media;

public class IndexedImageView extends ImageView implements IndexedItem {
	protected int index;
	protected Media media;
	private boolean selected;
	private Bitmap bitmap;
	int count = 0;
	
	/**
	 * To enable inheritance for IndexedAudioItem
	 * @param context
	 */
	public IndexedImageView(Context context) {	
		super(context);
	}
	/**
	 * 
	 * @param index This view's index in parent layout
	 * @param context
	 */
	public IndexedImageView(int index, Context context, Media media) {
		super(context);
		this.index = index;
		this.media = media;
		this.setFocusable(true);
		this.setPadding(2,2,2,2);		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(params);		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		//this.setAdjustViewBounds(true);
		//this.setMaxHeight(320);
		//BitmapFactory.Options options=new BitmapFactory.Options(); 
		//options.inSampleSize = 8; 		
		//bitmap = BitmapFactory.decodeFile(media.getSource(), options);
		bitmap = BitmapFactory.decodeFile(media.getSource());		
		setImageBitmap(bitmap);				
	}	

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {		
		if (focused) {
			NoteEditor.setCurrentPos(index);
			Log.i(Settings.TAG, "img " + NoteEditor.getCurrentPos());
			setBackgroundColor(Color.BLUE);
			//this.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
		}
		else {
			setBackgroundColor(Color.TRANSPARENT);
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}								
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {			
			requestFocus();
		}
		return super.onTouchEvent(event);
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int idx) {
		this.index = idx;
	}
	
	public Media getMedia() {
		return media;
	}
	
	public void setMedia(Media media) {
		try {
			this.media = media;
		} catch (Exception e) {
			Log.e(Settings.TAG, "Must set an ImageItem");
		}
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		setImageBitmap(bitmap);
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
}
