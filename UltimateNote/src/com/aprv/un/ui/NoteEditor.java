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
import android.widget.ListView;
import android.widget.Toast;

import com.aprv.un.Constants;
import com.aprv.un.ImageItem;
import com.aprv.un.Note;
import com.aprv.un.Settings;
import com.aprv.un.TextItem;
import com.aprv.un.db.dao.UltimateNotesDAO;
import com.aprv.un.model.Media;
import com.aprv.un.model.Notes;

public class NoteEditor extends Activity{
	private static final int MENU_ITEM_DELETE = Menu.FIRST;
	private static final int MENU_CANCEL = Menu.FIRST+1;

	private static int ACTIVITY_CAMERA_START = 0;
	public static final int STATE_INSERT = 1;
	public static final int STATE_EDIT = 2;
	private static final String UNTITLED = "<Untitled>";			
	
	private LinearLayout mItemsLinearLayout;
	private Button mTextButton;
	private Button mPhotoButton;
	private Notes mNote;
	private List<Media> mMedia;
	private UltimateNotesDAO mDAO;
	private int mState;
	private long mRowId;
	private String mediaLocation;
	private boolean mItemListChanged = false;
	
	private static int mCurrentPos = -1; //always start at -1 
	private List<IndexedItem> itemList;		
		  
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mDAO = new UltimateNotesDAO(this);
		mDAO.open();
		Intent intent = getIntent();		
		
		//Initialize when this activity is called by an intent
		if (intent!=null && Constants.ACTION_INSERT_NOTE.equals(intent.getAction())) {
			initNewNote();
		} else if (intent!=null && Constants.ACTION_EDIT_NOTE.equals(intent.getAction())) {			
			initSavedNote(intent.getLongExtra(Constants.KEY_NOTE_ID, 0));
		}		
		
		//Initialize with saved bundle
		if (savedInstanceState != null) {
			initSavedNote(savedInstanceState.getLong(Constants.KEY_NOTE_ID));
		}
		
		setContentView(R.layout.note_editor);
				
		mTextButton = (Button)findViewById(R.id.textButton);
		mPhotoButton = (Button)findViewById(R.id.photoButton);
		mTextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean curPosIsText = false, nextPosIsText = false;
				
				if (mCurrentPos>=0 && mCurrentPos<mMedia.size())
					curPosIsText = Media.TYPE_TEXT.equals(mMedia.get(mCurrentPos).getType());
				if (mCurrentPos+1 < mMedia.size()) 
					nextPosIsText = Media.TYPE_TEXT.equals(mMedia.get(mCurrentPos+1).getType());
				
