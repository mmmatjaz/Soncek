package com.mmm.soncek;

import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.mmm.soncek.MainActivity.Options;

public class Aladin implements Rules {

	class InitTask extends AsyncTask<String, String, String> {
		ImageActivity tata;
		private File CachePath;
		public InitTask(ImageActivity ia, File cachePath)	{
			CachePath=cachePath;
			tata=ia;
		}
		private boolean touchImage(String sUrl) {
			try {       	
	            URL url = new URL(sUrl);
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            int lenghtOfFile = connection.getContentLength();
	            if (lenghtOfFile>2000) return true;        		
	        } catch (Exception e) {
	        	Log.d("download","exception: "+e.getMessage());
	        	return false;
	        }        
	        return false;
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
		protected String doInBackground(String... params) {
			isInitialized=false;
			GeneratedYesterday=false;
			GeneratedAtMidnight=false;
			
			if (new java.io.File(CachePath,getFileName(0)).length()>110
					||	URLexists(GetUrl(0)))	{
				isInitialized=true;
			} else	{
				GeneratedYesterday=false;
				GeneratedAtMidnight=true;
			}
			
			if (!isInitialized && (
				new java.io.File(CachePath,getFileName(0)).length()>110
					||	URLexists(GetUrl(0))))	{
				isInitialized=true;
			} else	{
				GeneratedYesterday=true;
				GeneratedAtMidnight=false;
			}
			return " Konèano ";
		}
		@Override
		protected void onPreExecute(){
			initInProgress=true;
			
		}
		@Override
	    protected void onPostExecute(String message) {
			initInProgress=false;
			tata.showControls(true);
			tata.updateDisplay();
			isInitialized=true;
	    }
	}
	
	
	private InitTask initTask;
	private Options option;
	private boolean GeneratedYesterday;
	private boolean GeneratedAtMidnight;
	private static final int OffsetMax=22;
	private final String mapRoot=
			"http://www.vreme.si/uploads/probase/www/model/aladin/field/";
	private final String[] Days={	
			"Nedelja",
			"Ponedeljek",
			"Torek",
			"Sreda",
			"Èetrtek",
			"Petek",
			"Sobota"};
	private final String[] Maps={
			"Slovenija",
			"Alpe-Jadran"};
	private int MapSelected;
	
	private boolean isInitialized;
	private boolean initInProgress;
	private boolean padavine;
	
	public Aladin(MainActivity.Options aladinType) {
		GeneratedYesterday=true;
		GeneratedAtMidnight=true;	
		option=aladinType;
		padavine= (option==Options.AladinPer) ? true:false;
		MapSelected=0;
		isInitialized=false;
	}
	
	@Override
	public int getProgress(int Offset) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNoImages() {
		return OffsetMax;
	}

	@Override
	public String GetUrl(int Offset) {
		return mapRoot+getFileName(Offset);
	}

	@Override
	public String getLocalTime(int Offset) {
		if (!isInitialized) return "";
		final Calendar c = Calendar.getInstance();
		int todayOfWeek=c.get(Calendar.DAY_OF_WEEK);
		int thisWeek=c.get(Calendar.WEEK_OF_YEAR);
		int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);  	 
    	int dstOffset = c.get(java.util.Calendar.DST_OFFSET); 
    	
    	c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    	    	
    	if (GeneratedYesterday) 
    		c.add(java.util.Calendar.HOUR, -24); // go to yesterday
    	c.getTime();
        
    	int mHour = c.get(Calendar.HOUR_OF_DAY);
        
    	if (GeneratedAtMidnight) 
        	c.add(java.util.Calendar.HOUR, -mHour);
        else 
        	c.add(java.util.Calendar.HOUR, -mHour+12);
        
    	c.add(java.util.Calendar.HOUR, 6);
        c.add(java.util.Calendar.HOUR, Offset*3);
        c.add(java.util.Calendar.MILLISECOND, (zoneOffset + dstOffset));
        
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int DayOfWeek=c.get(Calendar.DAY_OF_WEEK);
        int Week=c.get(Calendar.WEEK_OF_YEAR);
		
        mHour = c.get(Calendar.HOUR_OF_DAY);     
        
        if (DayOfWeek==todayOfWeek && Week==thisWeek) return " Danes "+mHour+"h ";
        if ((todayOfWeek==DayOfWeek+1 && Week==thisWeek) || 
        	   (todayOfWeek==1 && DayOfWeek==7 && Week+1==thisWeek)) return " Vèeraj "+mHour+"h ";      	
		if ((todayOfWeek==DayOfWeek-1 && Week==thisWeek) || 
    		   (todayOfWeek==7 && DayOfWeek==1 && Week-1==thisWeek)) return " Jutri "+mHour+"h ";
        return " "+Days[DayOfWeek-1]+" "+mDay+"."+mMonth+". "+mHour+"h ";
	}

	@Override
	public String[] getOptionList() {
		// TODO Auto-generated method stub
		return Maps;
	}
	
	public String getFileName(int Offset)
	{
		
		String filename="as_";
		filename+=getCompactUTC();
		filename+=GeneratedAtMidnight ? "-0000" : "-1200";
		filename+=padavine ? "_tcc-rr_":"_t2m_";
		filename+=MapSelected==0 ? "si-neighbours_" : "alps-adriatic_";
		filename+=String.format("%03d", 6+3*Offset);
		filename+=".png";
		return filename;
	}
	
	private String getCompactUTC()
	{
		final Calendar c = Calendar.getInstance();
		int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);  	 
    	int dstOffset = c.get(java.util.Calendar.DST_OFFSET); 
    	c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    	if (GeneratedYesterday) c.add(java.util.Calendar.HOUR, -24); // go to yesterday
    	c.getTime();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        
        return mYear+String.format("%02d", mMonth)+(String.format("%02d", mDay));
	}
	
	@Override
	public Bundle getSettings()
	{
		Bundle b= new Bundle();
		b.putBoolean("gy", GeneratedYesterday);
		b.putBoolean("gam", GeneratedAtMidnight);
		b.putBoolean("pad", padavine);
		b.putInt("mapsel", MapSelected);
		return b;
	}
	
	@Override
	public void setSettings(Bundle b) {
		GeneratedYesterday=	b.getBoolean("gy");
		GeneratedAtMidnight=b.getBoolean("gam");
		padavine=			b.getBoolean("pad");
		MapSelected=		b.getInt("mapsel");
		isInitialized=true;
	}
	@Override
	public void Initialize(ImageActivity ia, File cf){
		initTask=new InitTask(ia,cf);
		initTask.execute();
	}

	@Override
	public int getInitialOffset() {
		return 0;
	}

	@Override
	public int getOptionsNumber() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void setOption(int opt) {
		// TODO Auto-generated method stub
		MapSelected=1-MapSelected;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return !initInProgress;
	}
}
