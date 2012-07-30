package com.sejong.ProjectManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialogActivity extends Activity implements OnClickListener {

 private String notiMessage;
 private Button cancleButton, OkButton;
 
 String ideaNum = null;
 String ideaName = null;

 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  requestWindowFeature(Window.FEATURE_NO_TITLE);
  Bundle bun = getIntent().getExtras();
  notiMessage = bun.getString("notiMessage");
  ideaNum = bun.getString("ideaNum");
  ideaName = bun.getString("ideaName");
  
  setContentView(R.layout.alertdialog);
 
  TextView adMessage = (TextView)findViewById(R.id.message);
  
  cancleButton = (Button)findViewById(R.id.openIdeaNotOk);
  OkButton = (Button)findViewById(R.id.openIdeaOk);
  
  cancleButton.setOnClickListener(this);
  OkButton.setOnClickListener(this);
  

 }

	public void onClick(View v) {
		if(v.getId()==R.id.openIdeaOk){
			Intent intent = new Intent(this, Login.class);
			intent.putExtra("flag", "2");
			intent.putExtra("ideaNum", ideaNum);
			intent.putExtra("ideaName", ideaName);
			startActivity(intent);
			finish();
		}else{
			finish();
		}
	}

}