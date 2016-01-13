package com.github.blesign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.blesign.adapter.GridViewAdapter;
import com.github.blesign.adapter.GridViewAdapter.ViewHolder;
import com.github.blesign.dao.BeaconDAO;
import com.github.blesign.model.Tracker;
import com.github.blesign.utils.BitmapUtils;
import com.github.blesign.utils.Consts;
import com.github.blesign.utils.LogUtil;
import com.github.blesign.utils.Utils;

public class MainActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	//UI
	private GridView gridViewTrackers;
	private GridViewAdapter adapter;
	private ArrayList<Tracker> lists;
	
	//Add Beacon
	private BeaconDAO beaconDAO;
	
	//拍照功能
	private File fos;
	private String path;
	private String name;
	private Uri uri;
	
	//窗口配置
	private View view; // 防丢器属性view
	private LayoutInflater inflater;
	private PopupWindow pop;
	private RelativeLayout layoutOpenCamera, layoutSelectRing, 
						   layoutDisconnectDelete, layoutConnection, layoutDisconnection;
	private TextView tvShowRingName, tvShowDistance;
	private SeekBar mSeekBar;
	
	private  int mSlectedItem = -1; // 当前防丢器在adapter中的位置（size-1）
	private Tracker tracker; // 当前防丢器
	
	//设置通知铃声
	private String defaultUri, defaultName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
			setData();
			setupView();
			addListener();
			getData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void getData() {
		// TODO Auto-generated method stub
		
	}

	private void setData() {
		// TODO Auto-generated method stub
		beaconDAO = new BeaconDAO(getApplicationContext());
		inflater = LayoutInflater.from(MainActivity.this);
		defaultUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION).toString();
		defaultName = "跟随系统";
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
				mSlectedItem = position;
				tracker = adapter.getItem(position);
				// 引入窗口配置文件  
				if(view == null){
					view = inflater.inflate(R.layout.tracker_unlinked, null); 
					setupPopView();
					addPopListener();
				}
				setTracker();
				if(pop == null){
	        		setupPop(parent, arg1, arg3, holder); 
	        	}
	        	if(pop.isShowing()) {  
	                // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏  
	                pop.dismiss();  
	            } else {  
	                // 显示窗口  
//	                pop.showAsDropDown(gvAdapter.getView((int)arg3, arg1, parent) , 0, 0);  
	                pop.showAsDropDown(adapter.getView((int)arg3, arg1, parent));
	                int[] location = new int[2];  
	                arg1.getLocationOnScreen(location);  
//	                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
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
				tracker.setDistance(Consts.DEFAULT_DISTANCE);
				tracker.setEnabled(0);
				tracker.setState(Consts.TRACKER_STATE_UNSELECTED);
				tracker.setRingName(defaultName);
				tracker.setRingUri(defaultUri);
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
					Utils.showMsg(this,"添加设备失败");
				}
			}else{
				Utils.showMsg(this,"添加设备取消");
			}
		}else if(requestCode == Consts.CAMERA_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				LogUtil.i(TAG, "resultCode == RESULT_OK");
				savePhoto();
			}else if(resultCode == RESULT_CANCELED){
				LogUtil.i(TAG, "resultCode == RESULT_CANCELED");
			}
		}else if(requestCode == Consts.REQUEST_SELECT_RING){
			if(resultCode == RESULT_OK && data != null){
				String u = data.getStringExtra(Consts.EXTRA_RING_URI);
				String ringName = data.getStringExtra(Consts.EXTRA_RING_NAME);
				uri  = Uri.parse(u);
				updateCurrentTracker(ringName, u);
			}else{
				LogUtil.i(TAG, "Select ring failed!");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateCurrentTracker(String ringName, String u) {
		beaconDAO.updateRing(tracker.getId(), ringName, u);
		tvShowRingName.setText(ringName);
		tracker.setRingName(ringName);
		tracker.setRingUri(u);
		lists.get(mSlectedItem).setRingName(ringName);
		lists.get(mSlectedItem).setRingUri(u);
	}

	protected void setTracker() {
		tvShowDistance.setText(tracker.getDistance());
		tvShowRingName.setText(tracker.getRingName());
		mSeekBar.setProgress(tracker.getDistance());
	}

	protected void setupPopView() {
		layoutOpenCamera = (RelativeLayout)view.findViewById(R.id.layout_tracker_open_camera);
		layoutSelectRing = (RelativeLayout)view.findViewById(R.id.layout_select_ring);
		tvShowRingName = (TextView)view.findViewById(R.id.tv_show_ring_name);
		tvShowDistance = (TextView)view.findViewById(R.id.tv_tracker_show_distance);
		mSeekBar = (SeekBar)view.findViewById(R.id.pop_seekbar);
		layoutDisconnectDelete = (RelativeLayout)view.findViewById(R.id.layout_tracker_disconnect_delete);
		layoutConnection = (RelativeLayout)view.findViewById(R.id.layout_tracker_connect);
		layoutDisconnection = (RelativeLayout)view.findViewById(R.id.layout_tracker_disconnect);
	}
	
	protected void addPopListener() {
		layoutOpenCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				openCamera();
			}
		});
		
		layoutSelectRing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SelectRingActivity.class);
				startActivityForResult(intent, Consts.REQUEST_SELECT_RING);
			}
		});
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				beaconDAO.updateDistanceByMac(tracker.getDevice_addr(), progress);
				tvShowDistance.setText(progress+"");
    			tracker.setDistance(progress);
    			lists.get(mSlectedItem).setDistance(progress);
