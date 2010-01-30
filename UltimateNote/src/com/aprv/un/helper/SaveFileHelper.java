package com.aprv.un.helper;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import com.aprv.un.model.Notes;

public class SaveFileHelper {
	
	/**
	 *  Error codes - Please add more error codes to correspond with specific exceptions. All non-success error should have negative values
	 */
	public static final int SUCCESS = 1;
	public static final int ERROR_INVALID_FILE = -1;
	public static final int ERROR_UNKNOWN = -2;
	
	/**
	 * 
	 * @param path	Absolute path to saved file
	 * @return	Note instance if note exist, otherwise return null
	 */
	public static Notes readSavedFile(String path) {
		//TODO	
		return null;
	}
	/**
	 * 
	 * @param note	Note to save
	 * @param path	Absolute path of target file
	 * @return	Error code: 1 if successful
	 */
	public static int saveFile(Notes note, String path) {
		//TODO		
		return ERROR_INVALID_FILE;
	}
	/**
	 * 
	 * @param path
	 * @return	Error code: 1 if successful
	 */
	public static int deleteFile(String path) {
		//TODO
		return ERROR_INVALID_FILE;
	}
	
	/**
	 * <p>This method helps generate a new filename and guarantee unique name if file already exists. 
	 * Try adding _0x postfix until it is unique 
	 * </p>
	 * 
	 * @param prefix
	 * @param dirPath	Absolute path to directory
	 * @param pathSeparator	Path separator, either / or \
	 * @param extension null if not required
	 * @return	prefix_<generated string based on date>
	 */
	public static String generateName(String dirPath, String pathSeparator, String filename, String extension) {
		boolean unique = false;
		int postfix = 0;
		String s = "";				
		String ext = (extension!=null)?extension:"";	//file extension - optional
		
		String path0 = dirPath + pathSeparator + filename;
		String path1 = null;
		
		do {
			path1 = path0 + s + '.' + ext;			
			File file = new File(path1);
			if (!file.exists()) {
				unique = true;
				break;
			} 
			else {
				postfix++;
				s = (postfix<10) ? ("(0" + String.valueOf(postfix) + ")") : String.valueOf("(" + postfix + ")");
			}
		}
		while (!unique);
		
		return path1;
	}
	
	public static String generateDateString() {
		Calendar c = Calendar.getInstance();
		int m = c.get(Calendar.MONTH)+ 1;
		int d = c.get(Calendar.DAY_OF_MONTH);
		String s_m = (m<10)?('0' + String.valueOf(m)):String.valueOf(m);
		String s_d = (d<10)?('0' + String.valueOf(d)):String.valueOf(d);
		String ret = String.valueOf(c.get(Calendar.YEAR)) + s_m + s_d;		
		return ret;
	}
	
	// =============== Begin Test Area ===================//
	/**
	 * Unit Testing - Use code from this point to test 
	 */
	public static void main(String[] args) {
		testGenerateName();
		testGenerateDateString();
	}
	
	private static void testGenerateName() {
		String name = generateName("C:","\\","test","txt");		
		System.out.println("Name: " + name);
	}
	
	private static void testGenerateDateString() {
		System.out.println(generateDateString());
	}
	
	// ================= End Test Area ===================//
}
