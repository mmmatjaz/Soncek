/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * Updated By: Babay88
 * -------------------
 * Extends Android ImageView to include pinch zooming and panning.
 */

package com.mmm.soncek;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
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
    int swipe = NONE;
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

        int viewWidth, viewHeight;
    static final int CLICK = 3;
    float saveScale = 1f;
    protected float origWidth, origHeight;
    int oldMeasuredWidth, oldMeasuredHeight;

    ScaleGestureDetector mScaleDetector;
    GestureDetector mTapDetector;
    ScaleListener sl;
    public Context context;
	//private ScaleListener sl;
	public Bitmap bmap;
	private Bitmap saveRef;
	private int bmWidth;
	private int bmHeight;
	
	
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
    
  

  private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        sl=new ScaleListener();
        mScaleDetector = new ScaleGestureDetector(context, sl);
        mTapDetector = new GestureDetector(context, new DoubleTapListener());
        matrix = new Matrix();
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                mTapDetector.onTouchEvent(event);
                PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    	last.set(curr);
                        start.set(last);
                        if (event.getX()>(1-swipeMin)*viewWidth && mode!=ZOOM){
	                    	swipeStart.set(event.getX(), event.getY());
	                    	swipe=SWIPEfR;
	                    	drawGradRigth(0.5f);
	                    }
	                    else if (event.getX()<swipeMin*viewHeight && mode!=ZOOM){
	                    	swipeStart.set(event.getX(), event.getY());
	                    	swipe=SWIPEfL;
	                    	drawGradLeft(0.5f);
	                    }
	                    else
	                    	mode = DRAG;
                        break;
                        
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;
                            float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale);
                            float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale);
                            matrix.postTranslate(fixTransX, fixTransY);
                            fixTrans();
                            last.set(curr.x, curr.y);
                        } else if (swipe==SWIPEfR) {
	            			float deltaX = curr.x - swipeStart.x;
	            			float r=.5f+Math.abs(deltaX)/(swipeMax*viewWidth)/2;
	            			if (r>1) r=1;
	            			drawGradCancel();
	            			drawGradRigth(r);
	            		}
	            		if (swipe==SWIPEfL) {
	            			float deltaX = curr.x - swipeStart.x;
	            			float r=.5f+Math.abs(deltaX)/(swipeMax*viewWidth)/2;
	            			if (r>1) r=1;
	            			drawGradCancel();
	            			drawGradLeft(r);
	            		}
                        break;

                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        int xDiff = (int) Math.abs(curr.x - start.x);
                        int yDiff = (int) Math.abs(curr.y - start.y);
                        if (xDiff < CLICK && yDiff < CLICK)
                            performClick();
                        
                        float deltaX = curr.x - swipeStart.x;
            			//float deltaY = curr.y - swipeStart.y;
                        if (swipe==SWIPEfR && Math.abs(deltaX)>swipeMax*viewWidth) {        		
	            			tata.forward();
	            		} else drawGradCancel();	            		
	            		if (swipe==SWIPEfL && Math.abs(deltaX)>swipeMax*viewWidth) {          		
	            			tata.back();
	            		} else drawGradCancel();
	            		swipe=NONE;
	            		
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

	private boolean drawGradRigth(float n) {
    	Canvas canvas=new Canvas(bmap);
    	float[] mtrx=new float[9];
		matrix.getValues(mtrx);
		float width= (float)viewWidth;
		float height=(float)viewHeight;
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
    	
    	return true;
    }
   
    private boolean drawGradLeft(float n) {
    	Canvas canvas=new Canvas(bmap);
    	float[] mtrx=new float[9];
		matrix.getValues(mtrx);
		float width= (float)viewWidth;
		float height=(float)viewHeight;
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
    	
    	return true;
    }
    
    private boolean drawGradCancel() {
    	if(bmap != null) {
	    	bmap = saveRef.copy(Bitmap.Config.ARGB_8888, true);
	    	super.setImageBitmap(bmap);
    	}
    	return true;
    }

    public void setMaxZoom(float x) {
        maxScale = x;
    }

   
    @Override
    public void setImageBitmap(Bitmap bm) { 
    	
        if(bm != null && bm.getWidth()>100 && bm.getHeight()>100) {
        	if (getVisibility()==View.INVISIBLE) setVisibility(View.VISIBLE);
        	saveRef=bm;
        	bmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        	super.setImageBitmap(bmap);
        }
        else setVisibility(View.INVISIBLE);
    }

    public void scaleOnDoubleTap(){
    	float[] mtrx=new float[9];
		matrix.getValues(mtrx);
		float curScale=mtrx[Matrix.MSCALE_X];
    	float nDisp=viewWidth/viewHeight;
        float nIm=bmWidth/bmHeight;
        // landscape:portrait
        float tarScale;
        if (bmWidth*curScale>=viewWidth && bmHeight*curScale>=viewHeight){
        	tarScale= nDisp>nIm ? (float)viewHeight/(float)bmHeight : (float)viewWidth/(float)bmWidth;
        	this.setScale(tarScale/curScale);
        }
    	else {
    		tarScale= nDisp>nIm ?  (float)viewWidth/(float)bmWidth : (float)viewHeight/(float)bmHeight;
    		this.setScale(tarScale/curScale);
    	}
        
        
    }
    
    public void setScale(float scale){
    	sl.setScale(scale);
    	setImageMatrix(matrix);
        invalidate();
	}
    
    private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            //Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            scaleOnDoubleTap();
            return true;
        }
       
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
       
    	@Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
        	float detScFac=detector.getScaleFactor();
			float detFocX=detector.getFocusX();
			float detFocY=detector.getFocusY();	
			
			scaleRoutine(detScFac,detFocX,detFocY);
            return true;
        }
        
        public void setScale(float scale)
    	{
    		scaleRoutine(scale,viewWidth/2,viewHeight/2);
    	}
        
        public boolean scaleRoutine(float detScFac,float detFocX,float detFocY) {
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

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight)
                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
            else
                matrix.postScale(mScaleFactor, mScaleFactor, detFocX, detFocY);

            fixTrans();
        	return false;        	
        }
    }

    void fixTrans() {
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        
        float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
        float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

        if (fixTransX != 0 || fixTransY != 0)
            matrix.postTranslate(fixTransX, fixTransY);
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }
    
    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        
        //
        // Rescales image on rotation
        //
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
                || viewWidth == 0 || viewHeight == 0)
            return;
        oldMeasuredHeight = viewHeight;
        oldMeasuredWidth = viewWidth;

        if (saveScale == 1) {
            //Fit to screen.
            float scale;

            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
                return;
            bmWidth = drawable.getIntrinsicWidth();
            bmHeight = drawable.getIntrinsicHeight();
            
            //Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;
            scale = Math.min(scaleX, scaleY);
            matrix.setScale(scale, scale);

            // Center the image
            float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
            float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
            redundantYSpace /= (float) 2;
            redundantXSpace /= (float) 2;

            matrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;
            origHeight = viewHeight - 2 * redundantYSpace;
            setImageMatrix(matrix);
        }
        fixTrans();
    }
}