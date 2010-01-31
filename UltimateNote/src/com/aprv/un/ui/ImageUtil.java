package com.aprv.un.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;

public class ImageUtil {
	private static Matrix matrix = new Matrix();
	
	public static Bitmap rotateImage (Bitmap src,float deg) {
		matrix.reset();
		matrix.postRotate(deg);
		return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,true);
	}
	
	public static Bitmap rotateImage (Bitmap src,float deg, String targetFile) throws IOException{
		Bitmap bitmap = rotateImage (src, deg);		
		if (targetFile.toLowerCase().endsWith(".jpg")) {
			saveBitmap(bitmap, CompressFormat.JPEG, targetFile);
		}
		else if (targetFile.toLowerCase().endsWith(".png")) {
			saveBitmap(bitmap, CompressFormat.PNG, targetFile);
		}
		return bitmap;
	}
	
	public static Bitmap resizeImage (Bitmap src, float sx, float sy){
		matrix.reset();
		matrix.postScale(sx, sy);
		return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,true);
	}
	
	public static Bitmap loadImage(String path) {
		File f = new File(path);
		if(f.exists() && f.canRead() && f.isFile()) {
			if(f.getName().contains(".jpg")) {
				Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
				return b;
			}
		}
		return null;
	}
	
	public static void saveBitmap(Bitmap bitmap, CompressFormat format, String targetFile) throws IOException{
		File outputFile = new File(targetFile);    	    	    	    	
    	if (!outputFile.exists()) {	    	    		
    		outputFile.createNewFile();	    	    		
    	}
		FileOutputStream outputStream = new FileOutputStream(outputFile);    	    		
		bitmap.compress(format, 100, outputStream);
		outputStream.flush();
		outputStream.close();		
	}
}
