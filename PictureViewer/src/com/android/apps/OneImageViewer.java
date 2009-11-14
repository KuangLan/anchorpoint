package com.android.apps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OneImageViewer extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.one_image);
		
		Bundle extra = getIntent().getExtras();
		
		TextView title = (TextView) findViewById(R.id.TextView01);
	    ImageView img = (ImageView) findViewById(R.id.ImageView01);
	    Button confirmButton = (Button) findViewById(R.id.confirm);
	   
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        Integer resloc = (Integer)extras.get("image");
	        String imgname = (String)extras.get("imagename");
	        if (resloc != null && imgname != null) {
	            title.setText(imgname);
	            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        img.setPadding(2,2,2,2);
	            img.setImageResource(resloc);
	        }
	    }

	    confirmButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent();
	            setResult(RESULT_OK, mIntent);
	            finish();
			}
		});
	}
}
