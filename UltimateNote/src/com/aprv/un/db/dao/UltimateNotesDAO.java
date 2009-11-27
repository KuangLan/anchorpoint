package com.aprv.un.db.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.aprv.un.db.schema.MEDIA_TABLE;
import com.aprv.un.db.schema.NOTES_MEDIA_UNION_TABLE;
import com.aprv.un.db.schema.NOTES_TABLE;
import com.aprv.un.model.Media;
import com.aprv.un.model.Notes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UltimateNotesDAO extends AbstractDao {
	private static final String NOTESTAG = "UltimateNotesDAO";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	/**
	 * Constructor - takes the context to allow the database to be opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public UltimateNotesDAO(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance of the database. If
	 * it cannot be created, throw an exception to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public UltimateNotesDAO open() throws SQLException {
		if (mDb == null) {
			mDbHelper = getDataBaseHelper(mCtx);
			mDb = mDbHelper.getWritableDatabase();
		}
		return this;
	}

	public void close() {
		mDb.close();
		mDbHelper.close();
	}

	/**
	 * Create a new Schedule using the periodsNames and periodsSelection provided.
	 * 
	 * If the Schedule is successfully created return the new ScheduleId for that Schedule,
	 * otherwise return a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public long createNotes(Notes notes, List<Media> mediaList) {
		// open();
		ContentValues initialNotesValues = new ContentValues();
		Date stamp = new Date();
		//initialValues.put(NOTES_TABLE.CREATED.getCOLUMN_NAME(), stamp.toGMTString());
		initialNotesValues.put(NOTES_TABLE.TITLE.getCOLUMN_NAME(), notes.getTitle());
		initialNotesValues.put(NOTES_TABLE.CREATED.getCOLUMN_NAME(), dateTimeFormat.format(stamp));
		//initialNotesValues.put(NOTES_TABLE.MODIFIED.getCOLUMN_NAME(), notes.getModified());
		initialNotesValues.put(NOTES_TABLE.SOURCE.getCOLUMN_NAME(), notes.getSource());

		long rowId = mDb.insert(NOTES_TABLE.TABLE_NAME, null, initialNotesValues);
		int size = mediaList.size();
		for (Media media : mediaList) {
			ContentValues initialMediaValues = new ContentValues();
			initialMediaValues.put(MEDIA_TABLE.NAME.getCOLUMN_NAME(), media.getName());
			initialMediaValues.put(MEDIA_TABLE.TYPE.getCOLUMN_NAME(), media.getType());
			initialMediaValues.put(MEDIA_TABLE.SOURCE.getCOLUMN_NAME(), media.getSource());
			initialMediaValues.put(MEDIA_TABLE.CAPTION.getCOLUMN_NAME(), media.getCaption());
			long mediaRowId = mDb.insert(MEDIA_TABLE.TABLE_NAME, null, initialNotesValues);
			createNotesMediaUnion(rowId, mediaRowId);
		}
		return rowId;
	}

	public long updateNotes(long rowId, Notes notes, List<Media> mediaList) {
		mDb.delete(NOTES_MEDIA_UNION_TABLE.TABLE_NAME, NOTES_MEDIA_UNION_TABLE.NOTE_ID.getCOLUMN_NAME() + "=" + rowId, null);
		ContentValues initialNotesValues = new ContentValues();
		Date stamp = new Date();
		initialNotesValues.put(NOTES_TABLE.TITLE.getCOLUMN_NAME(), notes.getTitle());
		initialNotesValues.put(NOTES_TABLE.CREATED.getCOLUMN_NAME(), dateTimeFormat.format(notes.getCreated()));
		initialNotesValues.put(NOTES_TABLE.MODIFIED.getCOLUMN_NAME(), dateTimeFormat.format(stamp));
		initialNotesValues.put(NOTES_TABLE.SOURCE.getCOLUMN_NAME(), notes.getSource());
		mDb.update(NOTES_TABLE.TABLE_NAME, initialNotesValues, NOTES_TABLE.ID.getCOLUMN_NAME() + "=" + rowId, null);
		for (Media media : mediaList) {
			ContentValues initialMediaValues = new ContentValues();
			initialMediaValues.put(MEDIA_TABLE.NAME.getCOLUMN_NAME(), media.getName());
			initialMediaValues.put(MEDIA_TABLE.TYPE.getCOLUMN_NAME(), media.getType());
			initialMediaValues.put(MEDIA_TABLE.SOURCE.getCOLUMN_NAME(), media.getSource());
			initialMediaValues.put(MEDIA_TABLE.CAPTION.getCOLUMN_NAME(), media.getCaption());
			long mediaRowId = mDb.insert(MEDIA_TABLE.TABLE_NAME, null, initialNotesValues);
			createNotesMediaUnion(rowId, mediaRowId);
		}
		return rowId;
	}

	public void deleteAllNotes() {
		mDb.delete(NOTES_TABLE.TABLE_NAME, null, null);
		mDb.delete(NOTES_MEDIA_UNION_TABLE.TABLE_NAME, null, null);
	}

	public void deleteNotes(long rowId) {
		mDb.delete(NOTES_TABLE.TABLE_NAME, NOTES_TABLE.ID.getCOLUMN_NAME() + "=" + rowId, null);
		mDb.delete(NOTES_MEDIA_UNION_TABLE.TABLE_NAME, NOTES_MEDIA_UNION_TABLE.NOTE_ID.getCOLUMN_NAME() + "=" + rowId, null);

	}

	public void dumpDataBase() {
		super.dumpDataBase(mDb);
	}

	private long createNotesMediaUnion(long noteId, long mediaId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(NOTES_MEDIA_UNION_TABLE.NOTE_ID.getCOLUMN_NAME(), noteId);
		initialValues.put(NOTES_MEDIA_UNION_TABLE.MEDIA_ID.getCOLUMN_NAME(), mediaId);
		return mDb.insert(NOTES_MEDIA_UNION_TABLE.TABLE_NAME, null, initialValues);
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllNotes() {
		return mDb.query(NOTES_TABLE.TABLE_NAME, new String[] { NOTES_TABLE.ID.getCOLUMN_NAME(), NOTES_TABLE.TITLE.getCOLUMN_NAME(),
				NOTES_TABLE.SOURCE.getCOLUMN_NAME(), NOTES_TABLE.CREATED.getCOLUMN_NAME(), NOTES_TABLE.MODIFIED.getCOLUMN_NAME() }, null, null,	null, null, null);
	}
	
	public Notes getNotes(long rowId) throws SQLException, ParseException {
		Cursor mCursor = mDb.query(true, NOTES_TABLE.TABLE_NAME, new String[] { NOTES_TABLE.ID.getCOLUMN_NAME(), NOTES_TABLE.TITLE.getCOLUMN_NAME(),
				NOTES_TABLE.SOURCE.getCOLUMN_NAME(), NOTES_TABLE.CREATED.getCOLUMN_NAME(), NOTES_TABLE.MODIFIED.getCOLUMN_NAME() }, 
				NOTES_TABLE.ID.getCOLUMN_NAME() + "=" + rowId, null, null, null, null, null);

		Notes notes = new Notes();

		while (mCursor.moveToNext()) {
			Long id = mCursor.getLong(mCursor.getColumnIndex(NOTES_TABLE.ID.getCOLUMN_NAME()));
			String title = mCursor.getString(mCursor.getColumnIndex(NOTES_TABLE.TITLE.getCOLUMN_NAME()));
			String source = mCursor.getString(mCursor.getColumnIndex(NOTES_TABLE.SOURCE.getCOLUMN_NAME()));
			String created = mCursor.getString(mCursor.getColumnIndexOrThrow(NOTES_TABLE.CREATED.getCOLUMN_NAME()));
			String modified = mCursor.getString(mCursor.getColumnIndexOrThrow(NOTES_TABLE.MODIFIED.getCOLUMN_NAME()));

			notes.setId(id);
			notes.setTitle(title);
			notes.setSource(source);
			if(created != null && created.trim().length() != 0)
				notes.setCreated(dateTimeFormat.parse(created));
			if(modified != null && modified.trim().length() != 0)
				notes.setModified(dateTimeFormat.parse(modified));
		}
		mCursor.close();
		return notes;
	}
	
	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 * @throws ParseException 
	 */
	public List<Notes> getAllNotes() throws ParseException {
		Cursor mCursor = fetchAllNotes();
		List<Notes> notesList = new ArrayList<Notes>();
		while (mCursor.moveToNext()) {
			Long id = mCursor.getLong(mCursor.getColumnIndex(NOTES_TABLE.ID.getCOLUMN_NAME()));
			String title = mCursor.getString(mCursor.getColumnIndex(NOTES_TABLE.TITLE.getCOLUMN_NAME()));
			String created = mCursor.getString(mCursor.getColumnIndexOrThrow(NOTES_TABLE.CREATED.getCOLUMN_NAME()));
			String modified = mCursor.getString(mCursor.getColumnIndexOrThrow(NOTES_TABLE.MODIFIED.getCOLUMN_NAME()));
			String source = mCursor.getString(mCursor.getColumnIndex(NOTES_TABLE.SOURCE.getCOLUMN_NAME()));
			Notes notes = new Notes(id);
			notes.setTitle(title);
			notes.setSource(source);
			if(created != null && created.trim().length() != 0)
				notes.setCreated(dateTimeFormat.parse(created));
			if(modified != null && modified.trim().length() != 0)
				notes.setModified(dateTimeFormat.parse(modified));
			
			notesList.add(notes);
		}
		mCursor.close();
		return notesList;
	}
	

	/**
	 * Return a Cursor positioned at the Time Entrie that matches the given rowId
	 * 
	 * @param rowId
	 *            ID of notes to retrieve
	 * @return Map<Long, Media> of Media for Notes rowId, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Map<Long, Media> fetchMediaEntriesByNoteId(long rowId) throws SQLException {


		List<Long> mediaEntryIds = getMediaEntryList(rowId);

		Map<Long, Media> mediaMap = getMediaMap(mediaEntryIds);

		return mediaMap;
	}

	/**
	 * 
	 * @param rowId
	 * @return
	 */
	private List<Long> getMediaEntryList(long rowId) {
		Cursor mCursor = mDb.query(true, NOTES_MEDIA_UNION_TABLE.TABLE_NAME, new String[] { NOTES_MEDIA_UNION_TABLE.MEDIA_ID.getCOLUMN_NAME() },
				NOTES_MEDIA_UNION_TABLE.NOTE_ID.getCOLUMN_NAME() + "=" + rowId, null, null, null, null, null);

		List<Long> mediaEntryIds = new ArrayList<Long>();

		while (mCursor.moveToNext()) {

			Long id = mCursor.getLong(mCursor.getColumnIndex(NOTES_MEDIA_UNION_TABLE.MEDIA_ID.getCOLUMN_NAME()));

			mediaEntryIds.add(id);
		}
		mCursor.close();
		return mediaEntryIds;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Long, Media> getMediaMap(List<Long> rowIds) {
		Cursor mCursor = mDb.query(true, MEDIA_TABLE.TABLE_NAME, new String[] { MEDIA_TABLE.ID.getCOLUMN_NAME(),
				MEDIA_TABLE.NAME.getCOLUMN_NAME(), MEDIA_TABLE.TYPE.getCOLUMN_NAME(), MEDIA_TABLE.SOURCE.getCOLUMN_NAME(),
				MEDIA_TABLE.CAPTION.getCOLUMN_NAME() }, MEDIA_TABLE.ID.getCOLUMN_NAME() + "in " + padRowId(rowIds) , null, null, null, null, null);
		Map<Long, Media> mediaMap = new HashMap<Long, Media>();
		while (mCursor.moveToNext()) {

			Long id = mCursor.getLong(mCursor.getColumnIndex(MEDIA_TABLE.ID.getCOLUMN_NAME()));
			String name = mCursor.getString(mCursor.getColumnIndex(MEDIA_TABLE.NAME.getCOLUMN_NAME()));
			String type =  mCursor.getString(mCursor.getColumnIndex(MEDIA_TABLE.TYPE.getCOLUMN_NAME()));
			String source = mCursor.getString(mCursor.getColumnIndex(MEDIA_TABLE.SOURCE.getCOLUMN_NAME()));
			String caption = mCursor.getString(mCursor.getColumnIndex(MEDIA_TABLE.CAPTION.getCOLUMN_NAME()));
			Media media = new Media(id);
			media.setName(name);
			media.setType(type);
			media.setSource(source);
			media.setCaption(caption);
			mediaMap.put(id, media);
		}
		mCursor.close();
		return mediaMap;
	}
	
	private String padRowId(List<Long> rowIds) {

		char c = ',';
		StringBuffer str = new StringBuffer();
		str.append('(');

		for (Long rowId : rowIds) {
			str.append(rowId);
			str.append(c);
		}
		str.setCharAt(str.length()-1, ')');
		//str = str.replace(str.length()-1, str.length(), ")");
		
		return str.toString();
	}
	
	private String getLocalDateTime(String dateTime) {
		Date utcDate = null;
		try {
			utcDate = dateTimeFormat.parse(dateTime);
		}
		catch (ParseException e) {
			Log.w(NOTESTAG, "Parsing ISO8601 datetime failed", e);
			//return dateTimeFormat.format(new Date());
		}

		long when = utcDate.getTime();
		int flags = 0;
		flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
		flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
		flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
		flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

		String finalDateTime = android.text.format.DateUtils.formatDateTime(mCtx,
				      when + TimeZone.getDefault().getOffset(when), flags);
		return finalDateTime;


	}

}
