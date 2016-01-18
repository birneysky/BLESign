package com.github.blesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.blesign.utils.Consts;
import com.github.blesign.utils.LogUtil;
import com.github.blesign.utils.Utils;

@SuppressLint("NewApi")
public class AddBeaconActivity extends Activity {
	private RelativeLayout titleBack;
	private static final String TAG = AddBeaconActivity.class.getSimpleName();
	private ImageView ivPointView;
	private ImageView ivRemoteView;
	private ImageView ivAddNewTracker;
	
	//ibeacon para
	private boolean isscaning;
	public static final int REQEUSET_ADD_IBEACON_NAME = 4;
	public static final int REQUEST_ADD_IBEACON = 3;
	
	private Handler mHanlder = new Handler();
	private final int SCAN_TIME = 6000;
	private String major, minor, addAddress="";
	private BluetoothAdapter mBluetoothAdapter;
	private int devicecount = 0;
	private TextView tvCenterNotice, tvShowNoticeBelow, titleRight, titleCenture;
	private static final int REQUEST_ENABLE_BT = 1;
	// 10秒后停止查找搜索.
//    private static final long SCAN_PERIOD = 10000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_beacon);
		setupView();
		addListener();
		initialize();
		checkBlueTooth();
	}
	
	
	/**
	 * 检查蓝牙功能是否可用，无用给予提示退出
	 */
	private boolean checkBlueTooth() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            return false;
        }
		return true;
	}
	private void setupView() {
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		titleRight = (TextView)findViewById(R.id.tv_title_bar_right_add);
		titleRight.setVisibility(View.GONE);
		titleCenture = (TextView)findViewById(R.id.tv_title_bar_title);
//		titleCenture.setText("添加");
		ivRemoteView = (ImageView) findViewById(R.id.iv_add_beacon_small_point);
		ivPointView = (ImageView) findViewById(R.id.iv_center_notice);
		tvCenterNotice = (TextView)findViewById(R.id.tv_add_beacon_center_notice);
		tvCenterNotice.setText(getResources().getString(R.string.add_beacon_center_notice_text));
		tvShowNoticeBelow = (TextView)findViewById(R.id.tv_show_notice_below);
		tvShowNoticeBelow.setText(getResources().getString(R.string.add_beacon_notice_text));
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddBeaconActivity.this.finish();
			}
		});
		
		ivPointView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		        if (!mBluetoothAdapter.isEnabled()) {
	                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		        }else{
		        	if(isscaning)return;
		        	setAnimation();
		        	ScanDeivce(true);
		        }
			}
		});
	}

	private void setAnimation() {
		int moveViewWidth 	= ivRemoteView.getWidth();
		int pointViewWidth 	= ivPointView.getWidth();
		final Animation animation = new RotateAnimation(0, 360, moveViewWidth/2, -pointViewWidth/2-3);
		animation.setDuration(8000);
		ivRemoteView.startAnimation(animation);
	}
	
	private void ScanDeivce(boolean enable) { // 添加beacon
		if (enable) {
			isscaning = true;
			mHanlder.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					isscaning = false;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							LogUtil.i(TAG, "device count = " + devicecount+", device mac = " + addAddress);
							if(devicecount == 0){ 
								// 未发现设备，重新扫描
								Intent connectionFailed = new Intent(AddBeaconActivity.this, ConnectionStateActivity.class);
								connectionFailed.putExtra(Consts.CONNECTION_STATE, Consts.EXTRA_ADD_BEACON_FAILED);
								startActivityForResult(connectionFailed, Consts.REQUEST_ADD_BEACON_CONNECTION_STATE);
							}else{
								//添加beacon发现设备，
								Intent connectionSuccess = new Intent(AddBeaconActivity.this, ConnectionStateActivity.class);
								connectionSuccess.putExtra(Consts.CONNECTION_STATE, Consts.EXTRA_ADD_BEACON_OK);
								connectionSuccess.putExtra(Consts.DEVICE_MAC, addAddress);
								startActivityForResult(connectionSuccess, Consts.REQUEST_ADD_BEACON_CONNECTION_STATE);
							}
							ivRemoteView.clearAnimation();
						}
					});
				}
			}, SCAN_TIME);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			isscaning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	public  BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecords) {
			/*final IBeacon ibeacon = IBeaconClass.fromScanData(device,rssi,scanRecords);
			LogUtil.i(TAG, ibeacon.toString());*/
			String device_mac = device.getAddress();
			if(device_mac.length() == 0){ // 扫描无效Utils.showMsg(getApplicationContext(), "扫描地址无效，device address为空");
				return;
			}
			if(Utils.ibeaconArr.contains(device_mac)){ // ?重复扫描
				//添加,重复则退出
				return;
			}else{ // 不重复
				addAddress = device_mac;
			}
			if(devicecount != 0){ // ?扫描到的非第一个设备Utils.showMsg(getApplicationContext(), "扫描不是第一个设备，device count");
				LogUtil.i(TAG, device_mac);
				return;
			}
			devicecount += 1;
			LogUtil.i(TAG, "addAddress = " + addAddress+", devicecount = "+devicecount);
		}
	};
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
			Utils.showMsg(getApplicationContext(), "开启蓝牙才能添加设备");
            return;
        }
		if(data==null)return;
		if(requestCode == Consts.REQUEST_ADD_BEACON_CONNECTION_STATE){
			if (resultCode == RESULT_OK){
				// 返回新添加的Tracker
				data.putExtra("tracker_mac", addAddress);
				setResult(RESULT_OK, data);
				finish();
			} else if (resultCode == Consts.RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN){
				// TODO 数据恢复
				devicecount = 0;
				// 重新扫描
				setAnimation();
				ScanDeivce(true);
			}else if(resultCode == RESULT_CANCELED){
				Log.i(TAG, Utils.ibeaconArr.size()+", "+devicecount);
				devicecount = 0;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 */
	private void initialize() {
		IbeaconApplication application = (IbeaconApplication) getApplication();
		if(application.isSupport){
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}
	}
	
}
