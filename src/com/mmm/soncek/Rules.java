package com.mmm.soncek;

import java.io.File;

import android.os.Bundle;

public interface Rules {
	public void Initialize(ImageActivity ia, File cf);
	public boolean isInitialized();
	public int getProgress(int Offset);
	public int getNoImages();
	public int getInitialOffset();
	public String GetUrl(int Offset);
	public String getFileName(int Offset);
	public String getLocalTime(int Offset);
	
	public String[] getOptionList();
	public int getOptionsNumber();
	public void setOption(int opt);
	
	public Bundle getSettings();
	public void setSettings(Bundle settings);
}
