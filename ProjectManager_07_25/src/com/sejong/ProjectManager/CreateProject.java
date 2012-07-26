package com.sejong.ProjectManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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

public class CreateProject extends Activity {
	/** Called when the activity is first created. */

	EditText projectName;
	DatePicker projectDDay;
	Button createProject_btn;
	String userid = null;
	
	String dDay = null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setContentView(R.layout.create_project);
		setContentView(R.layout.create_project);

		projectName = (EditText) findViewById(R.id.projectName_txt);
		projectDDay = (DatePicker) findViewById(R.id.projectDDay);
		createProject_btn = (Button) findViewById(R.id.create_btn);
		
		Intent intent = getIntent();
		userid = intent.getExtras().getString("id").toString();
		
		
		
		createProject_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				dDay = projectDDay.getYear() + "-" + (projectDDay.getMonth()+1) + "-" + projectDDay.getDayOfMonth();

				new Thread(){
					public void run(){
						String data;
						try {
							data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
							data += "&" + URLEncoder.encode("projectName", "UTF-8") + "=" + URLEncoder.encode(projectName.getText().toString(), "UTF-8");
							data += "&" + URLEncoder.encode("dDay", "UTF-8") + "=" + URLEncoder.encode(dDay, "UTF-8");

							// Send data					
							URL url = new URL("http://iamapark.cafe24.com/CreateProject.jsp");
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
				}.start();
				
			}
		});		
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

					if(xpp.getName().equals("result")) {

						xpp.next();

						String result = xpp.getText();

						if(result.equals("Project_Name_Alerady_Exist")){
							handler.sendEmptyMessage(1);
						}else if(result.equals("Completed")){
							handler.sendEmptyMessage(2);
						}

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

		Log.e("end", "end program");

	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(msg.what==1){
				Toast.makeText(CreateProject.this, "동일한 이름의 프로젝트가 있습니다.", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(CreateProject.this, "프로젝트 생성 완료.", Toast.LENGTH_LONG).show();

				finish();
				Intent intent = new Intent(CreateProject.this, ProjectList.class);
	            intent.putExtra("id", userid);
	            startActivity(intent);
			}
		}
	};
}