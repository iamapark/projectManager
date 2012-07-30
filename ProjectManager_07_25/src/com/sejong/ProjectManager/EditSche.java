package com.sejong.ProjectManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class EditSche extends Activity {
	/** Called when the activity is first created. */

	ToggleButton tb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_sche);

		tb = (ToggleButton) this.findViewById(R.id.toggle);

		tb.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tb.isChecked())
					tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));					
				else
					tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));				
			}
		});

	}
}