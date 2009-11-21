package com.aprv.un;

public class ImageItem extends NoteItem {
	private String path;
	
	public ImageItem(String name, String path) {
		super(name);
		this.path = path;
	}
	
	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }
}