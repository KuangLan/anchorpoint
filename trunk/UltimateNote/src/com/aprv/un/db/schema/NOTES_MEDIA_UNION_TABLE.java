package com.aprv.un.db.schema;

public enum NOTES_MEDIA_UNION_TABLE {
	ID("_id", "integer primary key autoincrement"),
	NOTE_ID("_noteId", "integer not null"),
	MEDIA_ID("_mediaId", "integer not null");

	
	public static String TABLE_NAME = "NOTES_MEDIA_UNION_TABLE";
	public String COLUMN_NAME;
	public String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[NOTES_MEDIA_UNION_TABLE.values().length];
	public static String[] COLUMNS_TYPE = new String[NOTES_MEDIA_UNION_TABLE.values().length];
	
	private NOTES_MEDIA_UNION_TABLE(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	

	static {
		for (int c = 0; c < NOTES_MEDIA_UNION_TABLE.values().length; c++) {
			COLUMNS[c] = NOTES_MEDIA_UNION_TABLE.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < NOTES_MEDIA_UNION_TABLE.values().length; c++) {
			COLUMNS_TYPE[c] = NOTES_MEDIA_UNION_TABLE.values()[c].COLUMN_TYPE;
		}
	}

	public String getCOLUMN_TYPE() {
		return COLUMN_TYPE;
	}

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}
}
