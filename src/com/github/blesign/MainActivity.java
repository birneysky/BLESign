package com.github.blesign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
import com.github.blesign.operation.BluetoothLeService;
import com.github.blesign.operation.SampleGattAttributes;
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
	//title bar
	private RelativeLayout titleBack;
	private TextView titleRight, titleCenture;
	//Add Beacon
	private BeaconDAO beaconDAO;
	
	//拍照功能
	private File fos;
	private String path;
	private String name;
	private Uri uri;
	//设置通知铃声
	private String defaultUri, defaultName;
	
	//窗口配置
	private View view; // 防丢器属性view
	private LayoutInflater inflater;
	private PopupWindow pop;
	private RelativeLayout layoutOpenCamera, layoutSelectRing, 
						   layoutDisconnectDelete, layoutConnection, layoutDisconnection;
	private TextView tvShowRingName, tvShowDistance;
	private SeekBar mSeekBar;
	
	private int mSlectedItem = -1; // 当前防丢器在adapter中的位置（size-1）
	private Tracker tracker; // 当前防丢器
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1://已连接
				
				break;
			case 0://已断开
				
				break;
			default:
				break;
			}
		}
	};
	private boolean mConnected;
	
	private BluetoothLeService mBluetoothLeService;
	// Code to manage Service lifecycle.
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	
	private Thread connectorThread;
	protected List<BluetoothGattService> list;
	protected BluetoothGattService alarmService, photoService;
	protected BluetoothGattCharacteristic alarmCharacteristic, photoCharacteristic;

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. 
	// This can be a result of read or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.i(TAG, "action = " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				// TODO 变更UI
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				// TODO 变更UI
				Message msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				// TODO 检查是否包含特定服务和特性，进行读写监听操作
				displayGattServices(mBluetoothLeService.getSupportedGattServices());
				connectorListener();
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				//
				String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
//				LogUtil.i(TAG, "data = "+  data);
			}
		}
	};
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        try {
			setData();
			setupView();
			addListener();
			initService();
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		}
    }

    private boolean scanning = true;

    protected void connectorListener() {
		// TODO Auto-generated method stub
    	if(connectorThread == null){
    		connectorThread = new Thread(){
    			@Override
    			public void run() {
    				// TODO 循环扫描iBeacon
    				while(scanning){
    					try {
    						this.sleep(1000);
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    					boolean rssi = mBluetoothLeService.getRssiVal();
    					mBluetoothLeService.readCharacteristic(photoCharacteristic);
//    					mBluetoothLeService.wirteCharacteristic(alarmCharacteristic);
    				}
    				super.run();
    			}
    		};
    		connectorThread.start();
    	}
    	Utils.connectorArr.add(tracker.getDevice_addr());
	}

    
 // Demonstrates how to iterate through the supported GATT
 	// Services/Characteristics.
 	// In this sample, we populate the data structure that is bound to the
 	// ExpandableListView
 	// on the UI.
 	private void displayGattServices(List<BluetoothGattService> gattServices) {
 		if (gattServices == null)
 			return;
 		String uuid = null;
 		// Loops through available GATT Services.
 		for (BluetoothGattService gattService : gattServices) {
 			uuid = gattService.getUuid().toString();
 			if(SampleGattAttributes.SETTING_ALARM_SERVICE.equals(uuid)){
 				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
 				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
 	 				String alarm = gattCharacteristic.getUuid().toString();
 	 				if(SampleGattAttributes.SETTING_ALARM_CHARACTERISTIC.equals(alarm)){
 	 					LogUtil.i(TAG, "alarm");
 	 					alarmCharacteristic = gattCharacteristic;
 	 				}
 	 			}
 			}else if(SampleGattAttributes.SETTING_CAMERA_SERVICE.equals(uuid)){
 				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
 				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
 	 				String photo = gattCharacteristic.getUuid().toString();
 	 				if(SampleGattAttributes.SETTING_CAMERA_CHARACTERISTIC.equals(photo)){
 	 					LogUtil.i(TAG, "photo");
 	 					photoCharacteristic = gattCharacteristic;
 	 				}
 	 			}
 			}
 		}
 	}
	@Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
	}
	private void initService() {
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		boolean bll = this.getApplicationContext().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		if (bll) {
			LogUtil.i("info", "-------initService--------");
		} else {
			LogUtil.i("info", "========initService=======");
		}
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	private void setData() {
		beaconDAO = new BeaconDAO(getApplicationContext());
		inflater = LayoutInflater.from(MainActivity.this);
		defaultUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION).toString();
		defaultName = "跟随系统";
	}

	private void setupView() {
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		titleBack.setVisibility(View.GONE);
		titleRight = (TextView)findViewById(R.id.tv_title_bar_right_add);
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
		titleRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 判断是否脸接并让设备警报
			}
		});
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
		int res = beaconDAO.updateRing(tracker.getDevice_addr(), ringName, u);
		if(res == 0)	return;
		tvShowRingName.setText(ringName);
		tracker.setRingName(ringName);
		tracker.setRingUri(u);
		lists.get(mSlectedItem).setRingName(ringName);
		lists.get(mSlectedItem).setRingUri(u);
	}

	protected void setTracker() {
		tvShowDistance.setText(tracker.getDistance()+"");
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
				intent.putExtra(Consts.DEVICE_MAC, tracker.getDevice_addr());
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
				Tracker trackerdel = lists.get(mSlectedItem);
				if (trackerdel != null) {
					String mac = trackerdel.getDevice_addr();
					if(mac != null){
						int result = beaconDAO.deleteBeacon(trackerdel.getId());// 删除数据库
						if(result == 0){
							Utils.showMsg(getApplicationContext(), "删除失败，请重試！");
							return;
						}
						Utils.ibeaconArr.remove(mac);
						lists.remove(trackerdel);
						adapter = new GridViewAdapter(getApplicationContext(), lists);
						gridViewTrackers.setAdapter(adapter);
						if (pop != null && pop.isShowing()) {
							// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
							pop.dismiss();
						}
					}
				}
			}
		});
		
		layoutConnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBluetoothLeService != null) {
					final boolean result = mBluetoothLeService.connect(tracker.getDevice_addr());
					Log.d(TAG, "Connect request result = " + result);
				}
			}
		});
		
		layoutDisconnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBluetoothLeService.disconnect();
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
		new Thread(){
			public void run() {
				// 首先保存图片
			    File appDir = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera");
			    if (!appDir.exists()) {
			        appDir.mkdirs();
			    }
//			    File file = new File(path, name);
			    Bitmap bmp = BitmapUtils.loadBitmap(fos.getPath(), 800, 1000);
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
		}.start();
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

}
