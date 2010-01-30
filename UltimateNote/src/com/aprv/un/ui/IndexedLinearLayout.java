package com.aprv.un.ui;

import com.aprv.un.Settings;
import com.aprv.un.model.Media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class IndexedLinearLayout extends LinearLayout implements IndexedItem {
	protected int index;
	protected Media media;
	private boolean selected;
		
	public IndexedLinearLayout(int index, Context context, Media media) {
		super(context);
		this.index = index;		
		this.media = media;
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {			
			requestFocus();
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {		
		if (focused) {
			NoteEditor.setCurrentPos(index);
			Log.i(Settings.TAG, "img " + NoteEditor.getCurrentPos());
			this.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
		}
		else {
			setBackgroundColor(Color.TRANSPARENT);
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
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
