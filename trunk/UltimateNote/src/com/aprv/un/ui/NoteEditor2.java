package com.aprv.un.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aprv.un.Constants;
import com.aprv.un.Settings;
import com.aprv.un.db.dao.UltimateNotesDAO;
import com.aprv.un.model.Media;
import com.aprv.un.model.Notes;

public class NoteEditor2 extends ListActivity{
	private UltimateNotesDAO mDAO;
	private Cursor mMediaCursor;
	private long mRowId;
	private int mState;
	private Notes mNote;
	private List<Media> mMedia;
	private ListView mListView;
	private Button mTextButton;
	private Button mPhotoButton;	
	private int mCurrentPos;	//Current position
	private boolean mItemListChanged = false;
	
	public static final int STATE_INSERT = 1;
	public static final int STATE_EDIT = 2;		
	private static final String STATE = "STATE";
	private static final int ACTIVITY_CAMERA_START = 0;
	private static final int MENU_ITEM_DELETE = Menu.FIRST;
	private static final int MENU_CANCEL = Menu.FIRST+1;
	
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
			//initSavedNote(savedInstanceState.(NOTE_ID)); //TODO: Change this hard code
			initSavedNote(intent.getLongExtra(Constants.KEY_NOTE_ID, 0));
		}		
		
		//Initialize with saved bundle
		if (savedInstanceState != null) {
			initSavedNote(savedInstanceState.getLong(Constants.KEY_NOTE_ID));
		}
		
		setContentView(R.layout.note_editor2);
		
		mListView = (ListView)findViewById(android.R.id.list);
		mTextButton = (Button)findViewById(R.id.textButton);
		mPhotoButton = (Button)findViewById(R.id.photoButton);

		mTextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//boolean curPosIsText = Media.TYPE_TEXT.equals(mCurrentPos<=mMedia.size()?mMedia.get(mCurrentPos).getType():"");
				//boolean nextPosIsText = Media.TYPE_TEXT.equals((mCurrentPos+1)<=mMedia.size()?mMedia.get(mCurrentPos+1).getType():"");
				boolean curPosIsText = false, nextPosIsText = false;
				
				if (mCurrentPos>=0 && mCurrentPos<mMedia.size())
					curPosIsText = Media.TYPE_TEXT.equals(mMedia.get(mCurrentPos).getType());
				if (mCurrentPos+1 < mMedia.size()) 
					nextPosIsText = Media.TYPE_TEXT.equals(mMedia.get(mCurrentPos+1).getType());
				
				if (!curPosIsText && !nextPosIsText) {
					Media m = new Media();
					m.setType(Media.TYPE_TEXT);				
					addMedia(m);
				} else {
					/*
					if (curPosIsText && mCurrentPos<mMedia.size()) {						
						//mListView.getSelectedView().requestFocus();
						//mListView.getChildAt(index)
					} else if (nextPosIsText && mCurrentPos+1<mMedia.size()) {
						//mListView.getChildAt(mCurrentPos+1).requestFocus();
					}
					*/
					getListView().setSelection(mCurrentPos+1<mMedia.size()?mCurrentPos:mCurrentPos+1);
				}
			}
		});
		
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonPhotoClicked();
			}
		});
								
		initListView();
		fillData();		
	}		
	
	private void initListView() {
		mListView = getListView();								
		
		mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mCurrentPos = arg2;
				Log.i(Settings.TAG, "Item selected: " + arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				//Do nothing
				return;							
			}
			
		});	
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(Settings.TAG, "Clicked on item # " + arg2);
				getListView().getFocusedChild().clearFocus();				
				arg1.requestFocus();
			}			
		});				
		
		//mListView.setClickable(true);	
		registerForContextMenu(mListView);
		mListView.setOnCreateContextMenuListener(this);		
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
		case MENU_ITEM_DELETE:
			deleteMedia(info.position);
			return true;
		
		case MENU_CANCEL: 
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
		menu.add(0, MENU_CANCEL, 0, R.string.menu_cancel);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(Settings.TAG, "Item clicked: " + position);
		// TODO Auto-generated method stub
		mListView.getSelectedView().clearFocus();
		setSelection(position);
		v.requestFocus();
		super.onListItemClick(l, v, position, id);
	}	
	
	private void addMedia(Media m) {		
		//int position = mListView.getSelectedItemPosition();
		int pos = mCurrentPos + 1;
		Log.i(Settings.TAG, "Adding media to position: " + mCurrentPos);		
		
		if (pos >= 0 && pos <= mMedia.size()) { 
			mMedia.add(pos, m);
			mCurrentPos = pos;
		}
		else {
			mMedia.add(m);
			mCurrentPos = mMedia.size()-1;
		}
		mItemListChanged = true;
		fillData();
	}
	
	private void initNewNote() {
		mState = STATE_INSERT;								
		mNote = new Notes();
		mNote.setTitle("<Untitled>");
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
	
	/**
	 * Merge consecutive text blocks resulted in media deletion
	 * @param position
	 */
	private void deleteMedia(int position) {
		mMedia.remove(position);
		mItemListChanged = true;
		mCurrentPos = position; //Move to the deleted 
		if (mMedia.size() == 0)
			mCurrentPos = -1;
		if (mCurrentPos >= mMedia.size())
			mCurrentPos = mMedia.size()-1;
		
		//Merge consecutive text blocks
		if (mCurrentPos > 0) {
			Media m1 = mMedia.get(mCurrentPos - 1);
			Media m2 = mMedia.get(mCurrentPos);
			if (Media.TYPE_TEXT.equals(m1.getType()) && Media.TYPE_TEXT.equals(m2.getType())) {
				String mergedText = m1.getSource() + "\n" + m2.getSource();
				m1.setSource(mergedText);
				deleteMedia(mCurrentPos);
			}
		}
		
		fillData();
	}
	
	private void buttonPhotoClicked() {		
		Intent i = new Intent(this, CameraPreview.class);		
    	startActivityForResult(i, ACTIVITY_CAMERA_START);
	}
		
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub				
		// Save all data
		if (!mItemListChanged)
			mDAO.updateNotesContentOnly(mRowId, mNote, mMedia);
		else {
			mDAO.updateNotes(mRowId, mNote, mMedia);
			mItemListChanged = false;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		//Log.i(Settings.TAG, "onResume() called.");
		//initSavedNote(mRowId);
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(Settings.TAG, "onSaveInstanceState() called.");
		// TODO Auto-generated method stub		
		outState.putLong(Constants.KEY_NOTE_ID, mRowId);
		outState.putInt(STATE, STATE_EDIT);
		super.onSaveInstanceState(outState);
	}

	private void fillData() {
		NoteItemsAdapter adapter = new NoteItemsAdapter(this, mMedia, this);		
		setListAdapter(adapter);				
		getListView().setSelection(mCurrentPos);
	}		
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		// Save everything
		// If this note is empty, just delete it
		/*
		if (mMedia.size() == 0) {
			mDAO.deleteNotes(mRowId);
		} else {
			mDAO.updateNotes(mRowId, mNote, mMedia);
		}
		*/
		//mDAO.close();
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
    			Media m = new Media();
    			m.setSource(path);
    			m.setType(Media.TYPE_IMAGE);
    			addMedia(m);
    			    			
    			Toast toast = Toast.makeText(this, "Photo saved to " + path, 3);
    			//toast.setGravity(Gravity.TOP, 0, 0);
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


	/**
     * Take care of canceling work on a note.  Deletes the note if we
     * had created it, otherwise reverts to the original text.
     */
    private final void cancelNote() {
        
        if (mState == STATE_EDIT) {
            // Put the original note text back into the database
            //mCursor.close();
            //mCursor = null;
            //ContentValues values = new ContentValues();
            //values.put(Notes.NOTE, mOriginalContent);
            //getContentResolver().update(mUri, values, null, null);
        	
        	
        } else if (mState == STATE_INSERT) {
            // We inserted an empty note, make sure to delete it
            deleteNote();
        }
        
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private final void deleteNote() {
    	mDAO.deleteNotes(mRowId);
    }
    
    public int getCurrentPosition() {
    	return mCurrentPos;
    }
    
    public void setCurrentPosition(int pos) {
    	mCurrentPos = pos;
    }
}
