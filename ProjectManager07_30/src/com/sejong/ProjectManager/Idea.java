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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
	String replyMessage;
	String replyMessageUserId;
	String replyNum;
	ArrayList<String> arrayIdeaReply = new ArrayList<String>(); //리플 내용이 담겨져 있는 배열
	ArrayList<String> arrayIdeaReplyUserId = new ArrayList<String>(); //리플 등록한 아이디가 담겨져 있는 배열
	ArrayList<String> arrayIdeaReplyNum = new ArrayList<String>(); //리플 고유번호가 담긴 배열

	ArrayList<ReplyListClass> m_orders = new ArrayList<ReplyListClass>();
	ReplyListClass r;

	ListAdapter replyAdapter;

	InputStream is;

	// 댓글
	TextView replyId;			
	TextView replyCon;

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
						
					}else if(xpp.getName().equals("replyNum")){
						xpp.next();

						replyNum = xpp.getText();
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
						arrayIdeaReplyNum.add(replyNum);
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

	class ReplyListClass{
		String reply_Id = "";
		String reply_Con = "";
		String IdeaKey = "";
		int trashImage = R.id.bottomImage;

		public ReplyListClass(String reply_Id, String reply_Con, String IdeaKey){
			this.reply_Id = reply_Id;
			this.reply_Con = reply_Con;
			this.IdeaKey = IdeaKey;			
		}

		public String getReplyId(){
			return reply_Id;
		}
		public String getReplyContents(){
			return reply_Con;
		}
		public String getIdeaKey(){
			return IdeaKey;
		}
		public int getTrashImage(){
			return trashImage;
		}
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
				Log.i("댓글", "댓글 달기 성공");
				getIdeaReply();
				replyContent.setText("");
			}else{
				Toast.makeText(Idea.this, "댓글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	Handler getReplyHandler = new Handler(){
		public void handleMessage(Message msg){
			//Log.i("ListView", "start");
			for(int i=0; i<arrayIdeaReply.size(); i++){
				Log.i("arrayIdeaReply Contents : " + i, arrayIdeaReply.get(i));
				m_orders.add(new ReplyListClass(arrayIdeaReplyUserId.get(i), arrayIdeaReply.get(i), arrayIdeaReplyNum.get(i)));			
				ReplyAdapter m_adapter = new ReplyAdapter(Idea.this, R.layout.row2, m_orders);	
				replyList.setAdapter(m_adapter);
				Log.i("리스트뷰 등록", "끝");
			}
			sendLinearLayout = new LinearLayout(Idea.this);
		}
	};

	private class ReplyAdapter extends ArrayAdapter<ReplyListClass>{
		private ArrayList<ReplyListClass> items;

		public ReplyAdapter(Context context, int textViewResourceId, ArrayList<ReplyListClass> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row2, null);
			}

			r= items.get(position);
			Log.i("position", String.valueOf(position));

			if (r != null) {
				replyId = (TextView) v.findViewById(R.id.idtext);
				replyCon = (TextView) v.findViewById(R.id.toptext);
				ImageView iv = (ImageView) v.findViewById(R.id.bottomImage);
				iv.setFocusable(false);
				if (replyId != null){
					replyId.setText(r.getReplyId());
				}
				if(replyCon != null){
					replyCon.setText(r.getReplyContents()); 
				}
				if(iv != null){
				}
				iv.setTag(R.string.ideaKey, r.getIdeaKey());
				iv.setTag(R.string.ideaUserId, r.getReplyId());
				iv.setImageResource(r.getTrashImage());
			}
			return v;
		}
	}

	public void clickImage(final View v){

		if(userId.equals(String.valueOf(v.getTag(R.string.ideaUserId)))){
			finish();
			
			new Thread(){
				public void run(){
					String data;
					try {
						data = URLEncoder.encode("ideaReplyNum", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(v.getTag(R.string.ideaKey)), "UTF-8");

						// Send data					
						URL url = new URL("http://iamapark.cafe24.com/deleteIdeaReply.jsp");
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
		}else{
			Toast.makeText(Idea.this, "다른 사람이 등록한 리플은 지울 수 없습니다.", Toast.LENGTH_LONG).show();
		}

	}
	
	Handler deleteHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent intent = new Intent(Idea.this, Idea.class);
			intent.putExtra("userId", userId);
			intent.putExtra("ideaKey", ideaNum);
			intent.putExtra("ideaName", ideaName);
			startActivity(intent);
		}
	};
	
}