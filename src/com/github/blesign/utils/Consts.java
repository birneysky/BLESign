package com.github.blesign.utils;

import java.io.File;

import android.os.Environment;

public class Consts {
	/**
	 * 设备uuid,过滤
	 */
	public static final String UUID = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0";
	public static final String IBEACON_ID = "iBeaconID";
	public static final int TRACKER_STATE_UNSELECTED = 0; // 未连接
	public static final int TRACKER_STATE_LOST =1;		// 已丢失
	public static final int TRACKER_STATE_TRACKING = 2;	// 正在防丢
	public static final int TRACKER_STATE_SEARCHING = 3; // 已经丢失，正在众寻
	public static final int TRACKER_STATE_ICON_CHANGE_RED = 7;//更改指示图片为红色
	public static final int TRACKER_STATE_ICON_CHANGE_BACK = 8;//更改指示颜色回为蓝色
	
	public static final String TRACKER_STATE_UNSELECTED_TEXT = "未追踪";
	public static final String TRACKER_STATE_LOST_TEXT = "追忆中";
	public static final String TRACKER_STATE_TRACKING_TEXT = "追踪中";
	
	public static final int MESSAGE_CHANGE_USER_ICON = 112; // 消息处理，更换用户头像
	public static final int MESSAGE_ALARM_ALERT = 1207;
	public static final int MESSAGE_CHECE_SENCE_ON_NET = 1217;//向后台发送Mac地址，判断是否是场景
	public static final int MESSAGE_SHOW_ALERT_DIALOG = 173;
	public static final int REQUEST_CHANGE_USER_ICON = 111; // 更换用户头像
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String CONNECTION_STATE = "connectionState";
	/**
	 * 扫描成功，传递iBeacon的mac地址
	 */
	public static final String DEVICE_MAC = "device_mac";
	
	public static final int REQUEST_ADD_BEACON_CONNECTION_STATE = 201;
	public static final int EXTRA_ADD_BEACON_OK = 201;
	public static final int EXTRA_ADD_BEACON_FAILED = 202;
	
	public static final int REQUEST_CONNECTION_STATE_ADD_TRACKER = 101;
	public static final int RESULT_CONNECTION_STATE_ADD_TRACKER_OK = 101;
	public static final int RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN = 102;
	
	public static final int REQUEST_SELECT_COUNTRY = 301;
	public static final int RESULT_SELECT_COUNTRY = 301;
	public static final String COUNTRY = "country";
	public static final String NUMBER_CODE = "numberCode";
	
	public static final int REQUEST_CHANGE_SLEEP_MODE = 401;
	public static final int RESULT_SLEEP_MODE_CHANGED = 401;
	public static final String EXTRA_TIME_BEGIN = "beginTime";
	public static final String EXTRA_TIME_END = "endTime";
	
	public static final int REQUEST_CHANGE_REPEAT_MODE = 501;
	public static final int RESULT_REPEAT_MODE_CHANGED = 501;
	public static final String EXTRA_KEY_REPEAT_MODE = "repeatModeKey";
	
	public static final int REQUEST_LOCATION_NOTE = 601;
	
	public static final int REQUEST_CHANGE_SAFITY_AREA = 701;
	public static final int RESULT_SAFITY_AREA_CHANGED = 701;
	public static final String EXTRA_KEY_SAFITY_AREA = "safityArea";
	
	public static final int REQUEST_ADD_SAFITY_AREA = 801;
	public static final int RESULT_SAFITY_AREA_ADDED = 801;
	public static final String EXTRA_KEY_SAFITY_AREA_NEW = "safityAreaNew";
	
	public static final int REQUEST_PUBLISHED_SEARCH = 811;
	public static final int RESULT_PUBLISHED_SEARCH = 811;
	public static final int REQUEST_PUBLISHED_SEARCH_ADD_PHOTO = 812; //添加照片
	public static final int REQUEST_PUBLISHED_SEARCH_VIEW_PHOTO = 813; //浏览添加的照片
	public static final String EXTRA_KEY_PUBLISHED_SEARCH = "publishedSearchExtra";

	public static final String ZIP_IMG_TEMP = Environment.getExternalStorageDirectory()+File.separator+"upFiles.zip";
	/**
	 * //服务器获取图片存放路径
	 */
	public static final String IMG_PATH=Environment.getExternalStorageDirectory()+File.separator+"ARTICLE_IMG/";
	public static final int IMAGE_COUNT = 4; // 最多4张图片
	
	/**
	 * 短信验证第三方key 
	 */
	public static final String SMS_AppId="vq1h53b9tuy09cuynir7hub4w6gy7ioh75c8oov74v19z5to";
	public static final String SMS_AppKey="rk82xuy4hie1tmk1en3b3vcfzb269ae5ahzzsoxyvhr2cll8";
	
	public static final int REQUEST_HELP_SEARCH = 821;
	public static final int RESULT_HELP_SEARCH = 821;
	public static final int REQUEST_HELP_SEARCH_STATE = 822;
	public static final int RESULT_HELP_SEARCH_SATATE = 822;
	
	public static final String EXTRA_SCAN_TYPE_HELP = "scanTypeHelp";
	public static final String EXTRA_SCAN_TYPE_ADD  = "scanTypeAdd";
	public static final String EXTRA_KEY_SCAN_TYPE  = "keyScanType";
	
	// 百度地图Activity：定位or查看位置
	public static final String EXTRA_BAIDUMAP_TYPE_KEY = "type";
	public static final String EXTRA_BAIDUMAP_TYPE_VIEW_POSITION = "viewPosition";
	public static final String EXTRA_BAIDUMAP_TYPE_LOCATION = "newLocation";
	/**
	 * 百度地图查看位置请求key
	 */
	public static final int BAIDU_VIEW_LOCATION = 717;
	
	/**
	 * 默认半径
	 */
	public static final int RADIUS = 500;
	/**
	 * 安全区域传值key
	 */
	public static final String EXTRA_KEY_SOUTH_WEAST = "southWeast";
	public static final String EXTRA_KEY_NORTH_EAST = "northEast";
	
	
	public static final String ACTION_SET_START_ALARM = "com.zijin.alarm.action.START";
	public static final String ACTION_SET_END_ALARM = "com.zijin.alarm.action.END";
//	public static final String ACTION_CANCEL_ALARM = "com.zijin.alarm.action.CANCEL";
}
