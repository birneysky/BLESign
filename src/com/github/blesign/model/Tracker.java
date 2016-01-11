package com.github.blesign.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Tracker implements Serializable{
	private int id;
	private String name;//名字
	private String device_addr;//蓝牙地址
	private String trackerIconPath;//图片
	private int distance;//距离1-10米
	private String uuid;//服务特性
//	private String major;
//	private String minor;
	private int state;
	private String device_mode;
	private int enabled;//开启，关闭
	
	public String getRingName() {
		return ringName;
	}

	public void setRingName(String ringName) {
		this.ringName = ringName;
	}

	private String ringName;

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

	/*public String getMajor() {
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
	}*/
	
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
				", state = " + state + ", trackerIconPath = " + trackerIconPath + ", distance = " + distance + ", ringName = " +ringName;
	}
}
