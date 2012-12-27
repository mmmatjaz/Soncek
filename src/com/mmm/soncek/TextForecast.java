package com.mmm.soncek;

import java.io.IOException;

import mmmatjaz.sonce.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

public class TextForecast extends Activity
{
	private final String url=	
			"http://www.vreme.si/uploads/probase/www/fproduct/text/sl/fcast_si_text.html";
	private WebView mWeb;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
     // Set the layout for this activity
        setContentView(R.layout.browserview);
        mWeb=(WebView) findViewById(R.id.webView1);
       

    }
	@Override
    public void onResume() 
	{
		super.onResume();  
		mWeb.startAnimation(AnimationUtils.loadAnimation(TextForecast.this, android.R.anim.fade_in));
		mWeb.setVisibility(View.VISIBLE);
		 mWeb.setBackgroundColor(Color.BLACK);
	        try {
				Document doc = Jsoup.connect(url).get();
				doc.head().getElementsByTag("link").remove();
				doc.head().getElementsByTag("link").remove();
				doc.head().getElementsByTag("body").attr("bgcolor","black");
				//doc.head().getElementsByTag("body").attr("background","file:///android_asset/bg2.png");
				doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "style.css");
				String htmldata = doc.outerHtml();
				mWeb.loadDataWithBaseURL("file:///android_asset/.", htmldata, "text/html", "UTF-8", null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

