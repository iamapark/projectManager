package com.sejong.ProjectManager.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LoginDBHandler {
	private LoginDBHelper helper;
    private SQLiteDatabase db;
    
    private LoginDBHandler(Context ctx) {
        this.helper = new LoginDBHelper(ctx);
        this.db = helper.getWritableDatabase();
    }
	
    public static LoginDBHandler open(Context ctx) throws SQLException {
    	LoginDBHandler handler = new LoginDBHandler(ctx);        

        return handler;    
    }
    
    public void close() {
        helper.close();
    }
    
    public long insert(String id){
    	ContentValues values = new ContentValues();
        values.put("memberid", id);        

        return db.insert("LoginTest", null, values);
    }
    
    public Cursor select(int _id){
    	Cursor cursor = db.query(true, "LoginTest", 
                new String[] {"_id", "memberid"},
                "_id" + "=" + _id, 
                null, null, null, null, null);        
    	if (cursor != null) { cursor.moveToFirst(); }        

    	return cursor;
    }
    
    public void delete(String car_name){
    	
    	int cnt = db.delete("LoginTest", "_id='1'", null);
    	
    	Log.i("drop table", String.valueOf(cnt));
   }
}
