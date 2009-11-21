package com.aprv.un;

public class TextItem extends NoteItem {
	private String text;
	
	public TextItem() {
		super();
	}
	
	public TextItem(String name, String text) {
		super(name);
		this.text = text;
	}
	
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }
}