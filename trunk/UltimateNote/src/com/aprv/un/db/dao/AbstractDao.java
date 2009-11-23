package com.aprv.un.db.dao;

/*
 * Copyright (C) 2009 Neguentropia Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aprv.un.db.schema.NOTES_TABLE;
import com.aprv.un.db.schema.ULTIMATE_NOTE_SCHEMA;


/**
 * The AbstractDao class provide commons functionalities to the application Dao.
 * 
 * This AbstractDao provide one important inner class and one convenient method.
 * 
 * Creating many instance of DatabaseHelper carry out to many table creation process in your
 * application life cycle. This is a common mistake when you use many tables in Android, and it's
 * cause a lots of problems. That is why I put the inner class DatabaseHelper in the AbstractDao.
 * 
 * This DatabaseHelper is responsible to create and populate the database. It's important to share
 * this inner class with all Dao, because, that way, we are sure that the database creation happen
 * only one time in the application life cycle. So, in this process, you have to create all the
 * tables in the first DatabaseHelper onCreate(SQLiteDatabase db) call.
 * 
 * The dumpDataBase method print the content of you database in the LogCat console. This method is
 * very convenient for debugging your application. Thats it! You don't need anymore to do sqlite
 * command line to see the content of you database.
 * 
 */
public abstract class AbstractDao {

	protected static final String DATABASE_NAME = "ultimatenotes.db";
	protected static final int DATABASE_VERSION = 1;
	//DATEFORMAT ISO8601
	protected static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//protected static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	private static DatabaseHelper databaseHelper;

	public AbstractDao() {
		super();
	}

	/**
	 * Table Creation Helper
	 * 
	 */
	protected static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
			//populateTimeSchedules(db);
		}

		private void createTables(SQLiteDatabase db) {
			for (String ct : ULTIMATE_NOTE_SCHEMA.getTableCreationList().keySet()) {
				db.execSQL(ULTIMATE_NOTE_SCHEMA.getTableCreationList().get(ct));
			}
		}

		private void dropTables(SQLiteDatabase db) {
			for (String ct : ULTIMATE_NOTE_SCHEMA.getDropList().keySet()) {
				db.execSQL(ULTIMATE_NOTE_SCHEMA.getDropList().get(ct));
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(ULTIMATE_NOTE_SCHEMA.SCHEMA_NAME, "Upgrading database from version " + oldVersion + " to " + newVersion
							+ ", which will destroy all old data");
			dropTables(db);
			onCreate(db);
		}
	}

	/**
	 * 
	 * @param mCtx
	 * @return
	 */
	protected DatabaseHelper getDataBaseHelper(Context mCtx) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(mCtx);
		}
		return databaseHelper;
	}

//	private static void populateTimeSchedules(SQLiteDatabase db) {
//		String[] periodsNames = timePeriods;
//		for (String tp : periodsNames) {
//			createTimeSchedule(db, tp);
//		}
//	}

	private static long createNotes(SQLiteDatabase db, String title) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(NOTES_TABLE.TITLE.getCOLUMN_NAME(), title);
		return db.insert(NOTES_TABLE.TABLE_NAME, null, initialValues);
	}

	/**
	 * 
	 * @param db
	 */
	protected void dumpDataBase(SQLiteDatabase db) {
		ULTIMATE_NOTE_SCHEMA[] t = ULTIMATE_NOTE_SCHEMA.values();
		Map<String, String> sl = ULTIMATE_NOTE_SCHEMA.getTablesList();
		System.out
				.println("********************************************************************************************************************************************************************************************");

		for (int tIdx = 0; tIdx < ULTIMATE_NOTE_SCHEMA.values().length; tIdx++) {
			System.out
					.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("table : " + t[tIdx].TABLE_NAME);
			System.out
					.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			StringBuffer sb = new StringBuffer();
			for (int c = 0; c < t[tIdx].TABLE_COLUM.length; c++) {
				sb.append(padColumn(t[tIdx].TABLE_COLUM[c] + " | "));
			}
			sb.append("\n");

			Cursor r = db.rawQuery(sl.get(t[tIdx].TABLE_NAME), null);
			while (r.moveToNext()) {
				for (int i = 0; i < r.getColumnCount(); i++) {
					sb.append(padColumn(r.getString(i) + " | "));
				}

				sb.append("\n");
			}
			r.close();
			System.out.println(sb.toString());

			System.out
					.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
	}

	/**
	 * 
	 */
	private String padColumn(String s) {
		int n = 20;
		boolean paddingLeft = true;
		char c = ' ';
		StringBuffer str = new StringBuffer(s);
		int strLength = str.length();
		if (n > 0 && n > strLength) {
			for (int i = 0; i <= n; i++) {
				if (paddingLeft) {
					if (i < n - strLength)
						str.insert(0, c);
				} else {
					if (i >= strLength)
						str.append(c);
				}
			}
		}
		return str.toString();
	}
}