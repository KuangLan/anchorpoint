package com.aprv.un;

public class NoteItem {
	private String name;	//name must be unique in a note
	
	public NoteItem(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}