package com.github.blesign.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Tracker implements Serializable{
	private int id;
	private String name;
	private String uuid;
//	private String mode;
	private String sleepTimes;
	private String repeatTimes;
	private String major;
	private String minor;
	private int state;
	private String trackerIconPath;
	private String device_addr;
	private String device_mode;
	private String sleepTimesMode;
	private String repeatTimesMode;
	private int distance;
	private int enabled;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getTrackerIconPath() {
		return trackerIconPath;
	}

	public void setTrackerIconPath(String trackerIconPath) {
		this.trackerIconPath = trackerIconPath;
	}
	

	public String getDevice_addr() {
		return device_addr;
	}

	public void setDevice_addr(String device_addr) {
		this.device_addr = device_addr;
	}
	
	

	public String getDevice_mode() {
		return device_mode;
	}

	public void setDevice_mode(String device_mode) {
		this.device_mode = device_mode;
	}
	
	

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
//
//	public String getMode() {
//		return mode;
//	}
//
//	public void setMode(String mode) {
//		this.mode = mode;
//	}

	public String getSleepTimes() {
		return sleepTimes;
	}

	public void setSleepTimes(String sleepTimes) {
		this.sleepTimes = sleepTimes;
	}

	public String getRepeatTimes() {
		return repeatTimes;
	}

	public void setRepeatTimes(String repeatTimes) {
		this.repeatTimes = repeatTimes;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}
	
	public String getSleepTimesMode() {
		return sleepTimesMode;
	}

	public void setSleepTimesMode(String sleepTimesMode) {
		this.sleepTimesMode = sleepTimesMode;
	}

	public String getRepeatTimesMode() {
		return repeatTimesMode;
	}

	public void setRepeatTimesMode(String repeatTimesMode) {
		this.repeatTimesMode = repeatTimesMode;
	}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled=enabled;
	}

	@Override
	public String toString() {
		return "Tracker: id = " + id + ", name = " + name + ", device_addr = "+ device_addr + 
				", state = " + state + ", trackerIconPath = " + trackerIconPath + ", distance = " + distance + 
				", device_mode = " + device_mode;
	}
}
