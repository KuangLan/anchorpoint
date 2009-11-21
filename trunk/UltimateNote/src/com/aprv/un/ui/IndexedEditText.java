package com.aprv.un.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.widget.EditText;

import com.aprv.un.ImageItem;
import com.aprv.un.NoteItem;
import com.aprv.un.Settings;
import com.aprv.un.TextItem;

public class IndexedEditText extends EditText implements IndexedItem{
	private int index;
	private TextItem textItem;
	
	/**
	 * 
	 * @param index This view's index in parent layout
	 * @param context
	 */
	public IndexedEditText(int index, Context context) {
		super(context);
		this.index = index;
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {		
		if (focused) {
			NoteEditor.setCurId(index);			
			Log.i(Settings.TAG, "id = " + NoteEditor.getCurId());
		} else {
			
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}		
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int idx) {
		this.index = idx;
	}
	
	public void setTextItem(TextItem text) {
		this.textItem = text;
	}
	
	public TextItem getTextItem() {
		return textItem;
	}
	
	public NoteItem getNoteItem() {
		return textItem;
	}
	
	public void setNoteItem(NoteItem noteItem) {
		try {
			this.textItem = (TextItem) noteItem;
		} catch (Exception e) {
			Log.e(Settings.TAG, "Must set an ImageItem");
		}
	}
}
