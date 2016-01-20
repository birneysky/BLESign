package com.github.blesign.utils;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;

public class AlarmUtil {

	private Uri uri;
	private Context context;
	private MediaPlayer mp;
	
	public AlarmUtil(Context context){
		mp = new MediaPlayer(); 
		this.context = context;
		mp.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				LogUtil.i("AlarmUtil", "MediaPlayer　onError!");
				mp.reset();
				return false;
			}
		});
	}

	public void setAlarm(String u) {
		uri = Uri.parse(u);
		if(uri == null)	return;
		mp.reset(); 
		mp.setLooping(true);
	    try {
			mp.setDataSource(context, uri);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		mp.start();
	}

	public void cancel(){
		mp.stop();
	}
	
	public void release(){
		mp.release();
	}
	
}
