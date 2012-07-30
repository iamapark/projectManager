package com.sejong.ProjectManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.gcm.GCMRegistrar;
import com.sejong.ProjectManager.DB.LoginDBHandler;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectList extends ListActivity implements OnClickListener {

	Button createProject_btn, joinProject_btn;
	Button getProjectList_btn;
	ListView project_list;
	TextView txt;
	String userId = null;
	String projectList = null;
	String deviceRegId = null;
	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> listKey = new ArrayList<String>();
	ArrayList<String> listName;
	ArrayList<ProjectListClass> m_orders = new ArrayList<ProjectListClass>();
	LoginDBHandler dbhandler;
	ProjectListClass p;
	TextView tt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_list);

		createProject_btn = (Button) findViewById(R.id.createProject_btn);
		joinProject_btn = (Button) findViewById(R.id.joinProject_btn);

		createProject_btn.setOnClickListener(this);
		joinProject_btn.setOnClickListener(this);


		getRegId();
		getMemberId();
		registRegId();
	}

	@Override
	public void finishActivity(int requestCode) {
		// TODO Auto-generated method stub
		super.finishActivity(requestCode);
	}

	public void getMemberId(){

		dbhandler = LoginDBHandler.open(this);

		Intent intent = getIntent();
		userId = intent.getExtras().getString("id").toString();

		getProjectList();
	}

	public void getProjectList(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/getProjectList.jsp");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void findList(String xmlString) {
		int cnt = 0;

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
					Log.i("cnt", String.valueOf(cnt));

					if(xpp.getName().equals("result")) {

						xpp.next();

						String result = xpp.getText();

						Message msg = Message.obtain();
						msg.obj = result;

						handler.sendMessage(msg);
						Log.i("result : ", result);
						Log.i("regId : ", deviceRegId);
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

			Log.e("errer", ""+e.getMessage());

		}

		Log.e("end", "end tag");

	}


	public void onClick(View v) {

		Intent intent = null;

		switch(v.getId()){
		//占쏙옙占쏙옙占쏙옙트 占쏙옙 占쏙옙튼 占쏙옙占쏙옙占쏙옙 占쏙옙
		case R.id.createProject_btn:
			intent = new Intent(this, CreateProject.class);
			intent.putExtra("id", userId);
			startActivity(intent);
			break;
		case R.id.joinProject_btn:
			intent = new Intent(this, JoinProject.class);
			intent.putExtra("id", userId);
			startActivity(intent);
			break;
		}
	}
	Handler deleteHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent = new Intent(ProjectList.this, ProjectList.class);
			intent.putExtra("id", userId);
			startActivity(intent);
		}
	};

	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			projectList = String.valueOf(msg.obj);
			String[] listString = projectList.split("/");
			listName = new ArrayList<String>();
			listKey = new ArrayList<String>();

			for(int i=0; i<listString.length; i++){
				if((i%2)!=0){
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
				String[] noDataList = {"생성된 프로젝트가 없습니다."};
				setListAdapter(new ArrayAdapter<String>(ProjectList.this, android.R.layout.simple_list_item_1, noDataList));
			}
			 */
			if(items.get(0)!="null"){
				for(int i=0; i<listName.size(); i++){
					m_orders.add(new ProjectListClass(listName.get(i), listKey.get(i)));
					PersonAdapter m_adapter = new PersonAdapter(ProjectList.this, R.layout.row, m_orders); // 占쏙옙占쏙옙拷占�占쏙옙占쌌니댐옙.
					setListAdapter(m_adapter);
				}
			}
		}		
	};


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		if(items.get(0).equals("null")){
			//Toast.makeText(this, "�꾨줈�앺듃瑜��앹꽦�섎젮硫�醫뚯긽�⑥쓽 '�꾨줈�앺듃 �앹꽦' 踰꾪듉���대┃�섏꽭��" , Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(this, "userid="+userId + "/projectName=" + items.get(position) + "/projectKey=" + listKey.get(position) + "/"+position , Toast.LENGTH_LONG).show();

			Intent intent = new Intent(this, Project.class);
			intent.putExtra("id", userId);
			intent.putExtra("projectName", listName.get(position));
			intent.putExtra("projectKey", listKey.get(position));
			startActivity(intent);
		}


	}


	public void clickImage(final View v){

		finish();

		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("projectKey", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(v.getTag()), "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/deleteProject.jsp");
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


	class ProjectListClass{
		String ProjectName = "";
		String ProjectKey = "";
		int trashImage = R.id.bottomImage;

		public ProjectListClass(String ProjectName, String ProjectKey){
			this.ProjectName = ProjectName;
			this.ProjectKey = ProjectKey;
		}

		public String getProjectName(){
			return ProjectName;
		}
		public String getProjectKey(){
			return ProjectKey;
		}
		public int getTrashImage(){
			return trashImage;
		}
	}
	private class PersonAdapter extends ArrayAdapter<ProjectListClass> {

		private ArrayList<ProjectListClass> items;

		public PersonAdapter(Context context, int textViewResourceId, ArrayList<ProjectListClass> items) {
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
			p = items.get(position);
			Log.i("position", String.valueOf(position));

			if (p != null) {
				tt = (TextView) v.findViewById(R.id.toptext);
				ImageView iv = (ImageView) v.findViewById(R.id.bottomImage);
				iv.setFocusable(false);
				if (tt != null){
					tt.setText(p.getProjectName());                            

				}if(iv != null){
					iv.setTag(p.getProjectKey());
					iv.setImageResource(p.getTrashImage());
				}
			}
			return v;
		}
	}

	public void getRegId(){

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, "34225333662");
			Log.i("regId", regId);
		} else {
			Log.v("received", "Already registered");
			Log.i("AregId", regId);
		}

		deviceRegId = regId;
	}

	public void registRegId(){
		new Thread(){
			public void run(){
				String data;
				try {
					data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
					data += "&" + URLEncoder.encode("regId", "UTF-8") + "=" + URLEncoder.encode(deviceRegId, "UTF-8");

					// Send data					
					URL url = new URL("http://iamapark.cafe24.com/registRegId.jsp");
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
			}
		}.start();
	}
}