//    			lists.set(mSlectedItem, tracker);
			}
		});
		
		layoutDisconnectDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		layoutConnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		layoutDisconnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void setupPop(AdapterView<?> parent, View arg1, long arg3, final ViewHolder holder) {
		// 创建PopupWindow对象  
    	pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
    	// 需要设置一下此参数，点击外边可消失  
    	pop.setBackgroundDrawable(new BitmapDrawable());  
    	//设置点击窗口外边窗口消失  
    	pop.setOutsideTouchable(false);  
    	// 设置此参数获得焦点，否则无法点击  
    	pop.setFocusable(true);
    	pop.setTouchable(true);
    	//设置PopupWindow消失的时候触发的事件
        pop.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
            	holder.ivSelectedBg.setVisibility(View.INVISIBLE);
            }
        });
        
	}
	/**
	 * 启用系统相机
	 * /sdcard/DCIM/Camera
	 */
	public void openCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory("android.intent.category.DEFAULT");
		
		path = Environment.getExternalStorageDirectory()+"/DCIM/Camera";
		name = "BLESIGN_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        fos=null;  
        try {  
        	File dir = new File(path);
        	if(!dir.exists()){
        		dir.mkdirs();
        	}
            fos=new File(path, name);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        Uri u=Uri.fromFile(fos);  
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);  
  
        startActivityForResult(intent, Consts.CAMERA_REQUEST_CODE);
	}
	
	/**
	 * 保存照片到系统相册
	 */
	public void savePhoto() {
	    // 首先保存图片
	    File appDir = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera");
	    if (!appDir.exists()) {
	        appDir.mkdirs();
	    }
//	    File file = new File(path, name);
	    Bitmap bmp = BitmapUtils.loadBitmap(fos.getPath(), 800, 100);
	    try {
	        OutputStream fs = new FileOutputStream(fos.getPath());
	        bmp.compress(CompressFormat.PNG, 70, fs);
	        fs.flush();
	        fs.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
		}
	    
	    // 其次把文件插入到系统图库
	    String s = null;
	    try {
	        s = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), fos.getAbsolutePath(), name, null);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    //delete another picture
	    String params[] = new String[]{Utils.getFileByUri(Uri.parse(s), MainActivity.this)};
	    MainActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", params);
	    // 最后通知图库更新
	    getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fos)));//Uri.parse("file://" + path)
	}
	

}
