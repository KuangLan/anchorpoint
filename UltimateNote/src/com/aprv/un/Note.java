package com.aprv.un;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Note {	
	private String title;
	private Date timeStamp;
	private List<NoteItem> noteItemList;
	
	public Note() {
		this.noteItemList = new ArrayList<NoteItem>();
	}
	
	public String getTitle() { return title; }
	public Date getTimeStamp() { return timeStamp; }
	public List<NoteItem> getNoteItemList() { return noteItemList; }
	
	public void setTitle(String title) { this.title = title; }
	public void setTimeStamp(Date timeStamp) { this.timeStamp = timeStamp; }
	public void setNoteItemList(List<NoteItem> noteItemList) { this.noteItemList = noteItemList; }	
}