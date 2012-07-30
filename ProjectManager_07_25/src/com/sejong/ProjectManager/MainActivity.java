package com.sejong.ProjectManager;

import com.sejong.ProjectManager.DB.LoginDBHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	Button login, register, about;
	LoginDBHandler dbhandler;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		login = (Button) this.findViewById(R.id.login_btn);
		register = (Button) this.findViewById(R.id.register_btn);
		about = (Button) this.findViewById(R.id.about_btn);

		//Ŭ�� ������ ���
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		about.setOnClickListener(this);
		

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = getIntent();
		switch(v.getId()){
		case R.id.login_btn:
			Intent intent = new Intent(this, Login.class);
			intent.putExtra("flag", "1");
			startActivity(intent);
			break;		
		case R.id.register_btn:
			startActivity(new Intent(this, Register.class));
			break;
		case R.id.about_btn:
			startActivity(new Intent(this, About.class));
			break;			
		}
	}
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		/**Toast.makeText(this, "onStop called!!", Toast.LENGTH_SHORT).show();
		
		//SQLite�� �÷� ��ü�� �����Ѵ�.
		dbhandler = LoginDBHandler.open(this);
		
		new Thread(){
			public void run(){
				dbhandler.delete("delete");
			}
		}.start();*/
	}
}