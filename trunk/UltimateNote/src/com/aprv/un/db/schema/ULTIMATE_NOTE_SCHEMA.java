package com.aprv.un.db.schema;

import java.util.HashMap;
import java.util.Map;

public enum ULTIMATE_NOTE_SCHEMA {
	    NOTES(NOTES_TABLE.TABLE_NAME, 
			  NOTES_TABLE.COLUMNS, 
			  NOTES_TABLE.COLUMNS_TYPE), 
		MEDIA(MEDIA_TABLE.TABLE_NAME,
			  MEDIA_TABLE.COLUMNS, 
			  MEDIA_TABLE.COLUMNS_TYPE), 
		NOTES_MEDIA_UNION(NOTES_MEDIA_UNION_TABLE.TABLE_NAME,
			  NOTES_MEDIA_UNION_TABLE.COLUMNS, 
			  NOTES_MEDIA_UNION_TABLE.COLUMNS_TYPE);
		
		//Schema definition
		public static final String SCHEMA_NAME = "ULTIMATE_NOTE_SCHEMA";
		//Schema table definition
		public final String TABLE_NAME;
		public final String[] TABLE_COLUM;
		public final String[] TABLE_COLUM_TYPE;

		//Convenient list to help getting 
		private static final Map<String, ULTIMATE_NOTE_SCHEMA> TABLE_BY_NAME = 
			new HashMap<String, ULTIMATE_NOTE_SCHEMA>();
		private static final Map<String, String> TABLE_CREATION_LIST = 
			new HashMap<String, String>();
		private static final Map<String, String> TABLE_DROP_LIST = 
			new HashMap<String, String>();
		private static final Map<String, String> TABLE_SELECT = 
			new HashMap<String, String>();

		private ULTIMATE_NOTE_SCHEMA(String name, String[] colum, String[] columType) {
			this.TABLE_NAME = name;
			this.TABLE_COLUM = colum;
			this.TABLE_COLUM_TYPE = columType;
		}

		static {
			for (ULTIMATE_NOTE_SCHEMA t : ULTIMATE_NOTE_SCHEMA.values()) {
				TABLE_BY_NAME.put(t.TABLE_NAME, t);
			}
		}
		static {
			for (ULTIMATE_NOTE_SCHEMA t : ULTIMATE_NOTE_SCHEMA.values()) {
				StringBuffer b = new StringBuffer();
				b.append("create table ");
				b.append(t.TABLE_NAME + " (");
				for (int c = 0; c < t.TABLE_COLUM.length; c++) {
					b.append(t.TABLE_COLUM[c] + " ");
					b.append(t.TABLE_COLUM_TYPE[c] + " ");
					if (c < (t.TABLE_COLUM.length - 1)) {
						b.append(", ");
					}
				}
				b.append(") ");
				TABLE_CREATION_LIST.put(t.TABLE_NAME, b.toString());
			}
		}
		static {
			for (ULTIMATE_NOTE_SCHEMA t : ULTIMATE_NOTE_SCHEMA.values()) {
				StringBuffer b = new StringBuffer();
				b.append("DROP TABLE IF EXISTS ");
				b.append(t.TABLE_NAME);
				TABLE_DROP_LIST.put(t.TABLE_NAME, b.toString());
			}
		}
		static {
			for (ULTIMATE_NOTE_SCHEMA t : ULTIMATE_NOTE_SCHEMA.values()) {
				StringBuffer b = new StringBuffer();
				b.append("select ");
				for (int c = 0; c < t.TABLE_COLUM.length; c++) {
					b.append(t.TABLE_COLUM[c] + " ");
					if (c < (t.TABLE_COLUM.length - 1)) {
						b.append(", ");
					}
				}
				b.append("from ");
				b.append(t.TABLE_NAME);
				TABLE_SELECT.put(t.TABLE_NAME, b.toString());
			}
		}
		public static Map<String, String> getTableCreationList() {
			return TABLE_CREATION_LIST;
			
		}	
		public static Map<String, String> getDropList() {
			return TABLE_DROP_LIST;
		}
		/**
		 * getSelection_List 
		 * 
		 * @return Map<String, String> Schedule name, User Selection
		 * the user schedule selection
		 */
		public static Map<String, String> getTablesList() {
			return TABLE_SELECT;
		}
}
