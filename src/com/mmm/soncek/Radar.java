package com.mmm.soncek;

import java.io.File;
import java.util.Calendar;

import android.os.Bundle;

public class Radar implements Rules{

	private final String mapRoot=
			"http://www.vreme.si/uploads/probase/www/observ/radar/";
		
	private final int offMax=72;
	ImageActivity tata;
	@Override
	public void Initialize(ImageActivity ia, File cf) {
		tata=ia;
		tata.showControls(true);
		tata.updateDisplay();		
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getProgress(int Offset) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNoImages() {
		// TODO Auto-generated method stub
		return offMax;
	}

	@Override
	public int getInitialOffset() {
		// TODO Auto-generated method stub
		return offMax-1;
	}

	@Override
	public String GetUrl(int o) {
		int Offset=offMax-o;
		// TODO Auto-generated method stub
		return mapRoot+getFileName(Offset);
	}

	@Override
	public String getFileName(int o) {
		int Offset=offMax-o;
		String filename="si1_";
		filename+=getCompactUTC(Offset);
		filename+="_zm_si.jpg";
		return filename;
	}

	@Override
	public String getLocalTime(int o) {
		int Offset=offMax-o;
		final Calendar c = Calendar.getInstance();
		int todayOfWeek=c.get(Calendar.DAY_OF_WEEK);
		int thisWeek=c.get(Calendar.WEEK_OF_YEAR);
    	c.add(java.util.Calendar.MINUTE, -Offset*10);
        c.getTime();
        int tempMinutes=c.get(Calendar.MINUTE);
        c.add(java.util.Calendar.MINUTE, -10-tempMinutes%10);
        c.getTime();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour=c.get(Calendar.HOUR_OF_DAY);
        int mMinutes=c.get(Calendar.MINUTE);
        int DayOfWeek=c.get(Calendar.DAY_OF_WEEK);
        int Week=c.get(Calendar.WEEK_OF_YEAR);
        
        if (DayOfWeek==todayOfWeek && Week==thisWeek) 
        	return " Danes "+
        		String.format("%02d", mHour)+":"+
        		String.format("%02d", mMinutes) + " ";
        
        if ((todayOfWeek==DayOfWeek+1 && Week==thisWeek) || 
        	   (todayOfWeek==1 && DayOfWeek==7 && Week+1==thisWeek)) 
        	return " Vèeraj "+
	        	String.format("%02d", mHour)+":"+
	    		String.format("%02d", mMinutes) + " ";
     	       
        return 	" "+
        		String.format("%02d", mDay)+"."+
        		String.format("%02d", mMonth)+"."+
        		mYear+" - "+
        		String.format("%02d", mHour)+":"+
        		String.format("%02d", mMinutes) + " ";
	}

	@Override
	public String[] getOptionList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOptionsNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOption(int opt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Bundle getSettings() {
		return null;
	}

	@Override
	public void setSettings(Bundle settings) {
		// TODO Auto-generated method stub
		
	}
	
	private String getCompactUTC( int o)
	{
		int Offset=offMax-o;
		final Calendar c = Calendar.getInstance();
		int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);  	 
    	int dstOffset = c.get(java.util.Calendar.DST_OFFSET); 
    	c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    	c.add(java.util.Calendar.MINUTE, -Offset*10);
        c.getTime();
        int tempMinutes=c.get(Calendar.MINUTE);
        c.add(java.util.Calendar.MINUTE, -10-tempMinutes%10);
        c.getTime();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour=c.get(Calendar.HOUR_OF_DAY);
        int mMinutes=c.get(Calendar.MINUTE);
        return 	mYear+
        		String.format("%02d", mMonth)+
        		String.format("%02d", mDay)+"-"+
        		String.format("%02d", mHour)+
        		String.format("%02d", mMinutes);
	}
}
