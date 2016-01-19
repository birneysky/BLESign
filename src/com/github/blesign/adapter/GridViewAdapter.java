package com.github.blesign.adapter;

import java.util.ArrayList;

import com.github.blesign.R;
import com.github.blesign.model.Tracker;
import com.github.blesign.utils.Consts;
import com.github.blesign.utils.ScreenUtils;
import com.view.circleimageview.CircleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<Tracker> lists;
	private Context context;
	
	private void setMusics(ArrayList<Tracker> lists) {
		if (lists != null) {
			this.lists = lists;
		} else {
			this.lists = new ArrayList<Tracker>();
		}
	}
	
	/*public void setSeclection(int position) {
	}*/
	
	public GridViewAdapter(Context context, ArrayList<Tracker> lists) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.setMusics(lists);
	}

	@Override
	public int getCount() {
		return lists.size() + 1;
	}

	@Override
	public Tracker getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.gridview_item, null);
			holder = new ViewHolder();
			holder.civTrackerPhoto = (CircleImageView) convertView.findViewById(R.id.circleImageView_tracker_photo);
			holder.civStatebg =(CircleImageView) convertView.findViewById(R.id.circleImageView_tracker_state_bg);
			holder.tvTrackerName = (TextView) convertView.findViewById(R.id.tv_name_tracker_item);
			holder.tvTrackerState = (TextView) convertView.findViewById(R.id.tv_state_tracker_item);
			holder.frameLayoutBg = (FrameLayout) convertView.findViewById(R.id.layout_state_tracker_item);
			holder.ivNoTrackerItemBg = (ImageView) convertView.findViewById(R.id.iv_no_tracker_item);
			holder.ivSelectedBg = (ImageView)convertView.findViewById(R.id.iv_tracker_item_selected);
//			holder.frameLayoutItemSelected = (FrameLayout)convertView.findViewById(R.id.layout_tracker_item_selected);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag(); 
//			convertView.setLayoutParams(new LayoutParams(parent.getWidth()/3, parent.getWidth()/3));
		}
		// 获取指定位置的数据
		int size = lists.size();
		Tracker tracker = null;
		if(size != position){
			 tracker = getItem(position);
		} 
		// 将数据写入item界面
		if (tracker != null) {
			holder.tvTrackerName.setText(tracker.getName());
			String pic_path = tracker.getTrackerIconPath();
			holder.civTrackerPhoto.setImageResource(R.drawable.app_logo);
			if(pic_path !=null){
				Bitmap bmp = BitmapFactory.decodeFile(Consts.IMG_PATH+pic_path);//if(bmp != null)
				holder.civTrackerPhoto.setImageBitmap(bmp);
			}
			int trackerState = tracker.getState();
			if(trackerState == Consts.TRACKER_STATE_CONNECT){
				holder.tvTrackerState.setVisibility(View.GONE);
				holder.civStatebg.setImageDrawable(context.getResources().getDrawable(R.drawable.selected_item_bg));
			} else if (trackerState == Consts.TRACKER_STATE_DISCONNECT) {
				holder.tvTrackerState.setVisibility(View.GONE);
				holder.civStatebg.setImageDrawable(context.getResources().getDrawable(R.drawable.unselected_item_bg));
			} 
			/*else if (trackerState == Consts.TRACKER_STATE_ICON_CHANGE_RED){//只需更改指示颜色为红色
				holder.civStatebg.setImageDrawable(context.getResources().getDrawable(R.drawable.lost_item_bg));
				holder.tvTrackerState.setVisibility(View.GONE);
			} else if (trackerState == Consts.TRACKER_STATE_ICON_CHANGE_BACK){
				holder.civStatebg.setImageDrawable(context.getResources().getDrawable(R.drawable.selected_item_bg));
				holder.tvTrackerState.setVisibility(View.GONE);
			}*/
			holder.ivNoTrackerItemBg.setVisibility(View.GONE);
		} else {
			holder.ivNoTrackerItemBg.setVisibility(View.INVISIBLE);
			holder.tvTrackerName.setVisibility(View.VISIBLE);
			holder.tvTrackerState.setVisibility(View.INVISIBLE);
			holder.civStatebg.setVisibility(View.INVISIBLE);
			holder.civTrackerPhoto.setVisibility(View.VISIBLE);
			holder.tvTrackerName.setText("添加设备");
			holder.civTrackerPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.listitem_add_device));
		}
		return convertView;
	}

	public class ViewHolder{
		private CircleImageView civTrackerPhoto;
		private CircleImageView civStatebg;
		private TextView tvTrackerName;
		private TextView tvTrackerState;
		private FrameLayout frameLayoutBg;
		public ImageView ivNoTrackerItemBg, ivSelectedBg;
//		private FrameLayout frameLayoutItemSelected;
	}

	
	
	
}


