package com.mmm.soncek;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;

public class Napoved implements Rules{
	
	class InitTask extends AsyncTask<String, String, String> {
		ImageActivity tata;	
		public InitTask(ImageActivity ia, File cachePath)	{
			tata=ia;
			isInitialized=false;
		}
		public boolean URLexists(String URLName){
			try {
				HttpURLConnection.setFollowRedirects(false);			
				//HttpURLConnection.setInstanceFollowRedirects(false)// note : you may also need
				HttpURLConnection con =
						(HttpURLConnection) new URL(URLName).openConnection();
				con.setRequestMethod("HEAD");
				return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	return false;
		    }
		}
		@Override
		protected String doInBackground(String... arg0) {
			for (int i=0;i<hardCoded.length;i++) {
				if (URLexists(imageRoot+hardCoded[i])) {
					found[OffsetMax]=hardCoded[i];
					OffsetMax++;
				}
			}
			if (OffsetMax>0){
				OffsetMax-=1;
				isInitialized=true;		
			}
			return " Konèano ";
		}
		@Override
		protected void onPreExecute(){
			initInProgress=true;	
			OffsetMax=0;
		}
		@Override
	    protected void onPostExecute(String message) {
			initInProgress=false;
			tata.OffsetMax=OffsetMax;
			tata.mSeekBar.setMax(OffsetMax);
			tata.showControls(true);
			tata.updateDisplay();
			isInitialized=true;
			
	    }
	}
	
	private int OffsetMax;
	private final String imageRoot=
			"http://www.vreme.si/uploads/probase/www/fproduct/graphic/sl/";
	public boolean initInProgress;
	public boolean isInitialized;	
	private final String[] hardCoded=new String[]{
			"fcast_si-neighbours_d1h06.png",
			"fcast_si-neighbours_d1h15.png",
			"fcast_si-neighbours_d2h06.png",
			"fcast_si-neighbours_d2h15.png",
			"fcast_si-neighbours_d3h06.png",	
			"fcast_si-neighbours_d3h15.png"};
	
	private String[] found=new String[8];
	private InitTask initTask;
	ImageActivity tata;
	
	public Napoved(ImageActivity ia){
		tata=ia;
		OffsetMax=0;
		isInitialized=false;
	}
	@Override
	public void Initialize(ImageActivity ia, File cf) {	
		initTask=new InitTask(ia,cf);
		initTask.execute();		
	}

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	public int getProgress(int Offset) {
		return 0;
	}

	@Override
	public int getNoImages() {
		return OffsetMax;
	}

	@Override
	public int getInitialOffset() {
		return 0;
	}

	@Override
	public String GetUrl(int Offset) {
		return imageRoot+found[Offset];
	}

	@Override
	public String getFileName(int Offset) {
		return found[Offset];
	}

	@Override
	public String getLocalTime(int Offset) {
		return "";
	}

	@Override
	public String[] getOptionList() {
		return null;
	}

	@Override
	public int getOptionsNumber() {
		return 0;
	}

	@Override
	public void setOption(int opt) {		
	}

	@Override
	public Bundle getSettings() {
		Bundle b=new Bundle();
		b.putStringArray("found", found);
		b.putInt("OffsetMax",OffsetMax);
		return b;
	}

	@Override
	public void setSettings(Bundle b) {
		found = b.getStringArray("found");
		OffsetMax=b.getInt("OffsetMax");
		tata.OffsetMax=OffsetMax;
		tata.mSeekBar.setMax(OffsetMax);
		isInitialized=true;
	}

}
