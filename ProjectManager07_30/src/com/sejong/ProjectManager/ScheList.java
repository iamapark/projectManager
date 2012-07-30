package com.sejong.ProjectManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheList extends ListActivity implements OnClickListener{
	/** Called when the activity is first created. */
	
	ListView sche_list;
	Button add_btn;
	
	InputStream is = null;
	
	Vector<String> dateVector;
	Vector<String> subjectVector;

	String userId = null;
	String projectName = null;
	String projectKey = null;
	String tagName="", date="", subject="";	
	
	ArrayList<String> listKey = new ArrayList<String>();
	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> listName;
	ArrayList<ScheListClass> m_orders = new ArrayList<ScheListClass>();
	
	ScheListClass s;
	TextView tt;
	

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
    					dateVector.add(date);
    					subjectVector.add(subject);
    					
    					date=""; subject="";
    					
    					handler.sendEmptyMessage(1);
    				}
    			}eventType = xpp.next();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	Log.i("subjectVector size", String.valueOf(subjectVector.size()));
    }
	
	Handler handler = new Handler(){
		private String scheList;

		public void handleMessage(Message msg){
			scheList = String.valueOf(msg.obj);
			String[] listString = scheList.split("/");
			listName = new ArrayList<String>();
			listKey = new ArrayList<String>();

			for(int i=0; i<listString.length; i++){
				if((i%2)==0){
					listKey.add(listString[i]);
				}else{
					listName.add(listString[i]);
				}
			}

			for(int i=0; i<subjectVector.size(); i++){
				items.add(subjectVector.get(i));				
			}
			/*
			if(items.get(0).equals("null")){
				String[] noDataList = {"d"};
				setListAdapter(new ArrayAdapter<String>(ScheList.this, android.R.layout.simple_list_item_1, noDataList));
			}
			*/
			if(items.get(0)!="null"){
				for(int i=0; i<subjectVector.size(); i++){
					m_orders.add(new ScheListClass(subjectVector.get(i), subjectVector.get(i)));
					Log.i("crazy", String.valueOf(i));
					ScheAdapter m_adapter = new ScheAdapter(ScheList.this, R.layout.row, m_orders);
					setListAdapter(m_adapter);
					//sche_list.setAdapter(new ArrayAdapter<String>(ScheList.this, android.R.layout.simple_list_item_1, listName));
				}
			}

		}
	};

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if(items.get(0).equals("null")){
			Toast.makeText(this, "d", Toast.LENGTH_SHORT).show();
		}else{
			Intent intent = new Intent(this, ScheList.class);
			intent.putExtra("userId", userId);
			intent.putExtra("scheName", listName.get(arg2));
			intent.putExtra("scheKey", listKey.get(arg2));
			intent.putExtra("projectKey", projectKey);
			startActivity(intent);
		}

	}

	public void onClick(View v) {
		Intent intent = new Intent(this, CreateSche.class);
		intent.putExtra("userId", userId);
		intent.putExtra("projectName", projectName);
		intent.putExtra("projectKey", projectKey);
		startActivity(intent);
	}



	private class ScheAdapter extends ArrayAdapter<ScheListClass> {

		private ArrayList<ScheListClass> items;

		public ScheAdapter(Context context, int textViewResourceId, ArrayList<ScheListClass> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Log.i("getView ", "호출");
			
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			s = items.get(position);
			Log.i("position", String.valueOf(position));

			if (s != null) {
				tt = (TextView) v.findViewById(R.id.toptext);
				ImageView iv = (ImageView) v.findViewById(R.id.bottomImage);
				iv.setFocusable(false);
				if (tt != null){
					tt.setText(s.getScheName());                            
					Log.i("getShceName", s.getScheName());
				}if(iv != null){
					iv.setTag(s.getScheKey());
					iv.setImageResource(s.getTrashImage());
				}
			}
			return v;
		}
	}

	public void clickImage(final View v){

		finish();

		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("scheNum", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(v.getTag()), "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/deletesche.jsp");
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

					//textview.setText(response.trim());
					wr.close();
					rd.close();

				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//deleteHandler.sendEmptyMessage(1);
			}
		}.start();
	}
	
	Handler deleteHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent = new Intent(ScheList.this, ScheList.class);
			intent.putExtra("userId", userId);
			intent.putExtra("projectKey", projectKey);
			intent.putExtra("projectName", projectName);
			startActivity(intent);
			getScheduleList();
		}
	};

}

