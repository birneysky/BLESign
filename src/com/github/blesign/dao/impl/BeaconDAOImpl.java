package com.github.blesign.dao.impl;

import java.util.ArrayList;

import com.github.blesign.dao.BeaconColumns;
import com.github.blesign.dao.DBHelper;
import com.github.blesign.model.Tracker;
import com.github.blesign.utils.Consts;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BeaconDAOImpl {
	
	private DBHelper helper;
	private SQLiteDatabase db;
	private String[] mSingleArg = new String[1];
	private static final String MAC_SELECTION = BeaconColumns.MAC + "=?";
	
	public BeaconDAOImpl() {
		// TODO Auto-generated constructor stub
	}

	public BeaconDAOImpl(Context context) {
		// TODO Auto-generated constructor stub
		helper = new DBHelper(context, Consts.DB_NAME, null, 1);
		db = helper.getWritableDatabase();
	}
	
	@SuppressWarnings("static-access")
	public long addBeacon(Tracker beacon){
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.NAME, beacon.getName());
		values.put(BeaconColumns.UUID, beacon.getUuid());
		values.put(BeaconColumns.MAC, beacon.getDevice_addr());
		values.put(BeaconColumns.IMGURL, beacon.getTrackerIconPath());
		values.put(BeaconColumns.ENABLED,beacon.getEnabled());//default ennable
		values.put(BeaconColumns.STATE, beacon.getState());
		values.put(BeaconColumns.DISTANCE, beacon.getDistance());
		long result = db.insert(helper.BEACONS, null, values);
		return result;
	}

	@SuppressWarnings("static-access")
	public Tracker findByBeaconMac(String mac) {
		Tracker tracker=null;
		mSingleArg[0] = mac;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.MAC, mac);
		Cursor cursor = db.query(helper.BEACONS, BeaconColumns.BEACON_PROJECTION, MAC_SELECTION, mSingleArg, null, null, null);
		 if(cursor !=null){
			 tracker = new Tracker();
			if(cursor.moveToNext()){ 
				tracker.setId(cursor.getInt(cursor.getColumnIndex(BeaconColumns.ID)));
				tracker.setName(cursor.getString(cursor.getColumnIndex(BeaconColumns.NAME)));
				tracker.setUuid(cursor.getString(cursor.getColumnIndex(BeaconColumns.UUID)));
				tracker.setDevice_addr(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAC)));
				tracker.setTrackerIconPath(cursor.getString(cursor.getColumnIndex(BeaconColumns.IMGURL)));
				tracker.setState(cursor.getInt(cursor.getColumnIndex(BeaconColumns.STATE)));
				tracker.setDistance(cursor.getInt(cursor.getColumnIndex(BeaconColumns.DISTANCE)));
			}
			cursor.close();
		 }
		 return tracker;
	}
	
	/**
	 * 获取所有设备
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ArrayList<Tracker> getAllBeacons() {
		ArrayList<Tracker> list = null;// = new ArrayList<Tracker>();
		Cursor cursor =  db.query(helper.BEACONS, BeaconColumns.BEACON_PROJECTION, null, null, null, null, null);
		if(cursor !=null){
			list= new ArrayList<Tracker>();
			while(cursor.moveToNext()){
				Tracker tracker = new Tracker();
				tracker.setId(cursor.getInt(cursor.getColumnIndex(BeaconColumns.ID)));
				tracker.setName(cursor.getString(cursor.getColumnIndex(BeaconColumns.NAME)));
				tracker.setUuid(cursor.getString(cursor.getColumnIndex(BeaconColumns.UUID)));
				tracker.setDevice_addr(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAC)));
				tracker.setTrackerIconPath(cursor.getString(cursor.getColumnIndex(BeaconColumns.IMGURL)));
				tracker.setState(cursor.getInt(cursor.getColumnIndex(BeaconColumns.STATE)));
				tracker.setDistance(cursor.getInt(cursor.getColumnIndex(BeaconColumns.DISTANCE)));
				list.add(tracker);
			}
			cursor.close();
		}
		return list;
	}

}
