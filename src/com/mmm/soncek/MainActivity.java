package com.mmm.soncek;


import java.io.File;
import mmmatjaz.sonce.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private enum ActLayouts {
		Menu, About, Welcome, NoCon, None
	}
	public enum Options {
		Html, Pics, AladinTemp, AladinPer, Satelite, Radar
	}
	static final String[] OptionsStr = new String[] { 
		"tekstovna napoved","slikovna napoved","napoved padavin","napoved temperatur",
		"satelitska slika","radarska slika"};
	
	private GridView list;
	private Button sharebtn;
    private Button switchbtn;
    private View bgIm;
    private View aboutLinLay;
    private View welcomeLay;
    private View noCon;
    
    private TextView titlelbl;
    
    private View[] views;
    private ActLayouts actLayout;
   
    
    public class RunDelayed implements Runnable {
    	private int selection;
		public RunDelayed(int sel){
    		selection=sel;
    	}
    	
		@Override
		public void run() {
    		if (selection==0)
        	{    		
        		Intent intent=new Intent(MainActivity.this,TextForecast.class);
        		//startActivity(intent);
        		startActivityForResult(intent,0);
        	}
        	if (selection>0 && selection<6)
        	{
        		Bundle bundle = new Bundle();
        		bundle.putInt("map", selection-1);
        		Intent intent=new Intent(MainActivity.this,ImageActivity.class);
        		intent.putExtras(bundle);
        		//startActivity(intent);
        		startActivityForResult(intent,0);
        	}	
		}
    }
    public Handler handler = new Handler();

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        
        //capture UI widgets
        list=	(GridView)		findViewById(R.id.gridView1);      
        	list.setAdapter(		new ArrayAdapter<String>(this, R.layout.list_item, OptionsStr));
        switchbtn=(Button) 		findViewById(R.id.switch_btn);
        sharebtn=(Button) 		findViewById(R.id.shbtn);
        aboutLinLay=(View)			findViewById(R.id.linearLayoutAbout);        
        titlelbl=(TextView)		findViewById(R.id.textViewTitle);
        welcomeLay=(View)		findViewById(R.id.LayoutWelcome);
        noCon=(View)			findViewById(R.id.LayoutOfflajn);
        bgIm=(View)				findViewById(R.id.bg_im);
        views=new View[]{list,aboutLinLay,welcomeLay,noCon};
        
        Button okBtn=(Button)	findViewById(R.id.buttonOK);
        Button okBtn2=(Button)	findViewById(R.id.buttonConOK);
        
        // set listeners         
        okBtn.setOnClickListener(new okBtnClickListener());
        okBtn2.setOnClickListener(new okNCBtnListener());
        sharebtn.setOnTouchListener(new shareBtnListener());
        switchbtn.setOnTouchListener(new menuHelpBtnListener());
        list.setOnItemClickListener(new listListener());
        
        deleteOldImages();
    }

    
    @Override
    public void onResume() 
	{
		super.onResume();
		hideAll();		
		showView(titlelbl);
		
		SharedPreferences settings = getSharedPreferences("prefs", 0);
		bgIm.setVisibility(View.VISIBLE);
		bgIm.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    	if (settings.getBoolean("welcomeRead", false))
    		checkConnection();
    	else changeView(ActLayouts.Welcome.ordinal(),false);	
	}
    
    public void deleteOldImages()
	{
		File f = new File(getCacheDir().toString()); 
		File[] files = f.listFiles();
		for (int i=0;i<files.length;i++)
		{
			long fileModified=files[i].lastModified();
			long now=System.currentTimeMillis();
			if ((now-fileModified)/1000/60/60>12)
				files[i].delete();
		}
	}
    
    private boolean checkConnection() {
    	ConnectivityManager cm =
    	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	changeView(ActLayouts.Menu.ordinal(),true);
	    	return true;
	    } else {
	    	changeView(ActLayouts.NoCon.ordinal(),false);
	    	return false;
	    }
	}

    public void changeBtnIcon(ActLayouts ac) {
    	switch (ac)
    	{
    	case Menu:
			switchbtn.setBackgroundResource(R.drawable.ic_menu_help_holo_light);	
			break;
    	case About:
			switchbtn.setBackgroundResource(R.drawable.ic_menu_moreoverflow);
			break;   	
    	}
    }
    
    public void hideAll()
    {
    	for (int i=0; i<views.length; i++) 	{
    		views[i].setVisibility(View.INVISIBLE);
    	}
    	switchbtn.setVisibility(View.VISIBLE);
		sharebtn.setVisibility(View.VISIBLE);
    }
    
    public void changeView(int view, boolean buttons)
    {
    	for (int i=0; i<views.length; i++) 	{
    		if (i==view) {
    			showView(views[i]);
    			actLayout=ActLayouts.values()[i];
    		} else {
    			hideView(views[i]);
    		}
    	}
    	if (buttons){
    		showView(switchbtn);
    		showView(sharebtn);
    	}
		else{
			hideView(switchbtn);
    		hideView(sharebtn);
		}
    }
	
    private void hideView(View v){
    	if (v.getVisibility()==View.VISIBLE){
    		v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
    		v.setVisibility(View.INVISIBLE);
    	}
    }
    private void showView (View v){    	
    	if (v.getVisibility()==View.INVISIBLE){
    		v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
			v.setVisibility(View.VISIBLE);
    	}
    }
    private void hideTheSun()
    {
    	bgIm.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    	bgIm.setVisibility(View.INVISIBLE);
    }
    
    private class listListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id) {
	    	changeView(ActLayouts.None.ordinal(), false);
	    	hideView(switchbtn);
	    	hideView(sharebtn);
	    	hideView(titlelbl);
	    	hideTheSun();
			handler.postDelayed(new RunDelayed(position), 300);
        }   	
    }
    private class okBtnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			SharedPreferences settings = getSharedPreferences("prefs", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("welcomeRead", true);
			editor.commit();
			checkConnection();			
		}  	
    }
    private class okNCBtnListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			changeView(ActLayouts.Menu.ordinal(),true);	
		}
    }
    private class shareBtnListener implements OnTouchListener {
		@Override
		public boolean onTouch(View Button, MotionEvent theMotion) {
			BitmapDrawable ikona=new BitmapDrawable(getResources(), 
    				BitmapFactory.decodeResource(getResources(),R.drawable.ic_menu_share_holo_dark));
    		switch ( theMotion.getAction() ) {    	    	
	    	case MotionEvent.ACTION_DOWN:		    		
	    		ikona.setAlpha(100);    
	    		Button.setBackgroundDrawable(ikona);
	    		
		    	break;
		    case MotionEvent.ACTION_UP:   
		    	Button.setBackgroundDrawable(ikona);
		    	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
       			sharingIntent.setType("text/plain");
       			String shareBody = "https://play.google.com/store/apps/details?id=mmmatjaz.sonce";
       			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Poèekiri ta app!");
       			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
       			startActivity(Intent.createChooser(sharingIntent, "Share via"));
		    	break;
		    default:
				break;
    		} 	
			return false;
		}  	
    }
    private class menuHelpBtnListener implements OnTouchListener {
		@Override
		public boolean onTouch(View Button, MotionEvent theMotion) {
			BitmapDrawable ikona=new BitmapDrawable(getResources(), 
    				BitmapFactory.decodeResource(getResources(),
    						actLayout==ActLayouts.About ? R.drawable.ic_menu_moreoverflow : R.drawable.ic_menu_help_holo_light));
    		switch ( theMotion.getAction() ) {    	    	
	    	case MotionEvent.ACTION_DOWN:		    		
	    		ikona.setAlpha(100);   
	    		Button.setBackgroundDrawable(ikona);
		    	break;
		    case MotionEvent.ACTION_UP: 
		    	Button.setBackgroundDrawable(ikona);
		    	actLayout=actLayout==ActLayouts.About ? ActLayouts.Menu:ActLayouts.About;
		    	changeView(actLayout.ordinal(),true);
		    	changeBtnIcon(actLayout);
		    	break;
		    default:
				break;
    		} 	
			return false;
		}
    	
    }
    
}
