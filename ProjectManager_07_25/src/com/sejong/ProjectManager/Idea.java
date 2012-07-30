package com.sejong.ProjectManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Idea extends Activity implements OnClickListener{
	/** Called when the activity is first created. */
	
	TextView ideaName_txt;
	TextView ideaContent_txt;
	Button replySend;
	EditText replyContent;
	ListView replyList;
	LinearLayout sendLinearLayout;
	
	String userId = "";
	String ideaNum = "";
	String ideaName = "";
	String ideaWriter = "";
	String replyMessage; String replyMessageUserId;
	ArrayList<String> arrayIdeaReply = new ArrayList<String>(); //
	ArrayList<String> arrayIdeaReplyUserId = new ArrayList<String>(); //
	ListAdapter replyAdapter;
		
	InputStream is;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idea);
		
		Intent intent = getIntent();
		userId = intent.getExtras().getString("userId");
		ideaNum = intent.getExtras().getString("ideaKey");
		ideaName = intent.getExtras().getString("ideaName");
		
		ideaName_txt = (TextView) findViewById(R.id.ideaSubject);
		ideaContent_txt = (TextView) findViewById(R.id.ideaContent);
		replySend = (Button) findViewById(R.id.replySend);
		replyContent = (EditText) findViewById(R.id.replyContent);
		replyList = (ListView) findViewById(R.id.replyList);
		replySend.setOnClickListener(this);
		replyContent.requestFocus();
		
		
		
		ideaName_txt.setText(ideaName);
		
		getIdeaInformation();
		getIdeaReply();
	}
	
	private void getIdeaReply(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("ideaNum", "UTF-8") + "=" + URLEncoder.encode(ideaNum, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/getIdeaReply.jsp");
					URLConnection conn = url.openConnection();

					// If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data);
					wr.flush();

					// Get the response
					is = conn.getInputStream();
					
					findReply();

					//textview.setText(response.trim());
					wr.close();
					is.close();
				
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void getIdeaInformation(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("ideaNum", "UTF-8") + "=" + URLEncoder.encode(ideaNum, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/getIdea.jsp");
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

					findIdea(response);

					//textview.setText(response.trim());
					wr.close();
					rd.close();
				
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void findReply() {
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput( is, "utf-8" );

			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				if(eventType == XmlPullParser.START_DOCUMENT) {

					Log.i("Start document","start document");

				} else if(eventType == XmlPullParser.START_TAG) {

					Log.i("Start tag", ""+xpp.getName());

					if(xpp.getName().equals("Content")) {

						xpp.next();
						
						replyMessage = xpp.getText();
						Log.i("Idea Reply Content", replyMessage);
						
					}else if(xpp.getName().equals("userId")){
						xpp.next();
						
						replyMessageUserId = xpp.getText();
						Log.i("replyMessageUserId", replyMessageUserId);
					}
				} else if(eventType == XmlPullParser.END_TAG) {

					Log.i("End tag", ""+xpp.getName());
					
					if(xpp.getName().equals("reply")){
						Log.i("arrayList added", xpp.getName());
						Log.i("replyMessageUserId", replyMessageUserId);
						Log.i("replyMessage", replyMessage);
						arrayIdeaReply.add(replyMessage);
						arrayIdeaReplyUserId.add(replyMessageUserId);
						Log.i("arrayIdeaReply size", String.valueOf(arrayIdeaReply.size()));
						Log.i("arrayIdeaReplyUserId size", String.valueOf(arrayIdeaReplyUserId.size()));
						replyMessage = ""; replyMessageUserId="";
					}
					

				} else if(eventType == XmlPullParser.TEXT) {

					Log.i("TEXT", ""+xpp.getText());

				}
				
				eventType = xpp.next();
				
				
			}
			
			
			

		} catch (Exception e) {

			Log.e("error", ""+e.getMessage());

		}
		
		getReplyHandler.sendEmptyMessage(1);

		Log.e("end program", "The End!!");
	}
	
	private void findIdea(String xmlString) {
		
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

					if(xpp.getName().equals("content")) {

						xpp.next();

						String result = xpp.getText();
						Message msg = Message.obtain();
						msg.obj = result;
						
						handler.sendMessage(msg);
			
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
	


	public void onClick(View arg0) {

		final String Content = replyContent.getText().toString();
		
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
					data += "&" + URLEncoder.encode("ideaNum", "UTF-8") + "=" + URLEncoder.encode(ideaNum, "UTF-8");
					data += "&" + URLEncoder.encode("replyContent", "UTF-8") + "=" + URLEncoder.encode(Content, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/registryReply.jsp");
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

					findState(response);

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
	
	private void findState(String xmlString) {
		
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
						Message msg = Message.obtain();
						msg.obj = xpp.getText();
						
						replySendHandler.sendMessage(msg);
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
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			Log.i("handler msg : ", String.valueOf(msg.obj));
			ideaContent_txt.setText(String.valueOf(msg.obj));
		}
	};
	
	Handler replySendHandler = new Handler(){
		public void handleMessage(Message msg){
			Log.i("return result", String.valueOf(msg.obj));
			if(String.valueOf(msg.obj).equals("1")){ //
				Log.i("���", "��� �ޱ� ����");
			}else{
				Toast.makeText(Idea.this, "Failed!!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	Handler getReplyHandler = new Handler(){
		public void handleMessage(Message msg){
			//Log.i("ListView", "start");
			for(int i=0; i<arrayIdeaReply.size(); i++){
				Log.i("arrayIdeaReply Contents : " + i, arrayIdeaReply.get(i));
			}
			replyAdapter = new ArrayAdapter<String>(Idea.this, android.R.layout.simple_list_item_1, arrayIdeaReply);	
			replyList.setAdapter(replyAdapter);
			Log.i("����Ʈ�� ���", "��");
			
			sendLinearLayout = new LinearLayout(Idea.this);
		}
	};
}