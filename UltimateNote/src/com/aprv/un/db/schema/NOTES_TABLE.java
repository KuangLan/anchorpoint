package com.aprv.un.db.schema;

public enum NOTES_TABLE {
	ID("_id", "integer primary key autoincrement"),
	TITLE("title", "text  not null"),
	MODIFIED("modified", "date"),
	CREATED("created", "date"),
	SOURCE("source", "text");

	public static String TABLE_NAME = "NOTE_TABLE";
	public static String[] COLUMNS = new String[NOTES_TABLE.values().length];
	public static String[] COLUMNS_TYPE = new String[NOTES_TABLE.values().length];

	public String COLUMN_NAME;
	public String COLUMN_TYPE;

	private NOTES_TABLE(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}

	static {
		for (int c = 0; c < NOTES_TABLE.values().length; c++) {
			COLUMNS[c] = NOTES_TABLE.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < NOTES_TABLE.values().length; c++) {
			COLUMNS_TYPE[c] = NOTES_TABLE.values()[c].COLUMN_TYPE;
		}
	}

	public String getCOLUMN_TYPE() {
		return COLUMN_TYPE;
	}

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}
}
