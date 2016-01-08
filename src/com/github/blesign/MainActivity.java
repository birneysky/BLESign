package com.github.blesign;

import java.util.ArrayList;

import com.github.blesign.adapter.GridViewAdapter;
import com.github.blesign.model.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	//UI
	private GridView gridViewTrackers;
	private GridViewAdapter adapter;
	private ArrayList<Tracker> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        addListener();
    }

	private void setupView() {
		// TODO Auto-generated method stub
		gridViewTrackers = (GridView)findViewById(R.id.gridView_trackers);
		lists = new ArrayList<Tracker>();
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
				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	}
}
