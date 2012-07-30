package com.sejong.ProjectManager;

import java.io.BufferedReader;
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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IdeaList extends Activity implements OnClickListener, OnItemClickListener{
	/** Called when the activity is first created. */

	ListView idea_list;
	Button add_btn;
	String userId = null;
	String projectName = null;
	String projectKey = null;
	String ideaList = null;
	ArrayList<String> listKey = new ArrayList<String>();
	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> listName;
	ArrayList<IdeaListClass> m_orders = new ArrayList<IdeaListClass>();
	IdeaListClass i;
	TextView tt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idea_list);

		Intent intent = getIntent();
		userId = intent.getExtras().getString("userId").toString();
		projectName = intent.getExtras().getString("projectName").toString();
		projectKey = intent.getExtras().getString("projectKey").toString();

		idea_list = (ListView) findViewById(R.id.idea_list);
		idea_list.setOnItemClickListener(this);

		add_btn = (Button) findViewById(R.id.add_btn);
		add_btn.setOnClickListener(this);

		getIdeaList();
	}

	public void getIdeaList(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("projectKey", "UTF-8") + "=" + URLEncoder.encode(projectKey, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/getIdeaList.jsp");
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

					findList(response);

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

	private void findList(String xmlString) {

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
						Message msg = Message.obtain();
						msg.obj = result;

						handler.sendMessage(msg);
						Log.i("result IdeaList", result);

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

	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			ideaList = String.valueOf(msg.obj);
			String[] listString = ideaList.split("/");
			listName = new ArrayList<String>();
			listKey = new ArrayList<String>();

			for(int i=0; i<listString.length; i++){
				if((i%2)==0){
					listKey.add(listString[i]);
				}else{
					listName.add(listString[i]);
				}
			}

			for(int i=0; i<listString.length; i++){
				items.add(listString[i]);				
			}
			/*
			if(items.get(0).equals("null")){
				String[] noDataList = {"�깅줉���꾩씠�붿뼱 �놁쓬"};
				idea_list.setAdapter(new ArrayAdapter<String>(IdeaList.this, android.R.layout.simple_list_item_1, noDataList));
			}
			*/
			if(items.get(0)!="null"){
				for(int i=0; i<listName.size(); i++){
					m_orders.add(new IdeaListClass(listName.get(i), listKey.get(i)));
					IdeaAdapter m_adapter = new IdeaAdapter(IdeaList.this, R.layout.row, m_orders);
					idea_list.setAdapter(m_adapter);
					//idea_list.setAdapter(new ArrayAdapter<String>(IdeaList.this, android.R.layout.simple_list_item_1, listName));
				}
			}

		}
	};

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if(items.get(0).equals("null")){
			Toast.makeText(this, "�꾩씠�붿뼱瑜��깅줉�섎젮硫��곗긽�⑥쓽 �뷀븯湲�+) 踰꾪듉���곗튂�섏꽭��", Toast.LENGTH_SHORT).show();
		}else{
			Intent intent = new Intent(this, Idea.class);
			intent.putExtra("userId", userId);
			intent.putExtra("ideaName", listName.get(arg2));
			intent.putExtra("ideaKey", listKey.get(arg2));
			intent.putExtra("projectKey", projectKey);
			startActivity(intent);
		}

	}

	public void onClick(View v) {
		Intent intent = new Intent(this, CreateIdea.class);
		intent.putExtra("userId", userId);
		intent.putExtra("projectName", projectName);
		intent.putExtra("projectKey", projectKey);
		startActivity(intent);
	}

	class IdeaListClass{
		String IdeaName = "";
		String IdeaKey = "";
		int trashImage = R.id.bottomImage;

		public IdeaListClass(String IdeaName, String IdeaKey){
			this.IdeaName = IdeaName;
			this.IdeaKey = IdeaKey;
		}

		public String getIdeaName(){
			return IdeaName;
		}
		public String getIdeaKey(){
			return IdeaKey;
		}
		public int getTrashImage(){
			return trashImage;
		}
	}

	private class IdeaAdapter extends ArrayAdapter<IdeaListClass> {

		private ArrayList<IdeaListClass> items;

		public IdeaAdapter(Context context, int textViewResourceId, ArrayList<IdeaListClass> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			i = items.get(position);
			Log.i("position", String.valueOf(position));

			if (i != null) {
				tt = (TextView) v.findViewById(R.id.toptext);
				ImageView iv = (ImageView) v.findViewById(R.id.bottomImage);
				iv.setFocusable(false);
				if (tt != null){
					tt.setText(i.getIdeaName());                            

				}if(iv != null){
					iv.setTag(i.getIdeaKey());
					iv.setImageResource(i.getTrashImage());
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
					data = URLEncoder.encode("ideaNum", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(v.getTag()), "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/deleteIdea.jsp");
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

				deleteHandler.sendEmptyMessage(1);
			}
		}.start();
	}
	
	Handler deleteHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent = new Intent(IdeaList.this, IdeaList.class);
			intent.putExtra("userId", userId);
			intent.putExtra("projectKey", projectKey);
			intent.putExtra("projectName", projectName);
			startActivity(intent);
			getIdeaList();
		}
	};

}