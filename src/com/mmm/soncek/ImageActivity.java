package com.mmm.soncek;

import java.io.File;
import mmmatjaz.sonce.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mmm.soncek.MainActivity.Options;

public class ImageActivity extends Activity{

	public Rules Rules;
	// LAYOUT OBJECTS
	public Button bStart;
	public Button bForward;
	public Button bBackward;
	public Button bEnd;
	public Button bLoop;
	public SeekBar mSeekBar;
	public TextView mTextLocal;
	public TextView mTextDebug;
	public ProgressBar mProgressB;
	public TouchImageView mImageView;
	public LinearLayout mTopLinLay;
	public LinearLayout mBottomLinLay;
		
	public int Offset;
	public int OffsetMax;
	String filepath;
	String filename;
	String URL;
	String Time;
	public boolean timerRunning;
	
	private boolean activityRecovered;
	private int mapType;
	
	public Handler handler;
	public Runnable Runnable;
	private Button bBack;
	private Button bOpt;
	public File cachePath;
	public boolean onResumeDone;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        //Log.d("file","onCreate");
        // capture widgets
        captureWidgets(); 
        onResumeDone=false;
        // register callbacks
        UIHandlers handlers=new UIHandlers(this);     
        mSeekBar.setOnSeekBarChangeListener(handlers.SeekBarHandler);
        bLoop.setOnTouchListener(			handlers.PlayPauseHandler);
        bBack.setOnTouchListener(			handlers.BackHandler);
        bOpt.setOnTouchListener(			handlers.OptionsHandler);
		
		// loop timer
		handler = new Handler();
		Runnable = new Runnable() {
	        public void run() {
	        	
	        	if (Offset>=OffsetMax) 
	        		Offset=0;
	        	else
	        	Offset++;
	        	//Log.d("timer","running "+Offset);
	        	mSeekBar.setMax(OffsetMax);
		    	mSeekBar.setProgress(Offset);
	        	   	
	        }
	    }; 
	    
	    // load bundle    
	    Bundle bundle = this.getIntent().getExtras();
	    mapType = bundle.getInt("map");
	    
	    switch (mapType){
	    case 0:
	    	Rules=new Napoved(this);
	    	break;
	    case 1:
	    	Rules=new Aladin(Options.AladinPer);
	    	break;
	    case 2:
	    	Rules=new Aladin(Options.AladinTemp);
	    	break;
	    case 3:
	    	Rules=new Satelit();
	    	break;
	    case 4:
	    	Rules=new Radar();
	    	break;
	    default:
    		Rules=new Aladin(Options.AladinPer);
	    }
	    bOpt.setVisibility(Rules.getOptionsNumber()==0 ? View.GONE : View.VISIBLE);
	    cachePath=getCacheDir();
	    
