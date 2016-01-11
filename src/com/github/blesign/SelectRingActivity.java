package com.github.blesign;

import com.github.blesign.adapter.SelectRingAdapter;
import com.github.blesign.adapter.SelectRingAdapter.ViewHolder;
import com.github.blesign.utils.Consts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectRingActivity extends Activity {
	
	private SelectRingAdapter ringAdapter;
	ListView listView;
	Button sureBtn;
	SharedPreferences sp;
	SharedPreferences.Editor spe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_ring);
		/*初始化SharedPreferences*/
		sp = getSharedPreferences("ring", 1);
		spe = sp.edit();
		
		/*初始化listView*/
		listView = (ListView) findViewById(R.id.ring_lv);
		ringAdapter = new SelectRingAdapter(this, sp.getInt("ring", 0));
		listView.setAdapter(ringAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(mOnItemClickListener);

		/*初始化返回按钮和保存按钮*/
		Button backBtn = (Button) findViewById(R.id.back_btn);
		sureBtn = (Button) findViewById(R.id.sure_btn);
		backBtn.setOnClickListener(mOnClickListener);
		sureBtn.setOnClickListener(mOnClickListener);
	}

	/*listView的按钮点击事件*/
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			ViewHolder mHolder = new ViewHolder(parent);
			/*设置Imageview不可被点击*/
			mHolder.iv.setClickable(false);
			/*清空map*/
			ringAdapter.map.clear();
			// mAdapter.map.put(position, 1);
			/*将所点击的位置记录在map中*/
			ringAdapter.map.put(position, true);
			/*刷新数据*/
			ringAdapter.notifyDataSetChanged();
			/*判断位置不为0则播放的条目为position-1*/
			if (position != 0) {
				try {
					RingtoneManager rm = new RingtoneManager(SelectRingActivity.this);
					rm.setType(RingtoneManager.TYPE_NOTIFICATION);
					rm.getCursor();
					rm.getRingtone(position - 1).play();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*position为0是跟随系统，先得到系统所使用的铃声，然后播放*/
			if (position == 0) {
				Uri uri = RingtoneManager.getActualDefaultRingtoneUri(
						SelectRingActivity.this, RingtoneManager.TYPE_NOTIFICATION);
				RingtoneManager.getRingtone(SelectRingActivity.this, uri).play();
			}

		}

	};
	protected String ringName;

	/*按钮点击事件*/
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/*返回按钮时退出demo*/
			case R.id.back_btn:
				finish();
				break;
			/*保存按钮则保存SharedPreferences中的数据*/
			case R.id.sure_btn:
				spe.putInt("ring", listView.getCheckedItemPosition()).commit();
				Toast.makeText(SelectRingActivity.this, "提示音保存成功", Toast.LENGTH_SHORT).show();
				ringName = ringAdapter.getItem(listView.getCheckedItemPosition())+"";
				setResult(RESULT_OK, new Intent().putExtra(Consts.EXTRA_RING_NAME, ringName));
				finish();
				break;
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_ring, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
