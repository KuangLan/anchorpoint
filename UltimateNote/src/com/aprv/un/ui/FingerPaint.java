/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aprv.un.ui;

import java.io.File;
import java.io.FileOutputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.aprv.un.Settings;
import com.aprv.un.helper.SaveFileHelper;

public class FingerPaint extends GraphicsActivity
        implements ColorPickerDialog.OnColorChangedListener {    

	private MyView view;
	private String saveFile;
	private static final int DIALOG_SAVE = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new MyView(this));
        
      //Pass settings from bundle
        
        boolean isNew = false;
        Intent intent = getIntent();
        if (intent != null) {        	        	
        	saveFile = intent.getStringExtra(Settings.KEY_SAVED_FILE_LOCATION);
        	if (saveFile!=null && saveFile.equals("")) {
        		String saveDir = intent.getStringExtra(Settings.KEY_SAVED_MEDIA_LOCATION);
        		saveFile = setupTargetFile(saveDir);
        		isNew = true;
        	}
        } 
        
        if (isNew) {
        	view = new MyView(this);
        } else {
        	view = new MyView(this, saveFile);
        }
        
        setContentView(view);
        

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }
    
    private Paint       mPaint;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    public class MyView extends View {
        
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        
        public MyView(Context c) {
            super(c);
            
            mBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.WHITE);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }
        
        public MyView(Context c, String filePath) {
        	super(c);
        	
        	Bitmap bm = BitmapFactory.decodeFile(filePath);        	
        	mBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);        	        	        	
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);          	        	
                        
            //Draw onto the newly created bitmap
        	Drawable drawable = new BitmapDrawable(bm);
        	drawable.setBounds(0, 0, bm.getWidth(), bm.getHeight());        	        	
        	drawable.draw(mCanvas);        	
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);
            
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            
            canvas.drawPath(mPath, mPaint);
        }
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
        
        public Bitmap getBitmap() {        	
        	return mBitmap;
        }
    }
    
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;
    private static final int SAVE_ID = Menu.FIRST + 5;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
        menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
        menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
        menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');
        menu.add(0, SAVE_ID, 0, "Save & Quit");

        /****   Is this the mechanism to extend with filter effects?
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
                              Menu.ALTERNATIVE, 0,
                              new ComponentName(this, NotesList.class),
                              null, intent, 0, null);
        *****/
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                        PorterDuff.Mode.CLEAR));
                return true;
            case SRCATOP_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                    PorterDuff.Mode.SRC_ATOP));
                mPaint.setAlpha(0x80);
                return true;
            case SAVE_ID:            	         	
        		boolean save = save();	  
        		prepareToFinish(save);
    			finish();	        		
            	return save;            	
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
		{
		case DIALOG_SAVE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you want to save?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean save = save();						
						prepareToFinish(save);
						FingerPaint.this.finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						prepareToFinish(false);
						FingerPaint.this.finish();
					}
				});
			
			AlertDialog alert = builder.create();
			return alert;
		}
		return super.onCreateDialog(id);
	}

	private boolean save() {
		Log.i(Settings.TAG, "FingerPaint.save() saving to " + saveFile);
		try 
		{
			File outputFile = new File(saveFile);    	    	    	    	
	    	if (!outputFile.exists()) {	    	    		
	    		outputFile.createNewFile();
	    	}
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			if (saveFile.toLowerCase().endsWith(".png")) {
				view.getBitmap().compress(CompressFormat.PNG, 100, outputStream);
			}
			else if (saveFile.toLowerCase().endsWith(".jpg")) {
				view.getBitmap().compress(CompressFormat.JPEG, 100, outputStream);
			}
			outputStream.flush();
			outputStream.close();
			return true;
		}
		catch (Exception e) 
		{
			Log.e(Settings.TAG, "FingerPaint.save() error: " + e);
			return false;
		}		
	}
	
	private void prepareToFinish(boolean save) {
		if (save)
		{
    		Intent i = new Intent();
			Bundle data = new Bundle();
			data.putString(Settings.KEY_PATH, saveFile);			
			i.putExtras(data);			
			setResult(RESULT_OK, i);		    					    			
		}
		else
		{
			setResult(RESULT_CANCELED);
		}
	}
	
	private String setupTargetFile(String savedMediaLocation) {    	
		File dir = new File(savedMediaLocation);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String path = SaveFileHelper.generateName(savedMediaLocation, "/", "Painting_" + SaveFileHelper.generateDateString(), "PNG");
		return path;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub		
		switch (keyCode) 
		{
		case KeyEvent.KEYCODE_BACK:
			showDialog(DIALOG_SAVE);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
