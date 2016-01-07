package com.github.blesign;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

public class IbeaconApplication extends Application {

	private List<Activity> activityList=new LinkedList<Activity>();
	private static IbeaconApplication instance;
	public boolean isSupport=true;
	private Handler handler = null;

	@Override
	public void onCreate() {
		super.onCreate();
		checkBLe();
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public Handler getHandler(){
		return this.handler;
	}
	
	/**
	 * 判断设备是否支持BLE
	 */
	 private void checkBLe() {
		 if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			 Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			 isSupport=false;
//	         IbeaconApplication.this.exit();
		 }
	 }
	
	 /**
      * 单例模式中获取唯一的ExitApplication 实例
      * @return
      */
     public static IbeaconApplication getInstance(){
	     if(null == instance){
	    	 instance = new IbeaconApplication();
	     }
	     return instance;
     }
     /**
      * 添加Activity 到容器中
      * @param activity
      */
     public void addActivity(Activity activity){
    	 if(!activityList.contains(activity)){
    		 activityList.add(activity);
    	 }
     }
     /**
      *  移除某一个Activity
      * @param activity
      */
     public void removeAcitvity(Activity activity){
    	 if(activityList.contains(activity)){
    		 activityList.remove(activityList.indexOf(activity));
    	 }
     }
     /**
      * 退出应用程序：遍历所有Activity 并finish
      */
     public void exit(){
	     for(Activity activity:activityList){
	    	 activity.finish();
	     }
	     System.exit(0);
     }
}