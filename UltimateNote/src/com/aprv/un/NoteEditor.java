package com.aprv.un;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NoteEditor extends Activity{
	private static int ACTIVITY_CAMERA_START = 0;
	
	private LinearLayout mItemsLinearLayout;
	private Button mTextButton;
	private Button mPhotoButton;
	private Note mNote;
	
	private List<EditText> mEditTexts;
	private List<ImageView> mImageViews;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_editor);
		
		//initialize variables
		mEditTexts = new ArrayList<EditText>();
		mImageViews = new ArrayList<ImageView>();
		mNote = new Note();
		
		mPhotoButton = (Button)findViewById(R.id.photoButton);
		mTextButton = (Button)findViewById(R.id.textButton);
		mItemsLinearLayout = (LinearLayout)findViewById(R.id.itemsLinearLayout);
		
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonPhotoClicked();				
			}
		});
		
		mTextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonTextClicked();
			}
		});
	}
	
	private void buttonPhotoClicked() {		
		Intent i = new Intent(this, CameraPreview.class);
    	startActivityForResult(i, ACTIVITY_CAMERA_START);
	}
	
	private void buttonTextClicked() {
		 EditText newEditText = new EditText(this);
		 LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		 newEditText.setLayoutParams(params);
		 mEditTexts.add(newEditText);
		 mItemsLinearLayout.addView(newEditText);
		 mNote.getNoteItemList().add(new TextItem(""));
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == ACTIVITY_CAMERA_START) {
        	switch (resultCode) {
    		case RESULT_OK:
    			Log.i("vinh","Captured.");
    			ImageView newImageView = new ImageView(this);
    			String path = UltimateNoteActivity.getImageDir() + "/" + UltimateNoteActivity.getNextPhotoName();
    			Bitmap bitmap = BitmapFactory.decodeFile(path);  
    			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);    			
    			newImageView.setLayoutParams(params);
    			//newImageView.setMaxHeight(20);
    			//newImageView.setMaxWidth(20);
    			newImageView.setImageBitmap(bitmap); 
    			mItemsLinearLayout.addView(newImageView);
    			mNote.getNoteItemList().add(new ImageItem(path));
    			UltimateNoteActivity.incrementPhotoNum();       			
    			break;
    		default:
        		Log.e("vinh","Unknown Camera result.");
        		break;
        	}        	
        }
    }
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		return super.onCreateOptionsMenu(menu);
	}	
	*/
}
