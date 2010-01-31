package com.aprv.un;

public class Settings {	
	public static String KEY_SAVED_FILE_LOCATION = "com.arpv.un.settings.file_location";
	public static String KEY_SAVED_MEDIA_LOCATION = "com.arpv.un.settings.media_location";
	public static String KEY_PATH = "com.arpv.un.path";
	public static String KEY_SPECIAL = "com.arpv.un.special";
	public static String KEY_BITMAP = "BITMAP";
	public static String TAG = "UltimateNote";
	
	private String savedFileLocation;
	private String savedMediaLocation;
	
	public Settings() {
		
	}
	
	public Settings(String savedFileLocation, String savedMediaLocation) {
		this.savedFileLocation = savedFileLocation;
		this.savedMediaLocation = savedMediaLocation;
	}
	
	public String getSavedFileLocation() { return savedFileLocation; }
	public String getSavedMediaLocation() { return savedMediaLocation; }
	
	public void setSavedFileLocation(String savedFileLocation) { this.savedFileLocation = savedFileLocation; }
	public void setSavedMediaLocation(String savedMediaLocation) { this.savedMediaLocation = savedMediaLocation; }
}
