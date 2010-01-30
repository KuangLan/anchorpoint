package com.aprv.un.ui;

import com.aprv.un.model.Media;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class IndexedAudioItem extends IndexedImageView{	
	/**
	 * 
	 * @param index This view's index in parent layout
	 * @param context
	 */
	public IndexedAudioItem(int index, Context context, Media media) {
		super(context);
		this.index = index;
		this.media = media;
		this.setFocusable(true);
		this.setPadding(4, 4, 4, 4);		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(params);		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		//this.setAdjustViewBounds(true);
		//this.setMaxHeight(320);
		
		this.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio));		
	}	
}
