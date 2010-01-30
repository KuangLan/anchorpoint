package com.aprv.un.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.TextSize;
import android.widget.EditText;

import com.aprv.un.NoteItem;
import com.aprv.un.Settings;
import com.aprv.un.TextItem;
import com.aprv.un.model.Media;

public class IndexedEditText extends EditText implements IndexedItem{
	private int index;
	private Media media;
	
	private Rect mRect;
    private Paint mPaint;
	
	/**
	 * 
	 * @param index This view's index in parent layout
	 * @param context
	 */
	public IndexedEditText(int index, Context context, Media media) {
		super(context);
		this.index = index;
		this.media = media;
		this.setPadding(4, 2, 4, 2);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(params);
		this.setKeyListener(new TextKeyListener(Capitalize.SENTENCES, false));
		//this.setBackgroundColor(Color.TRANSPARENT);
		
		setText(media.getSource());
		
		//To draw lines
		mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0x800000FF);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {		
		if (focused) {
			NoteEditor.setCurrentPos(index);	
			Log.i(Settings.TAG, "txt " + NoteEditor.getCurrentPos());
		} else {
			
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}		
	
	
	
	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		if (media!=null && text!=null)
			media.setSource(text.toString());
		super.onTextChanged(text, start, before, after);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;
                
        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);

            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);            
        }
		
		super.onDraw(canvas);
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int idx) {
		this.index = idx;
	}
	
	public void setMedia(Media text) {
		this.media = text;
	}
	
	public Media getMedia() {
		return media;
	}		
}
