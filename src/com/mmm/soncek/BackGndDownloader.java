package com.mmm.soncek;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class BackGndDownloader extends AsyncTask<String, String, String>
{
	private String downPath;
	private String fn;
	public ImageActivity tata;
	
	public BackGndDownloader(ImageActivity act)
	{
		tata=act;
		downPath=tata.cachePath.toString()+"/";
	}
		
	public int downloadImage(String FileName, String sUrl) 
	{
		fn=FileName;
        int total=-1;
        try {       	
            URL url = new URL(sUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            
            InputStream input = new BufferedInputStream(url.openStream(),8192);
            OutputStream output = new FileOutputStream(FileName);
            byte data[] = new byte[1024*1024];
            int count;
            total=0;
            while ((count = input.read(data)) != -1) {
            	total+=count;
                output.write(data, 0, count);
                publishProgress(total+"");
            }
            output.flush();
            output.close();
            input.close();  
            if(total<110) 
            {
            	total=-1;
            	java.io.File file = 
            			new java.io.File(FileName);
            	file.delete();
            }
        		
        } catch (Exception e) {
        	Log.d("download","exception: "+e.getMessage());
        	return -2;
        }
        
        return total;
    }
	
	@Override
	protected void onProgressUpdate(String... progress) 
	{
		int parsed=Integer.parseInt(progress[0]);
		tata.mTextDebug.setText(" Prenašam "+(parsed/1000)+ "kB");//(Integer.parseInt(progress[0]
		//tata.mProgressBar.setProgress(parsed%100);
		
    }
	@Override
	protected String doInBackground(String... params) 
	{
		final String temp=params[0];
		int dBytes=downloadImage(params[0], params[1]);
		if (dBytes==-2)
			return " Ni povezave";
		if (dBytes==1)
			return " Sry, napaka";
		return " Prenesel " +dBytes/1000+ "kB";
	}
	
	
	@Override
	protected void onPreExecute()
	{
		tata.showProgressBar(true);
	}
	@Override
    protected void onPostExecute(String message) 
	{
		tata.showProgressBar(false);
		tata.mTextDebug.setText(message);
		tata.RefreshImage(fn);
		
    }
}