	    // hide controls
	    showControls(false);
	    mImageView.setActivity(this);
	    mImageView.setFadingEdgeLength(10);
    }

	
	public void showControls(boolean b) {
		if (b){
			mBottomLinLay.setVisibility(	View.VISIBLE);
			mTopLinLay.setVisibility(		View.VISIBLE);
			mProgressB.setVisibility(		View.INVISIBLE);
		} else {
			mBottomLinLay.setVisibility(	View.INVISIBLE);
			mTopLinLay.setVisibility(		View.INVISIBLE);
			mProgressB.setVisibility(		View.VISIBLE);
		}
	}

	@Override
	public void onResume() 	{
		super.onResume();   
		onResumeDone=true;
		//Log.d("file","resume");
		timerRunning=false;
	    OffsetMax=	Rules.getNoImages();
	    if (!activityRecovered)    {
	    	//Log.d("offset","fresh");
	    	Offset = Rules.getInitialOffset();
	    	Rules.Initialize(this, cachePath);
	    }
	    else {
	    	showControls(true);
	    }
		//Log.d("offset","onResume offset set: "+Offset);
		//String[] swithces=Rules.getOptionList();
		
		int temp=Offset;
		mSeekBar.setMax(OffsetMax);
		mSeekBar.setProgress(temp);
		updateDisplay();
	}
	
	public void changeMap(){
		timerRunning=false;
	        	
    	Rules.setOption(0);
    	Rules.Initialize(this, cachePath);
    	Offset = 	Rules.getInitialOffset();
    	OffsetMax=	Rules.getNoImages();
		int temp=Offset;
		mSeekBar.setMax(OffsetMax);
		mSeekBar.setProgress(temp);
		updateDisplay();
	}
	
	@Override
	public void onPause() 	{
		super.onPause();
		//Log.d("file","onPause");
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("Offset", Offset);
		savedInstanceState.putInt("map", mapType);
		savedInstanceState.putBundle("rules", Rules.getSettings());
		//Log.d("file","saved");
	}
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Offset =	savedInstanceState.getInt("Offset");
		mapType=	savedInstanceState.getInt("map");
		Bundle rulBun=savedInstanceState.getBundle("rules");
		if (rulBun!=null && !rulBun.isEmpty())
			Rules.setSettings(rulBun);		
		activityRecovered=true;
		//Log.d("file","restored");
	}
	
	private void captureWidgets() {
		mTopLinLay=	(LinearLayout) 	findViewById(R.id.topLinLay);
		mBottomLinLay=(LinearLayout)findViewById(R.id.BottomLayout);
        bBack = 	(Button) 		findViewById(R.id.buttonBack);
        bOpt = 		(Button) 		findViewById(R.id.buttonOpt);
        bLoop = 	(Button) 		findViewById(R.id.buttonPlay);
        mSeekBar = 	(SeekBar) 		findViewById(R.id.seekBar1);
        mTextLocal=	(TextView) 		findViewById(R.id.textViewTime);
        mTextDebug=	(TextView) 		findViewById(R.id.TextViewInfo);     
        mProgressB=	(ProgressBar) 	findViewById(R.id.progressBar1);
        mImageView=	(TouchImageView)findViewById(R.id.imagecontainer);
	}

	public boolean getTimerRunning() {
		return timerRunning;
	}

	public void toggleTimer() {
		timerRunning=!timerRunning;
		if (!timerRunning) handler.removeCallbacks(Runnable);
		else {
			handler.removeCallbacks(Runnable);
    		handler.postDelayed(Runnable, 300);
		}
	}

	public void setImage(int progress) {
		Offset=progress;
		updateDisplay();
	}

	public void updateDisplay() {
		if (Rules.isInitialized()) {
			filepath= cachePath.toString()+"/"+Rules.getFileName(Offset);
			URL 	= Rules.GetUrl(Offset);
			Time 	= Rules.getLocalTime(Offset);
			//Log.d("file","update "+Offset+" "+Rules.getFileName(Offset));
			mTextLocal.setText(Time);
			
			java.io.File file = new java.io.File(filepath);
			
			if (file.length()<200) {	
	        	BackGndDownloader dlr=new BackGndDownloader(this);
	        	dlr.execute(file.toString(),URL);        	
	        } 
	        else {
	        	mTextDebug.setTextColor(0xffaaaaaa);
	        	mTextDebug.setText("");
	        	RefreshImage(file.toString());
	        }
		}
        mSeekBar.bringToFront();
	}

	public void RefreshImage(){
		RefreshImage(filename);
	}
	public void RefreshImage(String fn) {
    	try{
	    	Bitmap bm= BitmapFactory.decodeFile(fn);
	    	mImageView.setImageBitmap(bm);
    	} catch (Exception e) {
    		//Log.d("nini","a: "+e.toString());
    	}
    	
    	if (timerRunning) {
    		//Log.d("timer","refresh");
    		handler.removeCallbacks(Runnable);
    		handler.postDelayed(Runnable, 300);
		}	
		mTextDebug.setTextColor(0xaaaaaaaa);
		mTextLocal.setTextColor(0xffffffff);
    }
	
	public void showProgressBar(boolean b) {
		mProgressB.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
	}


	public void forward() {
		if (Offset<OffsetMax) Offset++;
		mSeekBar.setProgress(Offset);		
	}

	public void back() {
		if (Offset>0) Offset--;
		mSeekBar.setProgress(Offset);
		
	}
}
