package com.aprv.un.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aprv.un.ImageItem;
import com.aprv.un.Note;
import com.aprv.un.Settings;
import com.aprv.un.TextItem;

public class NoteEditor extends Activity{
	private static final int MENU_ITEM_DELETE = Menu.FIRST;

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
	
	private View selectedView;
		  
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
			//toast.setGravity(Gravity.TOP, 0, 0);
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
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		newEditText.setLayoutParams(params);		
		newEditText.setBackgroundColor(Color.TRANSPARENT);
		newEditText.setTextSize(20.0f);
		
		((IndexedEditText)newEditText).setNoteItem(new TextItem("TXT" + idx, ""));		
		insertIndexedItem(idx, (IndexedItem)newEditText);		
	}
			
	/**
	 * Common method to be called when inserting note items - Set all common settings here.
	 * @param loc position to be inserted into
	 * @param indexedItem
	 */
	private void insertIndexedItem(int loc, IndexedItem indexedItem) {	
		Log.i(Settings.TAG, "Added item to " + loc);
		if (indexedItem instanceof View) {
			View view = (View)indexedItem;
			insertView(loc);
			itemList.add(loc, indexedItem);
			mItemsLinearLayout.addView(view, loc);
			mNote.getNoteItemList().add(loc, indexedItem.getNoteItem());
			registerForContextMenu(view);
			view.setPadding(5, 5, 5, 5);
			view.setFocusableInTouchMode(true);
			View cur = (View)itemList.get(curId);
			cur.clearFocus();
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
    			
    			Toast toast = Toast.makeText(this, "Photo saved to " + path, 3);
    			//toast.setGravity(Gravity.TOP, 0, 0);
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
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {                     
        // TODO - Setup the menu header      
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_remove);
		selectedView = view;
        // Add a menu item to delete the note
		super.onCreateContextMenu(menu, view, menuInfo);		                        
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {   
    	AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
             
        } catch (ClassCastException e) {
            Log.e(Settings.TAG, "bad menuInfo", e);
            return false;
        }
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
            	//Log.i(Settings.TAG, "Deleting item at " + info.id);
            	//1 TODO - Delete item data in DB
                //2 Delete UI component
            	
            	try {            		            		
	            	IndexedItem view = (IndexedItem)selectedView;
	            	int idx = view.getIndex();
	            	Log.i(Settings.TAG, "Deleting: " + view.getIndex() + " List size: " + itemList.size());
	            	if (curId >= view.getIndex()) {
	            		curId--;
	            	}
	            	curId = (curId<0)?0:curId;
	            	removeView(idx);
	            	/*
	            	if (itemList.size() <= 1) {
	            		itemList.clear();
	            		mNote.getNoteItemList().clear();
	            	}
	            	*/
	            	//else {
	            		itemList.remove(idx);
	            		mNote.getNoteItemList().remove(idx);
	            		if (curId < itemList.size())
	            			((View)itemList.get(curId)).requestFocus();
	            	//}	            	
	            	mItemsLinearLayout.removeView(selectedView);	            		            		            	
            	} catch (Exception e) {
            		Log.e(Settings.TAG, "Cannot delete view");
            	}
            	
            	//Toast.makeText(this, "Not supported yet.", 3).show();
                return true;
            }
        }
        return false;
    }
	
	private void addImage(int loc, String path) {
		Log.i(Settings.TAG, "add image " + path);
		int idx = loc + 1;	//to insert after loc
		if (itemList.size() == 0) {
			idx = 0;
		}		
		Bitmap bitmap = BitmapFactory.decodeFile(path);		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ImageView newImageView = new IndexedImageView(idx, this);		
		newImageView.setLayoutParams(params);				
		newImageView.setImageBitmap(bitmap);
		((IndexedImageView)newImageView).setNoteItem(new ImageItem("IMG"+idx, path));		
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
			view.setIndex(i+1);	//push all item by 1
		}
		return idx;
	}
	
	private int removeView(int idx) {
		for (int i=idx; i<itemList.size(); i++) {
			IndexedItem view = itemList.get(i);
			view.setIndex(i-1);	//back 1
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
