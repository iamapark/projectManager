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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CreateSche extends Activity implements OnClickListener{
	/** Called when the activity is first created. */

	ToggleButton tb;        //�˶� ���� On-Off ��۹�ư
	Button cs_btn;          //������ ��� ��ư
	EditText sname_edit;    //������ ����
	EditText contents_edit; //������ ����
	DatePicker scheduleDDay_dPicker;
	
	String userId;
	String projectName;
	String projectKey;
	String scheduleDDay;
	String tagName; String result;
	
	InputStream is;
	Vector<String> stateVector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_sche);
		
		Intent intent = getIntent();
		userId = intent.getExtras().getString("userId");
		projectName = intent.getExtras().getString("projectName");
		projectKey = intent.getExtras().getString("projectKey");
		
		cs_btn = (Button) findViewById(R.id.cs_btn);
		sname_edit = (EditText) findViewById(R.id.sname_edit);
		contents_edit = (EditText) findViewById(R.id.contents_edit);
		tb = (ToggleButton) this.findViewById(R.id.toggle);
		scheduleDDay_dPicker = (DatePicker) findViewById(R.id.scheduleDDay);
		
		
		cs_btn.setOnClickListener(this);
		tb.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tb.isChecked())
					tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));				
				else
					tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));				
			}
		});

	}

	public void onClick(View v) {
		scheduleDDay = scheduleDDay_dPicker.getYear() + "-" + (scheduleDDay_dPicker.getMonth()+1) + "-" + scheduleDDay_dPicker.getDayOfMonth();
		
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(scheduleDDay, "UTF-8");
					data += "&" + URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(sname_edit.getText().toString(), "UTF-8");
					data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(contents_edit.getText().toString(), "UTF-8");
					data += "&" + URLEncoder.encode("projectKey", "UTF-8") + "=" + URLEncoder.encode(projectKey, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/CreateSchedule.jsp");
					URLConnection conn = url.openConnection();

					// If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();

					is = conn.getInputStream();
					
					findState();
					
					is.close();
					wr.close();
				
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void findState(){
    	stateVector = new Vector<String>();
    	    	
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
    				if(tagName.equals("result")){
    					result = xpp.getText();
    					Log.i("schdule create result", result);
    					
    					if(result.equals("Complete")){
    						handler.sendEmptyMessage(1);
    					}else{
    						handler.sendEmptyMessage(2);
    					}
    				}
    			}else if(eventType==XmlPullParser.END_TAG){
    				tagName = xpp.getName();
    				if(tagName.equals("item")){
    					stateVector.add(result);
    					
    					result=""; 
    				}
    			}eventType = xpp.next();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
				if(msg.what==1){
					
					Toast.makeText(CreateSche.this, "새로운 스케줄이 생성되었습니다.", Toast.LENGTH_SHORT).show();
					
					finish();
					Intent intent = new Intent(CreateSche.this, ScheList.class);
					intent.putExtra("userId", userId);
					intent.putExtra("projectName", projectName);
					intent.putExtra("projectKey", projectKey);
					
					startActivity(intent);
				}else{
					Toast.makeText(CreateSche.this, "스케줄 생성이 실패했습니다.", Toast.LENGTH_SHORT).show();
				}
		}
	};
}