package com.aprv.un;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Note {
	private String title;
	private Date timeStamp;
	private List<NoteItem> noteItemList;
	
	public Note(String title) {
		this.title = title;
		this.timeStamp = Calendar.getInstance().getTime();
	}
	
	public String getTitle() { return title; }
	public Date getTimeStamp() { return timeStamp; }
	public List<NoteItem> getNoteItemList() { return noteItemList; }
	
	public void setTitle(String title) { this.title = title; }
	public void setTimeStamp(Date time) { this.timeStamp = time; }
	public void setNoteItemList(List<NoteItem> list) { this.noteItemList = list; }
}
