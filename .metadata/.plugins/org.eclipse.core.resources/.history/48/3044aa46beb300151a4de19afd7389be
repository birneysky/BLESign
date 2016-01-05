package com.example.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.view.guide.OnViewChangeListener;
import com.view.guide.ScrollLayout;

public class GuideActivity extends Activity implements OnViewChangeListener{
	private static final String TAG = GuideActivity.class.getSimpleName();
	private ScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private Button startBtn, toMain1, toMain2, toMain3;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		IbeaconApplication.getInstance().addActivity(this);
		init();
	}

	/**
	 * 初始化
	 */
	private void init()	{
		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		startBtn = (Button) findViewById(R.id.btn_star);
		toMain1 = (Button)findViewById(R.id.btn_guide_goto_main_01);
		toMain2 = (Button)findViewById(R.id.btn_guide_goto_main_02);
		toMain3 = (Button)findViewById(R.id.btn_guide_goto_main_03);
		startBtn.setOnClickListener(onClick);
		toMain1.setOnClickListener(onClick);
		toMain2.setOnClickListener(onClick);
		toMain3.setOnClickListener(onClick);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for (int i = 0; i < count; i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mScrollLayout.setVisibility(View.GONE);
			pointLLayout.setVisibility(View.GONE);
			Intent intent = new Intent(GuideActivity.this, RegistActivity.class);
			GuideActivity.this.startActivity(intent);
			GuideActivity.this.finish();
		}
	};

	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > count - 1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
		if(position == count -1){
			pointLLayout.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	// 在欢迎界面屏蔽back键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			GuideActivity.this.finish();
		}
		return false;
	}

}
