/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * -------------------
 * Extends Android ImageView to include pinch zooming and panning.
 */

package com.mmm.soncek;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	ImageActivity tata;
    Matrix matrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int SWIPEfR = 3;
    static final int SWIPEfL = 4;
    int mode = NONE;
    
    static final float swipeMin=0.05f;
    static final float swipeMax=0.15f;
    
    static final int ctran=0x00000000;
    static final int copaq=0xFF000000;
    // Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    PointF swipeStart = new PointF();
    float minScale = 1f;
    float maxScale = 3f;
    float[] m;
    
    float redundantXSpace, redundantYSpace;
    
    float width, height;
    static final int CLICK = 3;
    float saveScale = 1f;
    float right, bottom, origWidth, origHeight, bmWidth, bmHeight;
    
    // set crop or fit
    boolean fit=false;
    boolean onMeasureDone=false;
    int measuredx, measuredy;
    
    ScaleGestureDetector mScaleDetector;
    
    public Context context;
	private ScaleListener sl;
	public Bitmap bmap;
	private Bitmap saveRef;

    public TouchImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }
    
    public TouchImageView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	sharedConstructing(context);
    }

    public void setActivity(ImageActivity s) {
    	tata=s;
    }
    
    public void setScale(float scale){
    	sl.setScale(scale);
    	setImageMatrix(matrix);
        invalidate();
	}
    
    public void sharedConstructing(Context context) {
    	super.setClickable(true);
    	
    	bmWidth=640f;
    	bmHeight=480f;
        this.context = context;
        sl= new ScaleListener();
        mScaleDetector = new ScaleGestureDetector(context, sl);
        matrix.setTranslate(1f, 1f);
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {
        	
            public boolean onTouch(View v, MotionEvent event) {
            	mScaleDetector.onTouchEvent(event);

            	matrix.getValues(m);
            	float x = m[Matrix.MTRANS_X];
            	float y = m[Matrix.MTRANS_Y];
            	PointF curr = new PointF(event.getX(), event.getY());
            	
            	switch (event.getAction()) {
	            	case MotionEvent.ACTION_DOWN:
	                    last.set(event.getX(), event.getY());
	                    start.set(last);
	                    //mode = DRAG;
	                    if (event.getX()>(1-swipeMin)*width && mode!=ZOOM){
	                    	swipeStart.set(event.getX(), event.getY());
	                    	mode=SWIPEfR;
	                    	drawGradRigth(0.5f);
	                    	//setBmAlpha(0.5f);
	                    }
	                    else if (event.getX()<swipeMin*width && mode!=ZOOM){
	                    	swipeStart.set(event.getX(), event.getY());
	                    	mode=SWIPEfL;
	                    	drawGradLeft(0.5f);
	                    }
	                    else
	                    	mode = DRAG;
	                    Log.d("touch","down");
	                    break;
	            	case MotionEvent.ACTION_MOVE:
	            		if (mode == DRAG) {
	            			float deltaX = curr.x - last.x;
	            			float deltaY = curr.y - last.y;
	            			float scaleWidth = Math.round(origWidth * saveScale);
	            			float scaleHeight = Math.round(origHeight * saveScale);
            				if (scaleWidth < width) {
	            				deltaX = 0;
	            				if (y + deltaY > 0)
		            				deltaY = -y;
	            				else if (y + deltaY < -bottom)
		            				deltaY = -(y + bottom); 
            				} else if (scaleHeight < height) {
	            				deltaY = 0;
	            				if (x + deltaX > 0)
		            				deltaX = -x;
		            			else if (x + deltaX < -right)
		            				deltaX = -(x + right);
            				} else {
	            				if (x + deltaX > 0)
		            				deltaX = -x;
		            			else if (x + deltaX < -right)
		            				deltaX = -(x + right);
		            			
	            				if (y + deltaY > 0)
		            				deltaY = -y;
		            			else if (y + deltaY < -bottom)
		            				deltaY = -(y + bottom);
	            			}
                        	matrix.postTranslate(deltaX, deltaY);
                        	last.set(curr.x, curr.y);
	                    }
	            		if (mode==SWIPEfR) {
	            			float deltaX = curr.x - swipeStart.x;
	            			float r=.5f+Math.abs(deltaX)/(swipeMax*width)/2;
	            			if (r>1) r=1;
	            			drawGradCancel();
	            			drawGradRigth(r);
	            		}
	            		if (mode==SWIPEfL) {
	            			float deltaX = curr.x - swipeStart.x;
	            			float r=.5f+Math.abs(deltaX)/(swipeMax*width)/2;
	            			if (r>1) r=1;
	            			drawGradCancel();
	            			drawGradLeft(r);
	            		}
	            		break;
	            		
	            	case MotionEvent.ACTION_UP:
	            		
	            		//mode = NONE;
	            		int xDiff = (int) Math.abs(curr.x - start.x);
	                    int yDiff = (int) Math.abs(curr.y - start.y);
	                    if (xDiff < CLICK && yDiff < CLICK)
	                        performClick();
	                    
	                    
	            		float deltaX = curr.x - swipeStart.x;
            			float deltaY = curr.y - swipeStart.y;
	            		if (mode==SWIPEfR && Math.abs(deltaX)>swipeMax*width) {        		
	            			tata.forward();
	            		} else drawGradCancel();
	            		
	            		if (mode==SWIPEfL && Math.abs(deltaX)>swipeMax*width) {          		
	            			tata.back();
	            		} else drawGradCancel();
	            		
	            		mode = NONE;
	            		float[] mtrx=new float[9];
	            		matrix.getValues(mtrx);
	            		tata.mSeekBar.bringToFront();
	            		
	                    break;
	            		
	            	case MotionEvent.ACTION_POINTER_UP:
	            		mode = NONE;
	            		break;
            	}
                setImageMatrix(matrix);
                invalidate();
                return true; // indicate event was handled
            }

        });
    }

   
    private void drawGradRigth(float n) {
    	Canvas canvas=new Canvas(bmap);
    	float[] mtrx=new float[9];
		matrix.getValues(mtrx);
		
		int w = (int)(swipeMax*width/mtrx[Matrix.MSCALE_X]);
    	int x = (int)((-mtrx[Matrix.MTRANS_X]+width)/mtrx[Matrix.MSCALE_X]);
    	int y = (int)((-mtrx[Matrix.MTRANS_Y])/mtrx[Matrix.MSCALE_Y]);
    	int h = (int)((height)/mtrx[Matrix.MSCALE_X]);
    	int r = (int)((h*h/4+w*w)/(2*w));
    	
    	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    	
    	int radCol[] = {Color.argb((int)(255*n/2), 0, 0,0),ctran};
    	float radP[] = {1f-(float)w/(float)r,1f};
    	
    	Shader radial = new RadialGradient(x-w+r, y+h/2, r, 
    			radCol,radP,TileMode.CLAMP);
		
    	p.setShader(radial);
    	canvas.drawCircle(x-w+r, y+h/2, r, p);
    	
    	
    }
   
    private void drawGradLeft(float n) {
    	Canvas canvas=new Canvas(bmap);
    	float[] mtrx=new float[9];
		matrix.getValues(mtrx);
		
		int w = (int)(swipeMax*width/mtrx[Matrix.MSCALE_X]);
    	int x = (int)((-mtrx[Matrix.MTRANS_X])/mtrx[Matrix.MSCALE_X]);
    	int y = (int)((-mtrx[Matrix.MTRANS_Y])/mtrx[Matrix.MSCALE_Y]);
    	int h = (int)((height)/mtrx[Matrix.MSCALE_X]);
    	int r = (int)((h*h/4+w*w)/(2*w));
    	
    	Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    	int radCol[] = {Color.argb((int)(255*n/2), 0, 0,0),ctran};
    	float radP[] = {1f-(float)w/(float)r,1f};
    	
    	Shader radial = new RadialGradient(x+w-r, y+h/2, r, 
    			radCol,radP,TileMode.CLAMP);
    	
    	p.setShader(radial);
    	canvas.drawCircle(x+w-r, y+h/2, r, p);
    }
    
    private void drawGradCancel() {
    	if(bmap != null) {
	    	bmap = saveRef.copy(Bitmap.Config.ARGB_8888, true);
	    	super.setImageBitmap(bmap);
    	}
    }
    @Override
    public void setImageBitmap(Bitmap bm) { 
    	
        if(bm != null) {
        	saveRef=bm;
        	bmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        	bmWidth = bm.getWidth();
        	bmHeight = bm.getHeight();
        	
        	super.setImageBitmap(bmap);
        }
    }
    
    public void setMaxZoom(float x)
    {
    	maxScale = x;
    }
   
    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    	@Override
    	public boolean onScaleBegin(ScaleGestureDetector detector) {
    		mode = ZOOM;
    		return true;
    	}
    	
    	public void setScale(float scale)
    	{
    		scaleRoutine(scale,width/2,height/2);
    	}
    	
    	public void scaleRoutine(float detScFac,float detFocX,float detFocY)
    	{
    		Log.d("scale","sc "+detScFac+" "+detFocX+" "+detFocY);
    		float mScaleFactor = detScFac;
		 	float origScale = saveScale;
	        saveScale *= mScaleFactor;
	        if (saveScale > maxScale) {
	        	saveScale = maxScale;
	        	mScaleFactor = maxScale / origScale;
	        } else if (saveScale < minScale) {
	        	saveScale = minScale;
	        	mScaleFactor = minScale / origScale;
	        }
        	right = width * saveScale - width - (2 * redundantXSpace * saveScale);
            bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
        	if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
        		matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
            	if (mScaleFactor < 1) {
            		matrix.getValues(m);
            		float x = m[Matrix.MTRANS_X];
                	float y = m[Matrix.MTRANS_Y];
                	
    	        	if (Math.round(origWidth * saveScale) < width) {
    	        		if (y < -bottom)
        	        		matrix.postTranslate(0, -(y + bottom));
    	        		else if (y > 0)
        	        		matrix.postTranslate(0, -y);
    	        	} else {
                		if (x < -right) 
        	        		matrix.postTranslate(-(x + right), 0);
                		else if (x > 0) 
        	        		matrix.postTranslate(-x, 0);
        	        }
            	}
        	} else {
            	matrix.postScale(mScaleFactor, mScaleFactor, detFocX, detFocY);
            	matrix.getValues(m);
            	float x = m[Matrix.MTRANS_X];
            	float y = m[Matrix.MTRANS_Y];
            	if (mScaleFactor < 1) {
    	        	if (x < -right) 
    	        		matrix.postTranslate(-(x + right), 0);
    	        	else if (x > 0) 
    	        		matrix.postTranslate(-x, 0);
    	        	if (y < -bottom)
    	        		matrix.postTranslate(0, -(y + bottom));
    	        	else if (y > 0)
    	        		matrix.postTranslate(0, -y);
            	}
        	}
    			
    	}
		@Override
	    public boolean onScale(ScaleGestureDetector detector) {
			float detScFac=detector.getScaleFactor();
			float detFocX=detector.getFocusX();
			float detFocY=detector.getFocusY();	
			
			scaleRoutine(detScFac,detFocX,detFocY);
	        return true;
	        
	    }
	}
    
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        
        onMeasureDone=true;
        measuredx=widthMeasureSpec;
        measuredy=heightMeasureSpec;
        
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        
        //Fit to screen.
        float scale;
        float scaleX =  (float)width / (float)bmWidth;
        float scaleY = (float)height / (float)bmHeight;
       
       	scale = Math.min(scaleX, scaleY);
       
        
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
        saveScale = 1f;//scale;

        // Center the image
        redundantYSpace = (float)height - (scale * (float)bmHeight) ;
        redundantXSpace = (float)width - (scale * (float)bmWidth);
        redundantYSpace /= (float)2;
        redundantXSpace /= (float)2;

        matrix.postTranslate(redundantXSpace, redundantYSpace);
        
        origWidth = width - 2 * redundantXSpace;
        origHeight = height - 2 * redundantYSpace;
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
        setImageMatrix(matrix);
        
        float nDisp=width/height;
        float nIm=bmWidth/bmHeight;
        // landscape:portrait
        float initScale = nDisp>nIm ? width/bmWidth : height/bmHeight;
       
        this.setScale(initScale/scale);
    }
}