				if (!curPosIsText && !nextPosIsText) {
					Media m = new Media();
					m.setType(Media.TYPE_TEXT);						
					addMedia(m, false);
				} else {
					
				}
			}
		});
		
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonPhotoClicked();
			}
		});
		
		loadData();	
	}
	
	private void addMedia(Media m, boolean addViewOnly) {
		addMedia(mCurrentPos+1, m, addViewOnly);
	}
	
	private void addMedia(int pos, Media m, boolean addViewOnly) {
		//Prepare data		
		View view = null;
		if (Media.TYPE_TEXT.equals(m.getType())) {
			view = new IndexedEditText(pos, this, m);				
		} else if (Media.TYPE_IMAGE.equals(m.getType())) {
			view = new IndexedImageView(pos, this, m);
		}
						
		Log.i(Settings.TAG, "Adding media to position: " + mCurrentPos);		
		
		//Push items from added position 1 step
		for (int i=pos; i<mItemsLinearLayout.getChildCount(); i++) {
			IndexedItem v = (IndexedItem)mItemsLinearLayout.getChildAt(i);
			v.setIndex(v.getIndex()+1);
		}
		
		if (pos >= 0 && pos <= mMedia.size()) {
			if (!addViewOnly) {
				mMedia.add(pos, m);
				mItemListChanged = true;
			}
			mItemsLinearLayout.addView(view, pos);
		}
		else {
			if (!addViewOnly) {
				mMedia.add(m);
				mItemListChanged = true;
			}
			mItemsLinearLayout.addView(view);
		}			
		view.requestFocus();
		registerForContextMenu(view);		
	}		
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		saveNote();
		super.onSaveInstanceState(outState);
	}
		
	@Override
	protected void onPause() {
		saveNote();
		super.onPause();
	}
	
	private void loadData() {
		mCurrentPos = -1; //Very important!
		mItemsLinearLayout = (LinearLayout) findViewById(R.id.itemsLinearLayout);
		mItemsLinearLayout.removeAllViews();
		for (int i=0; i<mMedia.size(); i++) {
			Media media = mMedia.get(i);							
			addMedia(i, media, true);
		}
		//Focus on the first item
		if (mItemsLinearLayout.getChildCount()>0)
			mItemsLinearLayout.getChildAt(0).requestFocus();
	}		
	
	private void saveNote() {		 
		//Find text items
		if (UNTITLED.equals(mNote.getTitle())) {
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<mMedia.size(); i++) {
				if (Media.TYPE_TEXT.equals(mMedia.get(i).getType())) {
					sb.append(mMedia.get(i).getSource()).append(' ');
				}
			}		
			if (sb.toString().trim().length() > 0)
				mNote.setTitle(sb.toString());
		}
		if (mItemListChanged) {
			//Update title
			
			mDAO.updateNotes(mRowId, mNote, mMedia);
			mItemListChanged = false;
		} else {
			mDAO.updateNotesContentOnly(mRowId, mNote, mMedia);
		}
	}

	private void initNewNote() {
		mState = STATE_INSERT;								
		mNote = new Notes();
		mNote.setTitle(UNTITLED);
		mMedia = new ArrayList<Media>();
		mRowId = mDAO.createNotes(mNote, mMedia);	
		mState = STATE_EDIT;								
	}
	
	private void initSavedNote(long rowId) {
		mState = STATE_EDIT;
		mRowId = rowId;
		mNote = mDAO.getNote(mRowId);
		mMedia = mDAO.getMediaInNote(mRowId);		
		if (mMedia == null)	//to fix the bug on onResume() is called the first time
			mMedia = new ArrayList<Media>();
		
		Log.i(Settings.TAG, "initSvedNote: " + rowId + " " + mNote + " " + mMedia);
	}
	
	private void buttonPhotoClicked() {		
		Intent i = new Intent(this, CameraPreview.class);		
		i.putExtra(Settings.KEY_SAVED_MEDIA_LOCATION, mediaLocation);		
    	startActivityForResult(i, ACTIVITY_CAMERA_START);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (requestCode == ACTIVITY_CAMERA_START) {
        	switch (resultCode) {
    		case RESULT_OK:    			    			    			    			    			
    			Bundle bundle = intent.getExtras();    			    			
    			String path = bundle.getString(Settings.KEY_PATH);
    			Media media = new Media();
    			media.setSource(path);
    			media.setType(Media.TYPE_IMAGE);
    			addMedia(media, false);
    			
    			Toast toast = Toast.makeText(this, "Photo saved to " + path, 3);
    			toast.show();
    			break;
    		case RESULT_CANCELED:
    			toast = Toast.makeText(this, "Image not captured", 3);
    			toast.show();
    			break;
    		default:
        		Log.e(Settings.TAG,"Unknown Camera result.");
        		break;
        	}        	
        }
    }
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {                     
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_remove);
		menu.add(0, MENU_CANCEL, 0, R.string.menu_cancel);
		menu.setHeaderTitle(R.string.title_edit_item);
		super.onCreateContextMenu(menu, view, menuInfo);		                        
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {       	
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
	            deleteMedia(mCurrentPos);	            		            		            	
                return true;
            }
            case MENU_CANCEL: {
            	return true;
            }
        }
        return false;
    }
    
    /**
	 * Merge consecutive text blocks resulted in media deletion
	 * @param position
	 */
	private void deleteMedia(int position) {
		Log.i(Settings.TAG, "Delete item at " + position);
		//Move items
		for (int i=position; i < mItemsLinearLayout.getChildCount(); i++) {
    		IndexedItem v = (IndexedItem) mItemsLinearLayout.getChildAt(i);
    		v.setIndex(v.getIndex()-1);
    	}				       	       	
    	    	    	
    	if (mCurrentPos > position || mCurrentPos == mMedia.size()) {
    		mCurrentPos--;
    	}
    	if (mMedia.size() == 0 || mCurrentPos < 0) 
    		mCurrentPos = -1;
		
    	//Delete data and view
    	mMedia.remove(position);
    	mItemsLinearLayout.removeViewAt(position);
    	
		//Merge consecutive text blocks
		if (position > 0 && position < mMedia.size()) {
			Media m1 = mMedia.get(position - 1);
			Media m2 = mMedia.get(position);
			if (Media.TYPE_TEXT.equals(m1.getType()) && Media.TYPE_TEXT.equals(m2.getType())) {
				Log.i(Settings.TAG, "pos=" + position + " consecutive text");
				String mergedText = m1.getSource() + "\n" + m2.getSource();
				m1.setSource(mergedText);
				EditText text = (EditText)mItemsLinearLayout.getChildAt(position-1);
				text.setText(mergedText);
				
				deleteMedia(position);
			}
		}		
		mItemListChanged = true;
	}
	
	public static int getCurrentPos() {
		return mCurrentPos;
	}
	
	public static void setCurrentPos(int index) {
		mCurrentPos = index;
	}	
}
