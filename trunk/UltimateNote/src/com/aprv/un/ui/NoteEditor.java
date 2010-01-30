package com.aprv.un.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

import com.aprv.un.Constants;
import com.aprv.un.Settings;
import com.aprv.un.db.dao.UltimateNotesDAO;
import com.aprv.un.helper.SaveFileHelper;
import com.aprv.un.model.Media;
import com.aprv.un.model.Notes;

public class NoteEditor extends Activity{
	private static final int MENU_ITEM_DELETE = Menu.FIRST;
	private static final int MENU_ITEM_ROTATE_LEFT = Menu.FIRST + 1;
	private static final int MENU_ITEM_ROTATE_RIGHT = Menu.FIRST + 2;
	private static final int MENU_ITEM_PLAY = Menu.FIRST + 3;
	private static final int MENU_CANCEL = Menu.FIRST+10;

	private static int ACTIVITY_CAMERA_START = 0;
	private static int ACTIVITY_PAINT_START = 1;
	public static final int STATE_INSERT = 1;
	public static final int STATE_EDIT = 2;
	private static final String UNTITLED = "<Untitled>";
	private static int NOTE_TITLE_LENGTH = 20;	
	
	private NotificationManager mNotificationManager; 
    private int NOTIFICATION_ID = 142148;
	private LinearLayout mItemsLinearLayout;
	private ImageButton mTextButton;
	private ImageButton mPhotoButton;
	private ImageButton mInsertButton;
	private ImageButton mRecordAudioButton;	
	private ImageButton mPaintButton;
	private Notes mNote;
	private List<Media> mMedia;
	private UltimateNotesDAO mDAO;
	private MediaRecorder mRecorder;
		
	private int mState;
	private long mRowId;
	private final String mediaLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UltimateNote/media";
	private final String imagePath = mediaLocation + "/images";
	private final String audioPath = mediaLocation + "/audio";
	private boolean mItemListChanged = false;
	private boolean mRecordingAudio = false;
	private String mRecordAudioPath;
	private int mAudioCount = 0;
	
	private static int mCurrentPos = -1; //always start at -1 	
		  
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
		
		// Get the notification manager service. 
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
		
		//Initialize with saved bundle
		if (savedInstanceState != null) {
			initSavedNote(savedInstanceState.getLong(Constants.KEY_NOTE_ID));
		}
		
		setContentView(R.layout.note_editor);
				
