package com.github.blesign.dao;

import java.util.ArrayList;

import com.github.blesign.dao.impl.BeaconDAOImpl;
import com.github.blesign.model.Tracker;
import com.zijin.ibeacon.db.SQLiteHelper;

import android.content.Context;

public class BeaconDAO {
	
	private BeaconDAOImpl beaconDAOImpl;
	
	
	public BeaconDAO(Context context){
		beaconDAOImpl = new BeaconDAOImpl(context);
	}
	
	public Tracker addBeacon(Tracker tracker){
		long result = beaconDAOImpl.addBeacon(tracker);
		if(result == -1){
			return null;
		}
		Tracker beacon = findByBeaconMac(tracker.getDevice_addr());
		return beacon;
	}
	
	public Tracker findByBeaconMac(String mac){
		return beaconDAOImpl.findByBeaconMac(mac);
	}
	
	/**
	 * 获取所有设备
	 * @return
	 */
	public ArrayList<Tracker> getAllBeacons() {
		return beaconDAOImpl.getAllBeacons();
	}
	
	public int updateRing(String mac, String ringName, String uri){
		return beaconDAOImpl.updateRing(mac, ringName, uri);
	}

	public int updateDistanceByMac(String device_addr, int progress) {
		// TODO Auto-generated method stub
		return beaconDAOImpl.updateDistanceByMac(device_addr, progress);
	}
	
	public int deleteBeacon(int id) {
		return beaconDAOImpl.deleteBeacon(id);
	}
}
