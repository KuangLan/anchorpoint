package com.aprv.un.ui;

import com.aprv.un.NoteItem;

public interface IndexedItem {
	public int getIndex();
	public void setIndex(int index);
	public NoteItem getNoteItem();
	public void setNoteItem(NoteItem item);
}
