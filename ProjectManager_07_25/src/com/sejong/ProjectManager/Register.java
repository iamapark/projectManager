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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	String txtId = null, txtPwd = null;
	EditText id, pwd, email;
	Button create;
	SQLiteDatabase db = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		id = (EditText)findViewById(R.id.id_edit);
		pwd = (EditText)findViewById(R.id.pwd_edit);
		email = (EditText)findViewById(R.id.email_edit);
		create = (Button)findViewById(R.id.create_btn);

		create.setOnClickListener(this);
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

					Log.i("Start document","�����±�");

				} else if(eventType == XmlPullParser.START_TAG) {

					Log.i("Start tag", ""+xpp.getName());

					if(xpp.getName().equals("result")) {

						xpp.next();

						String result = xpp.getText();

						if(result.equals("Failed")){
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

		} 
		catch (Exception e) {
			Log.e("����", ""+e.getMessage());
		}
		Log.e("����", "���α׷� ����");

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = getIntent();
		switch(v.getId()){
		
		case R.id.create_btn:
			
			new Thread(){
				public void run(){
					String data;
					
					try {
						data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(id.getText().toString(), "UTF-8");
						data += "&" + URLEncoder.encode("userpwd", "UTF-8") + "=" + URLEncoder.encode(pwd.getText().toString(), "UTF-8");
						data += "&" + URLEncoder.encode("useremail", "UTF-8") + "=" + URLEncoder.encode(email.getText().toString(), "UTF-8");

						// Send data
						URL url = new URL("http://iamapark.cafe24.com/MemberJoin.jsp");
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
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			break;				
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==1){
				Toast.makeText(Register.this, "동일한 아이디가 이미 존재합니다.", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(Register.this, "계정 생성이 완료되었습니다.", Toast.LENGTH_LONG).show();
				finishFromChild(getParent());
			}
		}
	};
	};