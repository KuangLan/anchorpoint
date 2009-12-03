package com.aprv.un.ui;

import android.app.ListActivity;
import android.content.ComponentName;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.aprv.un.Constants;
import com.aprv.un.Settings;
import com.aprv.un.db.dao.UltimateNotesDAO;
import com.aprv.un.db.schema.NOTES_TABLE;

public class NoteList extends ListActivity {		
	
	private static final String TAG = "UltimateNote";
	private static final int MENU_ITEM_INSERT = Menu.FIRST;
	private static final int MENU_ITEM_DELETE = Menu.FIRST+1;
	private static final int MENU_ITEM_DELETE_ALL = Menu.FIRST+2;
	private static final int MENU_EDIT_TITLE = Menu.FIRST+3;
	private static final int MENU_CANCEL = Menu.FIRST+4;
		
	/** The index of the title column */	
    private static final int COLUMN_INDEX_TITLE = 1;
    
    private Cursor mCursor;
    private UltimateNotesDAO mDao;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDao = new UltimateNotesDAO(this);	
        mDao.open();
		fillData();
    }
	
	private void fillData() {				
        mCursor = mDao.fetchAllNotes();
        startManagingCursor(mCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NOTES_TABLE.TITLE.COLUMN_NAME};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{android.R.id.text1};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
        	    new SimpleCursorAdapter(this, R.layout.notes_row, mCursor, from, to);
        setListAdapter(notes);                
        
        //Set listener for context menu item
        getListView().setOnCreateContextMenuListener(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_new_note)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        
        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.menu_delete_all)        
        .setIcon(android.R.drawable.ic_menu_delete);

        // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        
        
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NoteList.class), null, intent, 0, null);
		
        return true;
    }
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final boolean haveItems = getListAdapter().getCount() > 0;

        // If there are any notes in the list (which implies that one of
        // them is selected), then we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        if (haveItems) {
            // This is the selected item.
            //Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            // Build menu...  always starts with the EDIT action...
            Intent[] specifics = new Intent[1];
            specifics[0] = new Intent(Constants.ACTION_EDIT_NOTE);
            MenuItem[] items = new MenuItem[1];

            /*
            // ... is followed by whatever other actions are available...
            Intent intent = new Intent((String)null);
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
            menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null, specifics, intent, 0,
                    items);			
			*/
            // Give a shortcut to the edit action.
            if (items[0] != null) {
                items[0].setShortcut('1', 'e');
            }
            
        } else {
            menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
        }
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            // Launch activity to insert a new item        	
            startActivity(new Intent(Constants.ACTION_INSERT_NOTE, getIntent().getData()));        	        	        	
            return true;
        
        case MENU_ITEM_DELETE_ALL:
        	//This is for debugging purpose only!
        	mDao.deleteAllNotes();
        	fillData();
        	return true;
        }        
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		super.onSaveInstanceState(outState);
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Setup the menu header
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

        // Add a menu item to delete the note
        menu.add(0, MENU_EDIT_TITLE, 0, R.string.menu_edit_title);
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
        menu.add(0, MENU_CANCEL, 0, R.string.menu_cancel);
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                // Delete the note that the context menu is for
                //Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                //getContentResolver().delete(noteUri, null, null);
            	mDao.deleteNotes(info.id);
            	fillData();            	
                return true;
            } 
            case MENU_EDIT_TITLE: {
            	Intent i = new Intent(Constants.ACTION_EDIT_TITLE);      
            	i.putExtra(Constants.KEY_NOTE_ID, info.id);
            	startActivity(i);
            	return true;
            }
            case MENU_CANCEL: {
            	return true;
            }
        }
        return false;
    }
    
    

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	fillData();
		super.onResume();
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        
        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
            // The caller is waiting for us to return a note selected by
            // the user.  The have clicked on one, so return it now.
            //setResult(RESULT_OK, new Intent().setData(uri));
        	setResult(RESULT_OK, new Intent());
        } else {
            // Launch activity to view/edit the currently selected item
            //startActivity(new Intent(Intent.ACTION_EDIT, uri));
        	Log.e(Settings.TAG, "id: " + id);
        	startNoteEditor(id);
        }
    }
    
    /**
     * 
     * @param id	row ID of the note to be edited
     */
    private void startNoteEditor(long id) {
    	Intent i = new Intent();
    	i.setAction(Constants.ACTION_EDIT_NOTE);
    	i.putExtra(Constants.KEY_NOTE_ID, id);
    	startActivity(i);
    }
}