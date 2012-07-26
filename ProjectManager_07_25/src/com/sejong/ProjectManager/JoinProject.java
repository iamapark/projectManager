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
import android.widget.EditText;
import android.widget.Toast;

public class JoinProject extends Activity implements OnClickListener{
	/** Called when the activity is first created. */

	EditText key_edit;
	Button join_btn;
	String key; String userId="iamapark";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		
		key_edit = (EditText) findViewById(R.id.key_edit);
		join_btn = (Button) findViewById(R.id.join_btn);
		join_btn.setOnClickListener(this);
		
		Intent intent = getIntent();
		userId = intent.getExtras().getString("id").toString();
	}

	public void onClick(View v) {
 		key = key_edit.getText().toString();
 		
 		new Thread(){
 			public void run(){
 				String data;
				try {
					data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
					data += "&" + URLEncoder.encode("projectKeyValue", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/JoinProject.jsp");
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

						if(result.equals("USER_ID_DUPLICATED")){
							handler.sendEmptyMessage(0);
						}else if(result.equals("YOUR_TEAM_IS_FULL")){
							handler.sendEmptyMessage(1);
						}else if(result.equals("PROJECTKEY_DOES_NOT_EXIST")){
							handler.sendEmptyMessage(2);
						}else if(result.equals("Completed")){
							handler.sendEmptyMessage(3);
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

			Log.e("Error", ""+e.getMessage());

		}

		Log.e("end", "end Program!!");

	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			int flag = msg.what;
			if(flag==0)
				Toast.makeText(getBaseContext(), "이미 가입된 프로젝트입니다.", Toast.LENGTH_LONG).show();
			else if(flag==1)
				Toast.makeText(getBaseContext(), "프로젝트가 꽉 찼습니다.", Toast.LENGTH_LONG).show();
			else if(flag==2)
				Toast.makeText(getBaseContext(), "해당 프로젝트 키가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
			else if(flag==3){
				Toast.makeText(getBaseContext(), "프로젝트 참가 성공!!", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(JoinProject.this, ProjectList.class);
	            intent.putExtra("id", userId);
	            startActivity(intent);
			}
		}
	};
}