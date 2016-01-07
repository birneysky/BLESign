package com.github.blesign.model;

public class IbeaconLocation {

	private String findId;//ibeacon mac
	private String locationTime;
	private double locationLat;
	private double locationLot;
	private String locationStatus;
	private long locationDate;//System.currentTimeMillis();

	public String getId() {
		return findId;
	}
	public void setId(String id) {
		this.findId = id;
	}
	public String getLocationTime() {
		return locationTime;
	}
	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}
	public double getLocationLat() {
		return locationLat;
	}
	public void setLocationLat(double locationLat) {
		this.locationLat = locationLat;
	}
	public double getLocationLot() {
		return locationLot;
	}
	public void setLocationLot(double locationLot) {
		this.locationLot = locationLot;
	}
	public String getLocationStatus() {
		return locationStatus;
	}
	public void setLocationStatus(String locationStatus) {
		this.locationStatus = locationStatus;
	}
	public long getLocationDate() {
		return locationDate;
	}
	public void setLocationDate(long locationDate) {
		this.locationDate = locationDate;
	}
}
