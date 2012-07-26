package com.sejong.ProjectManager.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LoginDBHelper extends SQLiteOpenHelper {

	public LoginDBHelper(Context context) {
        super(context, "ba.db", null, 1);
    }

	@Override
    public void onCreate(SQLiteDatabase db) { 
        String table = 
            "CREATE TABLE LoginTest (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
            "memberid TEXT NOT NULL);";
        db.execSQL(table);
        
    }
	
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {        
        db.execSQL("DROP TABLE IF EXISTS LoginTest");
        onCreate(db);
    }
}
