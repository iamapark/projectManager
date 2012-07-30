package com.sejong.ProjectManager;


import com.google.android.gcm.GCMRegistrar;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.sejong.ProjectManager.DB.LoginDBHandler;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	EditText id;
	EditText pwd;
	Button loginButton;
	LoginDBHandler dbhandler;
	
	String flag = "";
		

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		id = (EditText) findViewById(R.id.id_edit);
		pwd = (EditText) findViewById(R.id.pwd_edit);
		loginButton = (Button) findViewById(R.id.login_btn);
		
		Intent intent = getIntent();
		flag = intent.getExtras().getString("flag");

		loginButton.setOnClickListener(this);

		
		checkLogin();
	}
	
	private void checkLogin(){
        
		/*
		 * dbhandler = LoginDBHandler.open(this);
        Cursor cursor = dbhandler.select(1);
        startManagingCursor(cursor);
        
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "비 로그인 상태", Toast.LENGTH_LONG).show();
        } else {            
            String name = cursor.getString(cursor.getColumnIndex("memberid"));
            Toast.makeText(this, "로그인 되어있는 아이디 " + name, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ProjectList.class);
            intent.putExtra("id", id.getText().toString());
            startActivity(intent);
        }
        */
		
        //dbhandler.close();
	}

	public void onClick(View arg0) {
		
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(id.getText().toString(), "UTF-8");
					data += "&" + URLEncoder.encode("userpwd", "UTF-8") + "=" + URLEncoder.encode(pwd.getText().toString(), "UTF-8");

					// Send data
					URL url = new URL("http://iamapark.cafe24.com/MemberLogin.jsp");
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
					Log.i("Start document","占쏙옙占쏙옙占승깍옙");
				} else if(eventType == XmlPullParser.START_TAG) {
					Log.i("Start tag", ""+xpp.getName());
					if(xpp.getName().equals("result")) {
						xpp.next();
						String result = xpp.getText();
						if(result.equals("ID_DOES_NOT_EXIST")){
							handler.sendEmptyMessage(1);
							
						}
						else if(result.equals("PASSWORD_DOES_NOT_MATCHED")){
							handler.sendEmptyMessage(2);
							
						}
						else if(result.equals("Completed")){
							handler.sendEmptyMessage(3);
							Toast.makeText(Login.this, id.getText().toString()+"로 로그인 성공.", Toast.LENGTH_SHORT).show();
						}
						break;
					}
				}
				else if(eventType == XmlPullParser.END_TAG) {
					Log.i("End tag", ""+xpp.getName());
				}
				else if(eventType == XmlPullParser.TEXT) {
					Log.i("TEXT", ""+xpp.getText());
				}
				eventType = xpp.next();
			}
		}
		catch (Exception e) {
			Log.e("error", ""+e.getMessage());
		}
		Log.e("end", "end program");
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			
			if(msg.what==1){
				Toast.makeText(Login.this, "ID가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
			}else if(msg.what==2){
				Toast.makeText(Login.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(Login.this, id.getText().toString()+"로 로그인 성공.", Toast.LENGTH_SHORT).show();
				
				if(flag.equals("1")){
					Intent intent = new Intent(Login.this, ProjectList.class);
		            intent.putExtra("id", id.getText().toString());
		            startActivity(intent);
				}else{
					Intent intent = new Intent(Login.this, Idea.class);
					Intent innt = getIntent();
					
					intent.putExtra("ideaKey", innt.getExtras().getString("ideaNum"));
					intent.putExtra("ideaName", innt.getExtras().getString("ideaName"));
					intent.putExtra("userId", id.getText().toString());
					
					startActivity(intent);
				}
			}
		    
		}
	};
	

}