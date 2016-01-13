package com.github.blesign.dao;


import com.github.blesign.dao.BeaconColumns;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String BEACONS = "beacons";
	
	private static final String CREATE_BEACONS = "CREATE TABLE " + BEACONS + "(" 
			+ BeaconColumns.ID + " integer primary key autoincrement, " 	
			+ BeaconColumns.NAME+ " TEXT, " 
			+ BeaconColumns.UUID + " TEXT , " 
			+ BeaconColumns.MAC + " TEXT NOT NULL, " 
			+ BeaconColumns.IMGURL + " TEXT, " 
			+ BeaconColumns.DISTANCE + " INTEGER DEFAULT(1), " 
			+ BeaconColumns.ENABLED + " INTEGER NOT NULL DEFAULT(0)," 
			+ BeaconColumns.STATE+ " INTEGER NOT NULL DEFAULT(1),"
			+ BeaconColumns.RING_NAME + " text,"
			+ BeaconColumns.RING_URI + " text"
			+");";
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL(CREATE_BEACONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