		mTextButton = (ImageButton)findViewById(R.id.textButton);
		mPhotoButton = (ImageButton)findViewById(R.id.photoButton);
		mInsertButton = (ImageButton)findViewById(R.id.insertButton);
		mRecordAudioButton = (ImageButton)findViewById(R.id.recordAudioButton);
		mPaintButton = (ImageButton)findViewById(R.id.paintButton);
		
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
					if (nextPosIsText) {
						mCurrentPos++;												
						mItemsLinearLayout.getChildAt(mCurrentPos).requestFocus();							
					}
				}
			}
		});
		
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonPhotoClicked();
			}
		});
		
		mInsertButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonInsertClicked();				
			}
		});	
		
		mRecordAudioButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				buttonRecordAudioClicked();				
			}
		});
		
		mPaintButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonPaintClicked();
			}
		});
		
		loadData();	
	}
	
	/**
	 * Prepare notifications for different types
	 * @param type	1: audio, 2: photo, 3: video etc
	 */
	private Notification prepareNotification(int type) {		
		int icon = -1;
		CharSequence text = null;
		long when = System.currentTimeMillis();
		
		switch (type) {
			case 0:
				icon = R.drawable.ic_audio_recording_24;
				text = "Recording audio";
				break;			
		}
		
		Notification notification = new Notification(icon, text, when);
		
		//Define Notiication's expanded message and Intent
		Context context = getApplicationContext();
		CharSequence contentTitle = "Recording Audio";
		CharSequence contentText = "Click here to open UltimateNote";
		/*
		Intent notificationIntent = new Intent(this, NoteEditor.class);			
		notificationIntent.setAction(Constants.ACTION_EDIT_NOTE);
		notificationIntent.putExtra(Constants.KEY_NOTE_ID, mRowId);
    	*/
		Intent notificationIntent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		return notification;
	}
	
	/**
	 * 
	 * @param m
	 * @param addViewOnly	if true, do not add this media to the managed media list, just add the view	
	 */
	private void addMedia(Media m, boolean addViewOnly) {
		addMedia(mCurrentPos+1, m, addViewOnly);
	}
	
	/**
	 * This method is to bypass Java language check when implementing listener - Very ugly and not to be used in long run
	 * @param from
	 * @param to
	 */
	private void updateMedia(Media from, Media to) {
		from.setCaption(to.getCaption());
		from.setId(to.getId());
		from.setName(to.getName());
		from.setSource(to.getSource());
		from.setType(to.getType());
	}
	
	private void addMedia(int pos, Media m, boolean addViewOnly) {
		//Prepare data		
		View view = null;
		if (Media.TYPE_TEXT.equals(m.getType())) {
			view = new IndexedEditText(pos, this, m);				
		} else if (Media.TYPE_AUDIO.equals(m.getType())) {
			++mAudioCount;									
			IndexedLinearLayout ll = new IndexedLinearLayout(pos, this, m);
			ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			ll.setOrientation(LinearLayout.HORIZONTAL);			
			ImageView icon = new ImageView(this);
			icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio));
			EditText te = new EditText(this);			
			te.setBackgroundColor(Color.TRANSPARENT);
			
			if (m.getCaption()!=null && m.getCaption().length() > 0) {
				te.setText(String.valueOf(m.getCaption()));
			} else {
				te.setText("Recording " + mAudioCount);
			}		
			
			ll.addView(icon);
			ll.addView(te);
			view = ll;
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
		Toast.makeText(this, "mCurrentPos = " + mCurrentPos, 3);
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
			
			String tmp = sb.toString();
			if (tmp.length() > NOTE_TITLE_LENGTH) {				
				mNote.setTitle(tmp.substring(0, NOTE_TITLE_LENGTH) + "...");
			} else if (tmp.length()>0) {
				mNote.setTitle(tmp);
			}
		}
		
		//The below ugly code is to update audio caption - TODO: change this asap! This is not for long-run
		for (int i=0; i<mMedia.size(); i++) {
			Media m = mMedia.get(i);
			if (Media.TYPE_AUDIO.equals(m.getType())) {
				LinearLayout ll = (LinearLayout)mItemsLinearLayout.getChildAt(i);
				EditText et = (EditText)ll.getChildAt(1);
				if (!et.getText().toString().equals(m.getCaption())) {
					m.setCaption(et.getText().toString());
					mItemListChanged = true;
				}
			}
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
		i.putExtra(Settings.KEY_SAVED_MEDIA_LOCATION, imagePath);		
    	startActivityForResult(i, ACTIVITY_CAMERA_START);
	}
	
	private void buttonInsertClicked() {
		Dialog fileBrowser = new FileBrowserDialog2(this);				
		fileBrowser.show();
		Log.i(Settings.TAG, "Dialog opened.");
	}
	
	private void buttonRecordAudioClicked() {
		if (mRecordingAudio == false) {
			//Start recording
			mRecorder = new MediaRecorder();
			
			String date = SaveFileHelper.generateDateString();			
			File file = new File(audioPath);
			if (!file.exists()) {
				file.mkdir();				
			}
			
			mRecordAudioPath = SaveFileHelper.generateName(audioPath, "/", date, "3GPP");			
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(mRecordAudioPath);
			
			try {
				mRecorder.prepare();
				mRecorder.start();			
				mRecordingAudio = true;
				//Change icon				
				mRecordAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_recording_24));
				
				//Set status bar notfication
				mNotificationManager.notify(NOTIFICATION_ID, prepareNotification(0));
			} catch (Exception e) {
				mRecordAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_audio_24));
				mRecorder.stop();
				mRecorder.release();
			}
		} else {	//stop recording
			//Set icon back to its original form
			mRecordAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_audio_24));
			
			//Remove status bar notification
			mNotificationManager.cancel(NOTIFICATION_ID);
			
			mRecordingAudio = false;
			if (mRecorder != null) {
				mRecorder.stop();
				mRecorder.release();
				
				//Add to media
				Media media = new Media();
				media.setSource(mRecordAudioPath);
    			media.setType(Media.TYPE_AUDIO);
    			
				addMedia(media, false);
			}			
			Toast.makeText(this, "Recording saved to " + mRecordAudioPath, 3).show();
		}
	}
	
	private void buttonPaintClicked() {
		Intent i = new Intent(this, FingerPaint.class);		
		i.putExtra(Settings.KEY_SAVED_MEDIA_LOCATION, imagePath);		
    	startActivityForResult(i, ACTIVITY_PAINT_START);
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
        
        if (requestCode == ACTIVITY_PAINT_START) {
        	switch (resultCode) {
    		case RESULT_OK:    			    			    			    			    			
    			Bundle bundle = intent.getExtras();    			    			
    			String path = bundle.getString(Settings.KEY_PATH);
    			Media media = new Media();
    			media.setSource(path);
    			media.setType(Media.TYPE_IMAGE);
    			addMedia(media, false);
    			
    			Toast toast = Toast.makeText(this, "Painting saved to " + path, 3);
    			toast.show();
    			break;
    		case RESULT_CANCELED:
    			toast = Toast.makeText(this, "Painting not saved", 3);
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
		if (view instanceof IndexedLinearLayout) {
			Media media = ((IndexedLinearLayout)view).getMedia();
			if (Media.TYPE_AUDIO.equals(media.getType())) {
				menu.add(0, MENU_ITEM_PLAY, 0, R.string.menu_play);
			}
		}
		else if (view instanceof IndexedImageView) {
			menu.add(0, MENU_ITEM_ROTATE_LEFT, 0, R.string.menu_rotateLeft);
			menu.add(0, MENU_ITEM_ROTATE_RIGHT, 0, R.string.menu_rotateRight);
		}
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
            case MENU_ITEM_ROTATE_LEFT: {
            	Media media = mMedia.get(mCurrentPos);
            	IndexedImageView imageView = (IndexedImageView)mItemsLinearLayout.getChildAt(mCurrentPos);
            	try {
            		Bitmap bmp = ImageUtil.rotateImage(imageView.getBitmap(), -90, media.getSource());
            		imageView.setBitmap(bmp);
            	} catch (Exception e) {
            		Log.e(Settings.TAG, "Error: " + e);
            	}
            	return true;
            }
            case MENU_ITEM_ROTATE_RIGHT: {
            	Media media = mMedia.get(mCurrentPos);
            	IndexedImageView imageView = (IndexedImageView)mItemsLinearLayout.getChildAt(mCurrentPos);
            	try {
            		Bitmap bmp = ImageUtil.rotateImage(imageView.getBitmap(), 90, media.getSource());
            		imageView.setBitmap(bmp);
            	} catch (Exception e) {
            		Log.e(Settings.TAG, "Error: " + e);
            	}            	
            	return true;
            }
            case MENU_ITEM_PLAY: {
            	Media media = mMedia.get(mCurrentPos);
            	MediaPlayer mp = new MediaPlayer();
            	try {
            		mp.setDataSource(media.getSource());
            		mp.prepare();
            		mp.start();
            	} catch (Exception e) {
            		Log.e(Settings.TAG, "Cannot play file " + media.getSource() + ": " + e);
            	}
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
    	    	    	    			
    	//Delete data and view
    	mMedia.remove(position);
    	mItemsLinearLayout.removeViewAt(position);
    	
    	//Update current position
    	if (mCurrentPos > position || mCurrentPos == mMedia.size()) {
    		mCurrentPos--;
    	}
    	if (mMedia.size() == 0 || mCurrentPos < 0) 
    		mCurrentPos = -1;
    	
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
	
	public void addExternalMedia(String path) {
		Media m = null;
		path = path.toLowerCase();
		if (path.endsWith(".jpg") || path.endsWith(".png")) {
			m = new Media();
			m.setType(Media.TYPE_IMAGE);
			m.setSource(path);			
		}
		if (m!=null) {
			addMedia(m, false);
		}
	}
	
	private void showNotification(int statusBarIconID, int statusBarTextID,
			int detailedTextID, boolean showIconOnly) {
		
	}
}
