package com.aprv.un.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.aprv.un.ImageItem;
import com.aprv.un.NoteItem;
import com.aprv.un.Settings;

public class IndexedImageView extends ImageView implements IndexedItem {
	private int index;
	private ImageItem imageItem;
	
	/**
	 * 
	 * @param index This view's index in parent layout
	 * @param context
	 */
	public IndexedImageView(int index, Context context) {
		super(context);
		this.index = index;
		this.setFocusable(true);
	}	

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		Log.i("vinh","edit text focused = " + focused);
		if (focused) {
			NoteEditor.setCurId(index);
			
		}
		else {
			
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}		
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
		NoteEditor.setCurId(index);
		return super.onTouchEvent(event);
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int idx) {
		this.index = idx;
	}
	
	public NoteItem getNoteItem() {
		return imageItem;
	}
	
	public void setNoteItem(NoteItem noteItem) {
		try {
			this.imageItem = (ImageItem) noteItem;
		} catch (Exception e) {
			Log.e(Settings.TAG, "Must set an ImageItem");
		}
	}
}
