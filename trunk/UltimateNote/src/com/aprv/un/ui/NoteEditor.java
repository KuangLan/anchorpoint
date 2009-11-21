package com.aprv.un.ui;

import java.util.ArrayList;
import java.util.List;
import com.aprv.un.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NoteEditor extends Activity{
	private static int ACTIVITY_CAMERA_START = 0;
	
	private LinearLayout mItemsLinearLayout;
	private Button mTextButton;
	private Button mPhotoButton;
	private Note mNote;
	private String mediaLocation;
	
	private List<EditText> mEditTexts;
	private List<ImageView> mImageViews;	
	
	private static int curId = 0; 
	private List<IndexedItem> itemList;
		  
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_editor);
		
		//initialize variables
		mEditTexts = new ArrayList<EditText>();
		mImageViews = new ArrayList<ImageView>();
		itemList = new ArrayList<IndexedItem>();
		
		//TODO - Get note from Bundle
		mNote = new Note();
		Intent i = getIntent();
		mediaLocation = i.getStringExtra(Settings.KEY_SAVED_MEDIA_LOCATION);
		if (mediaLocation == null) {
			mediaLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UltimateNote/media";
		}
		
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
	
	private void populateView() {
		
	}
	
	private void buttonPhotoClicked() {		
		Intent i = new Intent(this, CameraPreview.class);		
		i.putExtra(Settings.KEY_SAVED_MEDIA_LOCATION, mediaLocation);		
    	startActivityForResult(i, ACTIVITY_CAMERA_START);
	}
	
	private void buttonTextClicked() {
		int size = itemList.size();
		boolean add1 = !(size!=0 && itemList.get(curId) instanceof EditText);
		boolean add2 = !(curId+1 < size && itemList.get(curId+1) instanceof EditText);
		if (add1 && add2) 
			addTextItem(curId);
		else {
			Toast toast = Toast.makeText(this, "A text entry already exists at requested position.", 3);
			toast.show();
		}
	}
	
	/**
	 * 
	 * @param loc Insert after location loc
	 */
	private void addTextItem(int loc) {
		int idx = loc + 1;	//to insert after loc
		if (itemList.size() == 0) {
			idx = 0;
		}
		EditText newEditText = new IndexedEditText(idx, this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		newEditText.setLayoutParams(params);		
		((IndexedEditText)newEditText).setNoteItem(new TextItem());		
		insertIndexedItem(idx, (IndexedItem)newEditText);
	}
			
	/**
	 * 
	 * @param loc position to be inserted into
	 * @param indexedItem
	 */
	private void insertIndexedItem(int loc, IndexedItem indexedItem) {		 
		if (indexedItem instanceof View) {
			View view = (View)indexedItem;
			insertView(loc);
			itemList.add(loc, indexedItem);
			mItemsLinearLayout.addView(view, loc);
			mNote.getNoteItemList().add(loc, indexedItem.getNoteItem());
			view.requestFocus();
		}
		else {
			Log.e(Settings.TAG, "Must add a View item");
		}
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (requestCode == ACTIVITY_CAMERA_START) {
        	switch (resultCode) {
    		case RESULT_OK:    			    			    			    			
    			if (intent == null) {
    				Log.e(Settings.TAG, "onActivityResult returned intent is null");
    				return;
    			}    			
    			if (intent == null) {
    				Log.e(Settings.TAG, "Intent is null");
    			}
    			else {
    				Log.e(Settings.TAG, "Good intent");
    			}
    			Bundle bundle = intent.getExtras();
    			Log.e(Settings.TAG, "contain Settings.KEY_PATH = " + bundle.containsKey(Settings.KEY_PATH));
    			
    			String path = bundle.getString(Settings.KEY_PATH);
    			addImage(curId, path);
    			
    			Toast toast = Toast.makeText(this, "Photo saved to " + path, 5);
    			toast.show();
    			break;
    		case RESULT_CANCELED:
    			toast = Toast.makeText(this, "Image not captured", 3);
    			toast.show();
    			break;
    		default:
        		Log.e("vinh","Unknown Camera result.");
        		break;
        	}        	
        }
    }
	
	private void addImage(int loc, String path) {
		Log.i(Settings.TAG, "add image " + path);
		int idx = loc + 1;	//to insert after loc
		if (itemList.size() == 0) {
			idx = 0;
		}		
		Bitmap bitmap = BitmapFactory.decodeFile(path);		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView newImageView = new IndexedImageView(idx, this);		
		newImageView.setLayoutParams(params);				
		newImageView.setImageBitmap(bitmap);
		((IndexedImageView)newImageView).setNoteItem(new ImageItem(path, path));		
		insertIndexedItem(idx, (IndexedItem)newImageView);
		
		if (bitmap == null) {
			//TODO - Notify user
			Log.e(Settings.TAG, "Cannot read " + path);
		}
	}
	
	
	/**
	 * 
	 * @param idx position to insert
	 * @return
	 */
	private int insertView(int idx) {
		for (int i=idx; i<itemList.size(); i++) {
			IndexedItem view = itemList.get(i);
			view.setIndex(i+1);	//push all item back 1
		}
		return idx;
	}
	
	public static int getCurId() {
		return curId;
	}
	
	public static void setCurId(int index) {
		curId = index;
	}	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		return super.onCreateOptionsMenu(menu);
	}	
	*/
}
