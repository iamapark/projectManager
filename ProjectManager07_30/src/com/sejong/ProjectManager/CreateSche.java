package com.sejong.ProjectManager;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CreateSche extends Activity implements OnClickListener{
	/** Called when the activity is first created. */

	ToggleButton tb;        //알람 설정 On-Off 토글버튼
	Button cs_btn;          //스케줄 등록 버튼
	TextView mPickDate;    //스케줄 날짜
	EditText contents_edit; //스케줄 내용
	DatePicker scheduleDDay_dPicker;
	
	String userId;
	String projectName;
	String projectKey;
	String scheduleDDay;
	String tagName;
	String result;
	
	private int mYear, mMonth, mDay;
	static final int DATE_DIALOG_ID = 0;
	
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
		mPickDate = (TextView) findViewById(R.id.sname_edit);
		contents_edit = (EditText) findViewById(R.id.contents_edit);
		tb = (ToggleButton) this.findViewById(R.id.toggle);		
		
		mPickDate.setClickable(true);		
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
		
		mPickDate.setText("날짜를 선택하려면 클릭");
		
		mPickDate.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				showDialog(DATE_DIALOG_ID);				
			}
		}
		);
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);		
	}		

	private void updateDisplay()
	{
		mPickDate.setText(new StringBuilder()
		.append(mYear).append("-")
		.append(mMonth+1).append("-")
		.append(mDay).append(" "));
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener(){
		
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	
	protected Dialog onCreateDialog(int id){
		switch(id){
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			}
		return null;
	}
	
	public void onClick(View v) {
		scheduleDDay = mYear + "-" + mMonth + "-" + mDay;
		
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(scheduleDDay, "UTF-8");
					data += "&" + URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(mPickDate.getText().toString(), "UTF-8");
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
		
		finish();
		Intent intent = new Intent(this, ScheList.class);
		intent.putExtra("userId", userId);
		intent.putExtra("projectName", projectName);
		intent.putExtra("projectKey", projectKey);
		
		startActivity(intent);
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
    					result += xpp.getText();
    					Log.i("result", result);
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
}