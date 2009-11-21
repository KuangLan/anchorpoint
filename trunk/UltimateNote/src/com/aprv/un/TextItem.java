package com.aprv.un;

public class TextItem extends NoteItem {
	private String text;
	
	public TextItem(String path, String text) {
		super(path);
		this.text = text;
	}
	
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }
}