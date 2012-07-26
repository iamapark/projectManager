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

public class CreateIdea extends Activity implements OnClickListener{
	/** Called when the activity is first created. */
	
	EditText iname_edit;
	EditText contents_edit;
	Button ci_btn;

	String userId = null;
	String projectName = null;
	String projectKey = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_idea);
		
		Intent intent = getIntent();
		userId = intent.getExtras().getString("userId").toString();
		projectName = intent.getExtras().getString("projectName").toString();
		projectKey = intent.getExtras().getString("projectKey").toString();
		
		iname_edit = (EditText) findViewById(R.id.iname_edit);
		contents_edit = (EditText) findViewById(R.id.contents_edit);
		ci_btn = (Button) findViewById(R.id.ci_btn);
		ci_btn.setOnClickListener(this);

	}

	public void onClick(View v) {
		createProject();
	}
	
	private void createProject(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
					data += "&" + URLEncoder.encode("projectKey", "UTF-8") + "=" + URLEncoder.encode(projectKey, "UTF-8");
					data += "&" + URLEncoder.encode("ideaName", "UTF-8") + "=" + URLEncoder.encode(iname_edit.getText().toString(), "UTF-8");
					data += "&" + URLEncoder.encode("ideaContent", "UTF-8") + "=" + URLEncoder.encode(contents_edit.getText().toString(), "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/CreateIdea.jsp");
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

					findResponse(response);

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
	
	private void findResponse(String xmlString) {

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput( new StringReader ( xmlString ) );

			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				if(eventType == XmlPullParser.START_DOCUMENT) {

					Log.i("Start document","start");

				} else if(eventType == XmlPullParser.START_TAG) {

					Log.i("Start tag", ""+xpp.getName());

					if(xpp.getName().equals("result")) {

						xpp.next();

						String result = xpp.getText();
						
						if(result.equals("Complete")){
							handler.sendEmptyMessage(0);
						}else if(result.equals("ALREADY_EXIST_IDEA_NAME")){
							handler.sendEmptyMessage(1);
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

		Log.e("End", "End Program!!");

	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==0){ //아이디어 등록 성공
				Toast.makeText(CreateIdea.this, "아이디어가 등록되었습니다.", Toast.LENGTH_SHORT).show();
				
				//아이디어 리스트 화면으로 이동
				Intent intent = new Intent(CreateIdea.this, IdeaList.class);
	            intent.putExtra("userId", userId);
	            intent.putExtra("projectName", projectName);
	            intent.putExtra("projectKey", projectKey);
	            
	            finish();
	            startActivity(intent);
	    		
			}else if(msg.what==1){ //아이디어 이름 중복
				Toast.makeText(CreateIdea.this, "아이디어 이름이 중복됩니다.", Toast.LENGTH_SHORT).show();
			}
		}
	};
}