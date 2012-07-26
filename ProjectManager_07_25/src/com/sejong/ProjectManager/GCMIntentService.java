package com.sejong.ProjectManager;

import java.util.Iterator;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;


public class GCMIntentService extends GCMBaseIntentService {
    private static final String tag = "GCMIntentService";
    private static final String PROJECT_ID = "34225333662";
  //구글 api 페이지 주소 [https://code.google.com/apis/console/#project:긴 번호]
    //#project: 이후의 숫자가 위의 PROJECT_ID 값에 해당한다
    
    String who = null;
    String message = null;
    String ideaName = null;
    String ideaNum = null;
    
    
   
    //public 기본 생성자를 무조건 만들어야 한다.
    public GCMIntentService(){ this(PROJECT_ID); }
   
    public GCMIntentService(String project_id) { super(project_id); }
 
    /** 푸시로 받은 메시지 */
    protected void onMessage(Context context, Intent intent) {
        Bundle b = intent.getExtras();

        Iterator<String> iterator = b.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            String value = b.get(key).toString();
            Log.d(tag, "onMessage. "+key+" : "+value);
        }
        
        who = intent.getExtras().getString("who");
        message = intent.getExtras().getString("Idea Content");
        ideaName = intent.getExtras().getString("ideaName");
        ideaNum = intent.getExtras().getString("ideaNum");
        
        GET_GCM();
        
       // Toast.makeText(getBaseContext(), "메시지가 도착했습니다.", Toast.LENGTH_LONG).show();
    }
    
    public void GET_GCM(){
    	new Thread(){
    		public void run(){
    			handler.sendEmptyMessage(0);
    		}
    	}.start();
    }
    
    private Handler handler = new Handler(){
    	public void handleMessage(Message msg){
    		Context context = getApplicationContext();
    		int duration = Toast.LENGTH_LONG;
    		Toast toast = Toast.makeText(context, who + "가 아이디어를 등록했습니다.", duration);
    		toast.show();
    		
    		
    		Bundle bun = new Bundle();
    		bun.putString("notiMessage", "text");
    		bun.putString("ideaNum", ideaNum);
    		bun.putString("ideaName", ideaName);
    		
    		Intent popupIntent = new Intent(getApplicationContext(), AlertDialogActivity.class);

    		popupIntent.putExtras(bun);
    		PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
    		try {
    			pie.send();
    		} catch (CanceledException e) {
    			e.printStackTrace();
    		}
    	}
    };

    /**에러 발생시*/
    protected void onError(Context context, String errorId) {
        Log.d(tag, "onError. errorId : "+errorId);
    }
 
    /**단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다*/
    protected void onRegistered(Context context, String regId) {
        Log.d(tag, "onRegistered. regId : "+regId);
    }

    /**단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다*/
    protected void onUnregistered(Context context, String regId) {
        Log.d(tag, "onUnregistered. regId : "+regId);
    }
}