package com.aprv.un.ui;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.aprv.un.Settings;
import com.aprv.un.model.Media;

public class MediaTextEdit extends EditText{
	private Media mMedia;
	
	public MediaTextEdit(Context ctx, Media media){
		super(ctx);		
		this.mMedia = media;		
	}
	
	public Media getMedia() { return mMedia; }
	public void setMedia(Media m) { this.mMedia = m; }

	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		super.onTextChanged(text, start, before, after);
		// TODO Auto-generated method stub		
		if (mMedia != null && text != null) {
			mMedia.setSource(text.toString());			
		} else {			
		}		
	}				
}
