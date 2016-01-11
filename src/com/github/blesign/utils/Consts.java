package com.github.blesign.utils;

import java.io.File;

import android.os.Environment;

public class Consts {
	
	//DB
	public static final String DB_NAME = "ibeacon.db";
	
	//Camera
	public static final int CAMERA_REQUEST_CODE = 5011;
	//select ring
	public static final int REQUEST_SELECT_RING = 5021;
	public static final String 	EXTRA_RING_NAME = "ringName";
	
	public static final int TRACKER_STATE_UNSELECTED = 0; // 未连接
	public static final int TRACKER_STATE_LOST =1;		// 已丢失
	public static final int TRACKER_STATE_TRACKING = 2;	// 正在防丢
	public static final int TRACKER_STATE_SEARCHING = 3; // 已经丢失，正在众寻
	public static final int TRACKER_STATE_ICON_CHANGE_RED = 7;//更改指示图片为红色
	public static final int TRACKER_STATE_ICON_CHANGE_BACK = 8;//更改指示颜色回为蓝色
	
	public static final String TRACKER_STATE_UNSELECTED_TEXT = "未追踪";
	public static final String TRACKER_STATE_LOST_TEXT = "追忆中";
	public static final String TRACKER_STATE_TRACKING_TEXT = "追踪中";
	
	public static final int MESSAGE_ALARM_ALERT = 1207;
	public static final int MESSAGE_CHECE_SENCE_ON_NET = 1217;//向后台发送Mac地址，判断是否是场景
	public static final int MESSAGE_SHOW_ALERT_DIALOG = 173;
	public static final String CONNECTION_STATE = "connectionState";
	/**
	 * 扫描成功，传递iBeacon的mac地址
	 */
	public static final String DEVICE_MAC = "device_mac";
	public static final String IBEACON_ID = "iBeaconID";
	
	public static final int REQUEST_ADD_BEACON_CONNECTION_STATE = 201;
	public static final int EXTRA_ADD_BEACON_OK = 201;
	public static final int EXTRA_ADD_BEACON_FAILED = 202;
	
	public static final int REQUEST_CONNECTION_STATE_ADD_TRACKER = 101;
	public static final int RESULT_CONNECTION_STATE_ADD_TRACKER_OK = 101;
	public static final int RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN = 102;
	
	public static final int REQUEST_SELECT_COUNTRY = 301;
	public static final int RESULT_SELECT_COUNTRY = 301;
	
	public static final int REQUEST_CHANGE_SLEEP_MODE = 401;
	public static final int RESULT_SLEEP_MODE_CHANGED = 401;
	
	public static final int REQUEST_CHANGE_REPEAT_MODE = 501;
	public static final int RESULT_REPEAT_MODE_CHANGED = 501;
	
	public static final int REQUEST_LOCATION_NOTE = 601;
	
	public static final int REQUEST_CHANGE_SAFITY_AREA = 701;
	public static final int RESULT_SAFITY_AREA_CHANGED = 701;
	
	public static final int REQUEST_ADD_SAFITY_AREA = 801;
	public static final int RESULT_SAFITY_AREA_ADDED = 801;
	
	public static final int REQUEST_PUBLISHED_SEARCH = 811;
	public static final int RESULT_PUBLISHED_SEARCH = 811;
	public static final int REQUEST_PUBLISHED_SEARCH_ADD_PHOTO = 812; //添加照片
	public static final int REQUEST_PUBLISHED_SEARCH_VIEW_PHOTO = 813; //浏览添加的照片

	public static final String ZIP_IMG_TEMP = Environment.getExternalStorageDirectory()+File.separator+"upFiles.zip";
	/**
	 * //服务器获取图片存放路径
	 */
	public static final String IMG_PATH=Environment.getExternalStorageDirectory()+File.separator+"ARTICLE_IMG/";
	public static final int IMAGE_COUNT = 4; // 最多4张图片
	
	
}
