package com.mmm.soncek;

import java.io.File;
import java.util.Calendar;
import android.os.Bundle;

public class Satelit implements Rules{
	enum Region {
		Europe,Slovenia
	}
	private final String mapRoot=
			"http://www.vreme.si/uploads/probase/www/observ/satellite/";
	
	Region selectedRegion;
	int regionCat;
	boolean b;
	int offMax;
	ImageActivity tata;
	
	public Satelit()
	{
		regionCat=1;
		b=regionCat==0?true:false;
		offMax=regionCat==1? 25:16;
	}
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
		return regionCat==1? 25:16;
	}

	@Override
	public int getInitialOffset() {
		return getNoImages()-1;
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
		String filename="msg_";
		filename+=getCompactUTC(b, Offset);
		filename+=!b ? "_ir_sateu.jpg":"_hrv_si.jpg";
		return filename;
	}

	@Override
	public String getLocalTime(int o) {
		int Offset=offMax-o;
		final Calendar c = Calendar.getInstance();
		int todayOfWeek=c.get(Calendar.DAY_OF_WEEK);
		int thisWeek=c.get(Calendar.WEEK_OF_YEAR);
		
		int hourNow=c.get(Calendar.HOUR_OF_DAY);    
		if (b && hourNow>20) c.add(java.util.Calendar.HOUR_OF_DAY, -(hourNow-20));
        if (b && hourNow< 6) c.add(java.util.Calendar.HOUR_OF_DAY, -(4+hourNow));
        
		c.add(java.util.Calendar.HOUR_OF_DAY, -Offset*1);
        c.add(java.util.Calendar.MINUTE, -20);
        c.getTime();
        int mHour=c.get(Calendar.HOUR_OF_DAY);       
        if (b && mHour>20) c.add(java.util.Calendar.HOUR_OF_DAY, -(mHour-20));
        if (b && mHour<6)  c.add(java.util.Calendar.HOUR_OF_DAY, -(4+mHour));
                
        c.getTime();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour=c.get(Calendar.HOUR_OF_DAY);   
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        int DayOfWeek=c.get(Calendar.DAY_OF_WEEK);
        int Week=c.get(Calendar.WEEK_OF_YEAR);
        
        if (DayOfWeek==todayOfWeek && Week==thisWeek) return " Danes "+mHour+"h ";
        
        if ((todayOfWeek==DayOfWeek+1 && Week==thisWeek) || 
        	   (todayOfWeek==1 && DayOfWeek==7 && Week+1==thisWeek)) return " Vèeraj "+mHour+"h ";
        	
        return 	" "+
        		String.format("%02d", mDay)+"."+
        		String.format("%02d", mMonth)+"."+
        		mYear+" - "+
        		String.format("%02d", mHour)+"h ";
	}

	@Override
	public String[] getOptionList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOptionsNumber() {
		return 2;
	}

	@Override
	public void setOption(int opt) {
		b=!b;
	}

	@Override
	public Bundle getSettings() {
		Bundle b=new Bundle();
		b.putInt("reg", regionCat);
		return null;
	}

	@Override
	public void setSettings(Bundle bu) {
		regionCat=bu.getInt("reg");
		b=regionCat==0?true:false;
		offMax=regionCat==1? 25:16;
	}

	private String getCompactUTC(boolean b, int o)
	{
		int Offset=offMax-o;
		final Calendar c = Calendar.getInstance();
		int hourNow=c.get(Calendar.HOUR_OF_DAY);    
		if (b && hourNow>20) c.add(java.util.Calendar.HOUR_OF_DAY, -(hourNow-20));
        if (b && hourNow< 6) c.add(java.util.Calendar.HOUR_OF_DAY, -(4+hourNow));
        
		c.add(java.util.Calendar.HOUR_OF_DAY, -Offset*1);
        c.add(java.util.Calendar.MINUTE, -20);
        c.getTime();
        int mHour=c.get(Calendar.HOUR_OF_DAY);       
        if (b && mHour>20) c.add(java.util.Calendar.HOUR_OF_DAY, -(mHour-20));
        if (b && mHour<6) c.add(java.util.Calendar.HOUR_OF_DAY, -(4+mHour));
           
        int zoneOffset = 	c.get(java.util.Calendar.ZONE_OFFSET);  	 
    	int dstOffset = 	c.get(java.util.Calendar.DST_OFFSET); 
    	c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    	
        c.getTime();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour=c.get(Calendar.HOUR_OF_DAY);   
    	
        return 	mYear+
        		String.format("%02d", mMonth) +
        		String.format("%02d", mDay)	  + "-" +
        		String.format("%02d", mHour)  + "00";
	}
}
