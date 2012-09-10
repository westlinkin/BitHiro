package com.westlinkin.bithiro;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBService extends SQLiteOpenHelper{

	private final static String DATA_BASE_NAME = "bithiro";
	public DBService(Context context) {
		super(context, DATA_BASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public Cursor getCurcor(String sql, String[] args){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor curcor = db.rawQuery(sql, args);
		
		return curcor;
	}
}
