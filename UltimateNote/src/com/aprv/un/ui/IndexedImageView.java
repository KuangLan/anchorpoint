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
	private int index;
	private Media media;
	private boolean selected;
	int count = 0;
	
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
		this.setPadding(4, 2, 4, 2);		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(params);		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		
		Bitmap bm = BitmapFactory.decodeFile(media.getSource());
		setImageBitmap(bm);				
	}	

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {		
		if (focused) {
			NoteEditor.setCurrentPos(index);
			Log.i(Settings.TAG, "img " + NoteEditor.getCurrentPos());
			setBackgroundColor(Color.BLUE);
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
}
