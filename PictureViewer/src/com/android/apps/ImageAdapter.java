package com.android.apps;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

	// references to our images
    private Integer[] mThumbIds = {
            R.drawable.apple_32, R.drawable.designmoo_32,
            R.drawable.facebook_32, R.drawable.picasa_32,
            R.drawable.skype_32,R.drawable.yahoo_32
    };

	public int getCount() {
		// TODO Auto-generated method stub
		return mThumbIds.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mThumbIds[arg0];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		// related to the row ID (DB related)
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ImageView img;
		 
	     if (convertView == null) {  	
	    	 System.out.println("initialize");
	    	 img = new ImageView(mContext);
	     	 img.setLayoutParams(new GridView.LayoutParams(40,40));
	         img.setScaleType(ImageView.ScaleType.CENTER_CROP);
	         img.setPadding(2,2,2,2);
	     } else {
	    	 System.out.println("recycle");
	         img = (ImageView) convertView;
	     }
		img.setImageResource(mThumbIds[position]);
		return img;
	}
	
}
