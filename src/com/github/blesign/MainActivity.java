package com.github.blesign;

import java.util.ArrayList;

import com.github.blesign.adapter.GridViewAdapter;
import com.github.blesign.dao.BeaconDAO;
import com.github.blesign.model.Tracker;
import com.github.blesign.utils.Consts;
import com.github.blesign.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	//UI
	private GridView gridViewTrackers;
	private GridViewAdapter adapter;
	private ArrayList<Tracker> lists;
	
	//Add Beacon
	private BeaconDAO beaconDAO;
	
	//窗口配置
	private View view; // 防丢器属性view
	private LayoutInflater inflater;
	private  int mSlectedItem = -1; // 当前防丢器在adapter中的位置（size-1）
	private Tracker tracker; // 当前防丢器
	private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();
        setupView();
        addListener();
        getData();
    }

	private void getData() {
		// TODO Auto-generated method stub
		
	}

	private void setData() {
		// TODO Auto-generated method stub
		beaconDAO = new BeaconDAO(getApplicationContext());
		inflater = LayoutInflater.from(MainActivity.this);
	}

	private void setupView() {
		// TODO Auto-generated method stub
		gridViewTrackers = (GridView)findViewById(R.id.gridView_trackers);
		lists = new ArrayList<Tracker>();
		
		ArrayList<Tracker> retList = beaconDAO.getAllBeacons();
		if(retList != null && retList.size()>0){
			lists = retList;
			for(Tracker tracker:retList){
				Utils.ibeaconArr.add(tracker.getDevice_addr());
			}
		}
		adapter = new GridViewAdapter(getApplicationContext(), lists);
		gridViewTrackers.setAdapter(adapter);
	}

	private void addListener() {
		// TODO Auto-generated method stub
		gridViewTrackers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
				if(position == (adapter.getCount()-1)){//单击添加按钮
					Intent intent = new Intent(MainActivity.this,AddBeaconActivity.class);
					startActivityForResult(intent,AddBeaconActivity.REQUEST_ADD_IBEACON);
					return;
				}
				//arg1是当前item的view，通过它可以获得该项中的各个组件。 arg2是当前item的ID。这个id根据你在适配器中的写法可以自己定义。arg3是当前的item在listView中的相对位置！
				// 选中标识箭头显示,现在被挤压的看不见了
				final GridViewAdapter.ViewHolder holder = (GridViewAdapter.ViewHolder) arg1.getTag();
				if(holder.ivSelectedBg.getVisibility() == View.GONE){
					holder.ivSelectedBg.setVisibility(View.VISIBLE);
				}
				// 引入窗口配置文件  
				if(view == null){
					view = inflater.inflate(R.layout.tracker_unlinked, null); 
					setupPopView();
					addPopListener();
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == AddBeaconActivity.REQUEST_ADD_IBEACON){
			if(resultCode == RESULT_OK){
				Tracker tracker = new Tracker();
				String device_name = data.getStringExtra("tracker_name");
				String device_mac = data.getStringExtra("tracker_mac");
				String pic_path = data.getStringExtra("picPath");
				tracker.setName(device_name);
				tracker.setDevice_addr(device_mac);
				tracker.setDistance(5);
				tracker.setEnabled(0);
				tracker.setState(Consts.TRACKER_STATE_UNSELECTED);
				if(pic_path !=null){
					tracker.setTrackerIconPath(pic_path);
				}
				//插入数据库：必须保证mac地址不为空，mac地址唯一，成功添加后再加入记录数组
				Tracker dbTracker = beaconDAO.addBeacon(tracker);
				if(dbTracker!=null){
					lists.add(dbTracker);
					adapter = new GridViewAdapter(getApplicationContext(), lists);
					gridViewTrackers.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					Utils.ibeaconArr.add(dbTracker.getDevice_addr());
				}else{
					Utils.showMsg(MainActivity.this,"添加设备失败");
				}
			}else{
				Utils.showMsg(MainActivity.this,"添加设备取消");
			}
		
		}
		super.onActivityResult(requestCode, resultCode, data);
		
	}
}
