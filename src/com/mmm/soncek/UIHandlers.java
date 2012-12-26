package com.mmm.soncek;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class UIHandlers {
	ImageActivity tata;
	
	OnSeekBarChangeListener SeekBarHandler;
	OnTouchListener PlayPauseHandler;
	OnTouchListener BackHandler;
	OnTouchListener OptionsHandler;
	

	private BitmapDrawable iconPause;
	private BitmapDrawable iconPlay;
	private BitmapDrawable iconBack;
	private BitmapDrawable iconMenu;
	
	
	public UIHandlers(ImageActivity fotr)
	{
		tata=fotr;
		
		iconPause=new BitmapDrawable(tata.getResources(), 
				BitmapFactory.decodeResource(tata.getResources(), 
						R.drawable.ic_media_pause));
		iconPlay=new BitmapDrawable(tata.getResources(), 
				BitmapFactory.decodeResource(tata.getResources(), 
						R.drawable.ic_media_play));
		iconBack=new BitmapDrawable(tata.getResources(), 
				BitmapFactory.decodeResource(tata.getResources(), 
						R.drawable.ic_menu_back));
		iconMenu=new BitmapDrawable(tata.getResources(), 
				BitmapFactory.decodeResource(tata.getResources(), 
						R.drawable.ic_menu_mapmode));
		
		SeekBarHandler = new SeekBar.OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {		
				if (tata.onResumeDone){
					tata.Offset=seekBar.getProgress();
					Log.d("file","p_changed, off= "+seekBar.getProgress()+"/"+seekBar.getMax());
					tata.updateDisplay();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub			
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		PlayPauseHandler= new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iconPause.setAlpha(50);   
					iconPlay.setAlpha(50);
		    		v.setBackgroundDrawable(tata.getTimerRunning() ? 
		    				iconPause:iconPlay);
					break;
				case MotionEvent.ACTION_UP:
					tata.toggleTimer();
					iconPause.setAlpha(255);   
					iconPlay.setAlpha(255);
					v.setBackgroundDrawable(tata.getTimerRunning() ? 
							iconPause:iconPlay);
					break;
				default:
					break;
				}
				return false;
			}		
		};
		
		BackHandler= new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iconBack.setAlpha(50);   
					v.setBackgroundDrawable(iconBack);
					break;
				case MotionEvent.ACTION_UP:
					iconBack.setAlpha(255);   
					v.setBackgroundDrawable(iconBack);
					Intent intent=new Intent(tata.getApplicationContext(),MainActivity.class);
		    		tata.startActivity(intent);
					break;
				default:
					break;
				}
				return false;
			}			
		};
		OptionsHandler= new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iconMenu.setAlpha(50);   
					v.setBackgroundDrawable(iconMenu);
					break;
				case MotionEvent.ACTION_UP:
					iconMenu.setAlpha(255);   
					v.setBackgroundDrawable(iconMenu);
					tata.Rules.setOption(0);
					//tata.RefreshImage(filename)
					tata.updateDisplay();
					break;
				default:
					break;
				}
				return false;
			}				
		};
		
		
	}
	
	private boolean ButtonTouchEvent(View btn, MotionEvent theMotion, int resID)
	{
		int btnID=btn.getId();
		BitmapDrawable ikona;
		switch ( theMotion.getAction() ) {    	    	
		    case MotionEvent.ACTION_DOWN:	
		    	ikona=new BitmapDrawable(tata.getResources(), 
		    			BitmapFactory.decodeResource(tata.getResources(), btnID==R.id.buttonPlay ? 
		    					(tata.timerRunning ? R.drawable.ic_media_pause : 
		    						R.drawable.ic_media_play) : resID));
	    		ikona.setAlpha(50);    		
	    		btn.setBackgroundDrawable(ikona);
		    	break;
		    case MotionEvent.ACTION_UP:     	
		    	ikona=new BitmapDrawable(tata.getResources(), 
		    			BitmapFactory.decodeResource(tata.getResources(), btnID==R.id.buttonPlay ? 
		    					(tata.timerRunning ? R.drawable.ic_media_pause : 
		    						R.drawable.ic_media_play) : resID));
		    	btn.setBackgroundDrawable(ikona);
		    	
				if (!tata.timerRunning){
					if (tata.Offset < tata.OffsetMax) tata.Offset++;
					else tata.Offset=0;
					tata.bLoop.setBackgroundResource(R.drawable.ic_media_pause);
					//mSlideBar.Show();
				} else {
					tata.timerRunning=false;			
					tata.handler.removeCallbacks(tata.Runnable);
					tata.bLoop.setBackgroundResource(R.drawable.ic_media_play);
				}
					
		    	tata.timerRunning=!tata.timerRunning;
		    	//tata.mSeekBar.setMax(tata.OffsetMax);
		    	tata.mSeekBar.setProgress(tata.Offset); 
		    	break;
	    }
	    return true;
	}

}
