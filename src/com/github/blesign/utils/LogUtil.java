package com.github.blesign.utils;


import android.util.Log;

public class LogUtil {
	
	//工具类，通用类：在企业中有高手来写。
	//日志处理：是用一个框架，记录每个模块的访问次数，每个方法执行时间
	//log4j
	// baidu log4j android
	//true 发布版本。
	static boolean isRelease=false;
	
	public static void i(String tag,String msg) {
		if (isRelease){
			return;
		}else{
			Log.i(tag, msg);
		}
	}
	
	public static void v(String tag, String msg){
		if(isRelease){
			return;
		}else{
			Log.v(tag, msg);
		}
	}
	
	public static void w(String tag, String msg){
		if(isRelease){
			return;
		}else{
			Log.w(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if(isRelease){
			return;
		}else{
			Log.d(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if(isRelease){
			return;
		}else{
			Log.e(tag, msg);
		}
	}

}
