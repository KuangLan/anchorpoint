package com.aprv.un.model;

import java.util.Calendar;
import java.util.Date;

public class Media {
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_AUDIO = "audio";
	public static final String TYPE_PAINTING = "painting";
	
	private long id;
	private String name;
	private String type;
	private String source;
	private String caption;
	
	private static final String[] EXTENSION_AUDIO = {".3gpp",".wav", ".mp3"};
	private static final String[] EXTENSION_IMAGE = {".jpg",".png"};
	
	public Media() {
		
	}
	public Media(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public static String getMediaType(String filename) {
		filename = filename.toLowerCase();
		for (int i=0; i<EXTENSION_AUDIO.length; i++) {
			if (filename.endsWith(EXTENSION_AUDIO[i])) {
				return TYPE_AUDIO;
			}
		}
		for (int i=0; i<EXTENSION_IMAGE.length; i++) {
			if (filename.endsWith(EXTENSION_IMAGE[i])) {
				return TYPE_IMAGE;
			}
		}
		return "";
	}
}
