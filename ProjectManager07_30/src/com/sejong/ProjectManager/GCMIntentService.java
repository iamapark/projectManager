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
  //援ш� api �섏씠吏�二쇱냼 [https://code.google.com/apis/console/#project:湲�踰덊샇]
    //#project: �댄썑���レ옄媛��꾩쓽 PROJECT_ID 媛믪뿉 �대떦�쒕떎
    
    String who = null;
    String message = null;
    String ideaName = null;
    String ideaNum = null;
    
    
   
    //public 湲곕낯 �앹꽦�먮� 臾댁“嫄�留뚮뱾�댁빞 �쒕떎.
    public GCMIntentService(){ this(PROJECT_ID); }
   
    public GCMIntentService(String project_id) { super(project_id); }
 
    /** �몄떆濡�諛쏆� 硫붿떆吏�*/
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
        
       // Toast.makeText(getBaseContext(), "硫붿떆吏�� �꾩갑�덉뒿�덈떎.", Toast.LENGTH_LONG).show();
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
    		//Toast toast = Toast.makeText(context, who + "媛��꾩씠�붿뼱瑜��깅줉�덉뒿�덈떎.", duration);
    		//toast.show();
    		
    		
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


    protected void onError(Context context, String errorId) {
        Log.d(tag, "onError. errorId : "+errorId);
    }
 

    protected void onRegistered(Context context, String regId) {
        Log.d(tag, "onRegistered. regId : "+regId);
    }


    protected void onUnregistered(Context context, String regId) {
        Log.d(tag, "onUnregistered. regId : "+regId);
    }
}