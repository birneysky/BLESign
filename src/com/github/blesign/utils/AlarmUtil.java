package com.github.blesign.utils;

import android.content.Context;
import android.net.Uri;

public class AlarmUtil {

	private static Uri uri;

	public static void setAlarm(String u, Context context) {
		// TODO Auto-generated method stub
		uri = Uri.parse(u);
		
	}

	public static void cancel(){
		
	}
	
}
