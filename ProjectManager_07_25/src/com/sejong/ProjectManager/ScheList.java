package com.sejong.ProjectManager;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScheList extends Activity implements OnClickListener{
	/** Called when the activity is first created. */
	
	Button add_btn;
	
	InputStream is = null;
	
	Vector<String> dateVector;
	Vector<String> subjectVector;

	String userId = null;
	String projectName = null;
	String projectKey = null;
	String tagName="", date="", subject="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sche_list);
		
		add_btn = (Button) findViewById(R.id.add_btn);
		add_btn.setOnClickListener(this);

		Intent intent = getIntent();
		userId = intent.getExtras().getString("userId");
		projectName = intent.getExtras().getString("projectName");
		projectKey = intent.getExtras().getString("projectKey");
		
		getScheduleList();
		
	}
	
	private void getScheduleList(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
					data += "&" + URLEncoder.encode("projectKey", "UTF-8") + "=" + URLEncoder.encode(projectKey, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/getScheduleList.jsp");
					URLConnection conn = url.openConnection();

					// If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();

					// Get the response
					is = conn.getInputStream();


					findList();

					//textview.setText(response.trim());
					wr.close();
					is.close();
				
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void findList(){
    	dateVector = new Vector<String>();
    	subjectVector = new Vector<String>();
    	
    	try{
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    		factory.setNamespaceAware(true);
    		XmlPullParser xpp = factory.newPullParser();
    		
    		xpp.setInput(is, "euc-kr");
    		
    		int eventType = xpp.getEventType();
    		
    		while(eventType!=XmlPullParser.END_DOCUMENT){
    			if(eventType==XmlPullParser.START_TAG){
    				tagName = xpp.getName();
    				Log.i("tagName", tagName);
    			}else if(eventType==XmlPullParser.TEXT){
    				if(tagName.equals("Date")){
    					date += xpp.getText();
    					Log.i("Date", date);
    				}else if(tagName.equals("Subject")){
    					subject += xpp.getText();
    					Log.i("Subject", subject);
    				}
    			}else if(eventType==XmlPullParser.END_TAG){
    				tagName = xpp.getName();
    				if(tagName.equals("item")){
    					dateVector.add(date.replaceAll("-" + date.split("-")[2], ""));
    					subjectVector.add(subject);
    					
    					date=""; subject="";
    				}
    			}eventType = xpp.next();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	for(int i=0; i<subjectVector.size(); i++){
    		Log.i("dateVector:" + i,dateVector.get(i));
    	}
    	Log.i("subjectVector size", String.valueOf(subjectVector.size()));
    }

	public void onClick(View arg0) {
		Intent intent = new Intent(this, CreateSche.class);
		intent.putExtra("userId", userId);
		intent.putExtra("projectName", projectName);
		intent.putExtra("projectKey", projectKey);
		
		startActivity(intent);		
	}
}