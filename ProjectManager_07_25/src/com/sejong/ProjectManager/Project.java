package com.sejong.ProjectManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Project extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	TextView ddayView;
	TextView Day;
	TextView projectSubject;
	Button ideaM_btn;
	Button scheduleM_btn;

	ProgressThread pThread = null;
	float maxProgress = 0;
	private ProgressBar pBar = null;

	private static final int MSG_PROGRESS = 1;		// pBar의 진행을 처리하기 위한 메시지 코드
	
	String userId = null;
	String projectName = null;
	String projectKey = null;
	String DDay = "";
	String toDay = "";
	int DDayYear, DDayMonth, DDayDay;

	GetProjectDDay thread;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project);
		
		Intent intent = getIntent();
		userId = intent.getExtras().getString("id").toString();
		projectName = intent.getExtras().getString("projectName").toString();
		projectKey = intent.getExtras().getString("projectKey").toString();
		
		
		ideaM_btn = (Button) findViewById(R.id.ideaM_btn);
		ideaM_btn.setOnClickListener(this);
		scheduleM_btn = (Button) findViewById(R.id.scheduleM_btn);
		scheduleM_btn.setOnClickListener(this);
		
		projectSubject = (TextView) findViewById(R.id.projectSubject);
		projectSubject.setText(projectName);
		
		// D-Day TextView
		ddayView = (TextView) findViewById(R.id.d_Day);
		ddayView.setTextColor(Color.DKGRAY);
		ddayView.setTypeface(Typeface.SANS_SERIF);
		ddayView.setTextSize(70);		
		
		getProejctDDay();

		Log.i("state", String.valueOf(thread.getState()));
		
		while(thread.getState()!=Thread.State.TERMINATED){
			if(thread.getState()!=Thread.State.TERMINATED){
				Log.i("getStateIf", String.valueOf(thread.getState()));
			}else{
				Log.i("getStateIfElse", String.valueOf(thread.getState()));
			}
		}
		
		try {
			Log.i("cal - state", String.valueOf(thread.getState()));
			cal();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class GetProjectDDay extends Thread{
		public void run(){
			String data;
			try {
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
				data += "&" + URLEncoder.encode("projectKey", "UTF-8") + "=" + URLEncoder.encode(projectKey, "UTF-8");
				data += "&" + URLEncoder.encode("projectName", "UTF-8") + "=" + URLEncoder.encode(projectName, "UTF-8");

				// Send data					
				URL url = new URL("http://iamapark.cafe24.com/getProjectDDay.jsp");
				URLConnection conn = url.openConnection();

				// If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
				String line = null;
				String response = "";

				while ((line = rd.readLine()) != null) {
					response += line;
				}

				findName(response);

				//textview.setText(response.trim());
				wr.close();
				rd.close();
			
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private  void getProejctDDay(){
		thread = new GetProjectDDay();
		thread.start();
	}
	
	private void cal() throws ParseException{
		

		int cd = (int) CalDate(DDayYear,DDayMonth,DDayDay);
		
		if(cd > 0)
			ddayView.setText("D+"+cd);
		else if(cd == 0){
			ddayView.setTextColor(Color.RED);			
			ddayView.setText("D-DAY");
		}
		else
			ddayView.setText("D"+cd);
			Calendar oCalendar = Calendar.getInstance( );  // 현재 날짜/시간 등의 각종 정보 얻기
			String TODAY = oCalendar.get(Calendar.YEAR) + "-" + (oCalendar.get(Calendar.MONTH) + 1) + "-" + oCalendar.get(Calendar.DAY_OF_MONTH);
					
			String format = "yyyy-MM-dd";
		    SimpleDateFormat sdf = new SimpleDateFormat(format);
		    float sDate;
		    float eDate;
		    float tDate;
		
		    sDate = sdf.parse(toDay).getTime();
	        eDate = sdf.parse(DDay).getTime();
	        tDate = sdf.parse(TODAY).getTime();

	        Toast.makeText(Project.this, sDate + "/" + eDate + "/" + tDate, Toast.LENGTH_LONG).show();
			
	        maxProgress = (((tDate - sDate) / (eDate - sDate)) * 100); 

	        Log.i("sDate", toDay + String.valueOf(sDate));
	        Log.i("eDate", DDay + String.valueOf(eDate));
	        Log.i("tDate", TODAY + String.valueOf(tDate));
	        
	        Log.i("(tDate - sDate)", String.valueOf((tDate - sDate)));
	        Log.i("(eDate - sDate)", String.valueOf((eDate - sDate)));
	        
	        // ProgressBar
			pBar = (ProgressBar) findViewById(R.id.progressBar);
			//maxProgress = 30;
			// pBar의 최대값 -->maxProgress ((현재날짜-시작날짜)/(디데이-시작날짜)*100) 나중에 날짜받아와서수정해야함!

			// ProgressBar의 진행을 처리할 스레드 생성 및 시작
			pThread = new ProgressThread();
			pThread.start();	
	}

	
	private class ProgressThread extends Thread			// ProgressThread: 프로그레스바의 진행을 처리하는 스레드
	{
		ProgressHandler progressHandler = new ProgressHandler();

		public void run()
		{
			for(int i = 0; i <= maxProgress; i++)
			{
				Message msg = progressHandler.obtainMessage(MSG_PROGRESS, i, 0);
				progressHandler.sendMessage(msg);
			}
		}
	}

	private class ProgressHandler extends Handler		// ProgressHandler: 프로그레스바의 업데이트를 담당하는 핸들러
	{
		public void handleMessage(Message msg)
		{
			int curProgress = msg.arg1;

			switch(msg.what)
			{
			case MSG_PROGRESS:
				pBar.setProgress(curProgress);				
				break;
			default:
				break;
			}
		}
	}

	public int CalDate(int dYear, int dMonth, int dDay){
		try{
			Calendar today = Calendar.getInstance();
			Calendar dday = Calendar.getInstance();

			dday.set(dYear, dMonth, dDay);

			long day = dday.getTimeInMillis()/86400000;
			long tday = today.getTimeInMillis()/86400000;

			long count = tday - day + 30;

			return (int) count+1;
		}
		catch(Exception e)
		{
			e.printStackTrace();	
			return -1;
		}
	}
	
	private void findName(String xmlString) {
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput( new StringReader ( xmlString ) );

			int eventType = xpp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if(eventType == XmlPullParser.START_DOCUMENT) {

					Log.i("Start document","start document");

				} else if(eventType == XmlPullParser.START_TAG) {

					Log.i("Start tag", ""+xpp.getName());

					if(xpp.getName().equals("day")) {

						xpp.next();

						String result = xpp.getText();
						Message msg = Message.obtain();
						msg.obj = result;
						
						Log.i("Thread getDDay", "aha");
						
						String[] day = result.split("/");
						DDay = day[0];
						toDay = day[1];
						
						DDayYear = Integer.parseInt(DDay.split("-")[0]);
						DDayMonth = Integer.parseInt(DDay.split("-")[1]);
						DDayDay = Integer.parseInt(DDay.split("-")[2]);
						
						Log.i("DDayYear", String.valueOf(DDayYear));
						Log.i("DDayMonth", String.valueOf(DDayMonth));
						Log.i("DDayDay", String.valueOf(DDayDay));
						
						break;

					}
				} else if(eventType == XmlPullParser.END_TAG) {

					Log.i("End tag", ""+xpp.getName());

				} else if(eventType == XmlPullParser.TEXT) {

					Log.i("TEXT", ""+xpp.getText());

				}

				eventType = xpp.next();

			}

		} catch (Exception e) {

			Log.e("error", ""+e.getMessage());

		}

		Log.e("end program", "The End!!");

	}
	
	public void onClick(View v) {
		Intent intent;

		if(v.getId()==R.id.scheduleM_btn){
			intent = new Intent(this, ScheList.class);
			intent.putExtra("userId", userId);
			intent.putExtra("projectName", projectName);
			intent.putExtra("projectKey", projectKey);
	        startActivity(intent);
		}else if(v.getId()==R.id.ideaM_btn){
			intent = new Intent(this, IdeaList.class);
			intent.putExtra("userId", userId);
			intent.putExtra("projectName", projectName);
			intent.putExtra("projectKey", projectKey);
	        startActivity(intent);
		}
		
	}
}