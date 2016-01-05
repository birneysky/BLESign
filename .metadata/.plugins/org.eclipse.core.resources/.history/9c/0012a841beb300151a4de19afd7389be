package com.example.slidingmenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;

public class WelcomeActivity extends Activity {
	String package_name = "";
	boolean firstTime;
	
	private static final String FILE_NAME = "/app_icon_b.png";
	public static String TEST_IMAGE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		IbeaconApplication.getInstance().addActivity(this);
		SharedPreferences sp = getSharedPreferences("firstTemp", Context.MODE_PRIVATE);
		firstTime = sp.getBoolean("firstTime", false);
		initImagePath();
    	if(firstTime){
    		Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
    		startActivity(intent);
    		this.finish();
    	}else{
    		Editor editor = sp.edit();
    		editor.putBoolean("firstTime", true);
    		editor.commit();
    		Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
    		startActivity(intent);
    		this.finish();
    	}
		
	}

	private void initImagePath() {
		FileOutputStream fos = null;
		try {
//			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//					&& Environment.getExternalStorageDirectory().exists()) {
//				
//				TEST_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_NAME;
//			} else {
				TEST_IMAGE = getApplication().getFilesDir().getAbsolutePath() + FILE_NAME;
//			}
			File file = new File(TEST_IMAGE);
			// 创建图片文件夹
			if (!file.exists()) {
				if(file.createNewFile()){
					Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_b);
					fos = new FileOutputStream(file);
					pic.compress(CompressFormat.JPEG, 100, fos);
					fos.flush();
					fos.close();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		} finally {
			try {
				if(fos != null){
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
