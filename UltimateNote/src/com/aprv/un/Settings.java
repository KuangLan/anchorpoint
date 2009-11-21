package com.aprv.un;

public class Settings {	
	public static String KEY_SAVED_FILE_LOCATION = "KEY_SAVED_FILE_LOCATION";
	public static String KEY_SAVED_MEDIA_LOCATION = "KEY_SAVED_MEDIA_LOCATION";
	
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
