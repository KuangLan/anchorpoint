package com.aprv.un.db.schema;

public enum MEDIA_TABLE {
	ID("_id", "integer primary key autoincrement"),
	NAME("name", "text"),
	TYPE("type", "text"),
	SOURCE("source", "text"),
	CAPTION("caption", "text");
		
	public static String TABLE_NAME = "MEDIA_TABLE";
	public String COLUMN_NAME;
	public String COLUMN_TYPE;
	
	public static String[] COLUMNS = new String[MEDIA_TABLE.values().length];
	public static String[] COLUMNS_TYPE = new String[MEDIA_TABLE.values().length];
	
	private MEDIA_TABLE(String column_name, String column_type) {
		COLUMN_NAME = column_name;
		COLUMN_TYPE = column_type;
	}
	

	static {
		for (int c = 0; c < MEDIA_TABLE.values().length; c++) {
			COLUMNS[c] = MEDIA_TABLE.values()[c].COLUMN_NAME;
		}

		for (int c = 0; c < MEDIA_TABLE.values().length; c++) {
			COLUMNS_TYPE[c] = MEDIA_TABLE.values()[c].COLUMN_TYPE;
		}
	}

	public String getCOLUMN_TYPE() {
		return COLUMN_TYPE;
	}

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}
}
