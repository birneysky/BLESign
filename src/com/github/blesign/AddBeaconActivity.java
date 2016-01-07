package com.github.blesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AddBeaconActivity extends Activity {
	private RelativeLayout titleBack;
	private static final String TAG = AddBeaconActivity.class.getSimpleName();
	private ImageView ivPointView;
	private ImageView ivRemoteView;
	private ImageView ivAddNewTracker;
//	private BluetoothLeService mService;
	
	//ibeacon para
	private boolean isscaning;
//	private boolean mConnected;
//	private LeDeviceListAdapter mLeDeviceListAdapter;
	public static final int REQEUSET_ADD_IBEACON_NAME = 4;
	public static final int REQUEST_ADD_IBEACON = 3;
	
//	private String ibeaconName="";//return ibeaconName
	private Handler mHanlder = new Handler();
	private final int SCAN_TIME = 6000;
	private String major, minor, addAddress="";
	private BluetoothAdapter mBluetoothAdapter;
	private int devicecount = 0;
	private String scanType; // 添加beacon：空；帮助众寻：scanTypeHelp
	private TextView tvCenterNotice, tvShowNoticeBelow;
	private double mLat;//获取当前位置，为帮助众寻
	private double mLong;
	private String helpAddress;
	private SharedPreferences usersp;
	private static final int REQUEST_ENABLE_BT = 1;
//	private static final int OK=1;
	// 10秒后停止查找搜索.
//    private static final long SCAN_PERIOD = 10000;
//    private boolean mScanning;
//    private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_beacon);
//		mHandler = new Handler();
		getData();
		setupView();
		addListener();
		initialize();
		checkBlueTooth();
		getCurrentLocation();
	}
	
	/**
	 * 帮助众寻需要当前地址
	 */
	private void getCurrentLocation() { 
		final BaiduUtils mBaidu = new BaiduUtils(AddBeaconActivity.this);
		mBaidu.setCallback(new BaiduUtils.Callback() {
			@Override
			public void onResult(BDLocation location) {
				if(location ==null){
					return;
				}
				mLat = location.getLatitude();
				mLong = location.getLongitude();
//				mAddress = location.getAddrStr();
				mBaidu.stop();
			}
		});
		mBaidu.start();
	}
	private void getData() {
		Intent intent = getIntent();
		scanType = intent.getStringExtra(Consts.EXTRA_KEY_SCAN_TYPE);
		mPbulicFind = (PublicFind) getIntent().getSerializableExtra("data");
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
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		ivRemoteView = (ImageView) findViewById(R.id.iv_add_beacon_small_point);
		ivPointView = (ImageView) findViewById(R.id.iv_center_notice);
		ivAddNewTracker = (ImageView) findViewById(R.id.iv_title_bar_right_add);
		ivAddNewTracker.setVisibility(View.GONE);
		tvCenterNotice = (TextView)findViewById(R.id.tv_add_beacon_center_notice);
		tvShowNoticeBelow = (TextView)findViewById(R.id.tv_show_notice_below);
		if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){
			tvCenterNotice.setText(getResources().getString(R.string.add_beacon_center_notice_text_help));
			tvShowNoticeBelow.setText(getResources().getString(R.string.add_beacon_notice_text_help));
		}else{
			tvCenterNotice.setText(getResources().getString(R.string.add_beacon_center_notice_text));
			tvShowNoticeBelow.setText(getResources().getString(R.string.add_beacon_notice_text));
		}
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddBeaconActivity.this.finish();
				Utils.isScanAddOrHelp=false;
			}
		});
		
		ivPointView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*if(mBluetoothAdapter == null){
					final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
					mBluetoothAdapter = bluetoothManager.getAdapter();
				}*/
				// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		        if (!mBluetoothAdapter.isEnabled()) {
	                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		        }else{
		        	if(isscaning)return;
		        	setAnimation();
		        	Utils.isScanAddOrHelp=true;
		        	if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){ //帮助众寻
		        		upScanTimes();
		        		helpScan();
		        	} 
		        	ScanDeivce(true);
		        }
			}
		});
	}

	/**
	 * 帮助众寻
	 */
	protected void helpScan() {	
		if(mBluetoothAdapter==null)return;
		String mac = mPbulicFind.getFindMac();
		if(mac != null){
			Log.i("info", "help scan mac = " + mac);
			helpAddress = mac;
			if("null".equals(mac)){
				return;
			}
		}else{
			return;
		}
	}

	/**
	 * 向后台更新扫描次数
	 */
	protected void upScanTimes() {
		if (mPbulicFind == null)
			return;
		final String deviceMac = mPbulicFind.getFindMac();
		if (deviceMac == null){
			Utils.showMsg(getApplicationContext(), "iPhone发布的众寻，暂时无法查找！");
			return;
		}
		if ("null".equals(deviceMac)) { // 应该基本用不到
			Intent connection = new Intent(AddBeaconActivity.this, ConnectionStateActivity.class);
			connection.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_HELP);
			connection.putExtra(Consts.CONNECTION_STATE, Consts.EXTRA_ADD_BEACON_FAILED);
			startActivityForResult(connection, Consts.REQUEST_HELP_SEARCH);
			ivRemoteView.clearAnimation();
		}
		else if(deviceMac.length() == 0){ // iOS发出的丢失信息，根据major和miner搜寻
			Utils.showMsg(getApplicationContext(), "iPhone发布的众寻，暂时无法查找！");
			return;
		}
		else {
			// 需要发送到后台
			AppRequest request = new AppRequest();
			request.setmRequestURL("/help/SearchHelpAction!updateScan");
			request.putPara("clientType", "android");
			request.putPara("helpUserID", usersp.getInt(ConstsUser.ID, 0) + "");
			request.putPara("userID", mPbulicFind.getUserID());
			request.putPara("findID", mPbulicFind.getFindID());
			request.putPara("findMac", deviceMac);
			request.putPara("helpLong", mLong + "");
			request.putPara("helpLat", mLat + "");
			new AppThread(AddBeaconActivity.this, request, new AppHandler() {
				@Override
				protected void handle(AppRequest request, AppResponse response) {
					if ("0".equals(response.getmCode())) {
					
					}else{
						Utils.showMsg(getApplicationContext(), "添加查找次数失败");//response.getmMessage()
					}
				}
			}).start();
		}
	}
	private void setAnimation() {
		int moveViewWidth 	= ivRemoteView.getWidth();
		int pointViewWidth 	= ivPointView.getWidth();
		final Animation animation = new RotateAnimation(0, 360, moveViewWidth/2, -pointViewWidth/2-3);
		animation.setDuration(8000);
		ivRemoteView.startAnimation(animation);
	}
	
	private void ScanDeivce(boolean enable) { // 添加beacon
		/*if(mBluetoothAdapter == null){
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}*/
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
							Log.i(TAG, "device count = " + devicecount);
							if(devicecount == 0){ 
								// 未发现设备，重新扫描
								Intent connectionFailed = new Intent(AddBeaconActivity.this, ConnectionStateActivity.class);
								connectionFailed.putExtra(Consts.CONNECTION_STATE, Consts.EXTRA_ADD_BEACON_FAILED);
								if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){//帮助
									connectionFailed.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_HELP);
								}else{
									connectionFailed.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_ADD);
								}
								startActivityForResult(connectionFailed, Consts.REQUEST_ADD_BEACON_CONNECTION_STATE);
							}else{
								//发现设备，
								Intent connectionSuccess = new Intent(AddBeaconActivity.this, ConnectionStateActivity.class);
								connectionSuccess.putExtra(Consts.CONNECTION_STATE, Consts.EXTRA_ADD_BEACON_OK);
								if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){ // 帮助众寻扫描到了
									connectionSuccess.putExtra(Consts.DEVICE_MAC, helpAddress);
									connectionSuccess.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_HELP);
									startActivityForResult(connectionSuccess, Consts.REQUEST_HELP_SEARCH);
								}else{ // 添加beacon扫描到了
									connectionSuccess.putExtra(Consts.DEVICE_MAC, addAddress);
									connectionSuccess.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_ADD);
									startActivityForResult(connectionSuccess, Consts.REQUEST_ADD_BEACON_CONNECTION_STATE);
								}
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
			final iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecords);
        	if(!ibeacon.proximityUuid.equalsIgnoreCase(Consts.UUID)){
        		return;
        	}
        	Log.i(TAG, "扫描到的设备 "+iBeaconClass.iBeaconToString(ibeacon));
			if(Utils.scenes.contains(device.getAddress())){ // ?重复扫描Utils.showMsg(getApplicationContext(), "重复扫描，scenes");
				return;
			}
			String device_mac = device.getAddress();
			major = Integer.toString(ibeacon.major);
			minor = Integer.toString(ibeacon.minor);
			if(device_mac.length() == 0){ // 扫描无效Utils.showMsg(getApplicationContext(), "扫描地址无效，device address为空");
				return;
			}
			if(Utils.ibeaconArr.contains(device_mac)){ // ?重复扫描
				if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){//帮助众寻,重复则找到自己已经丢失的设备
					//执行下面代码
					return;
				}else{//添加,重复则退出
					return;
				}
			}else{ // 不重复
				if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){//帮助众寻，不重复
					//判断Mac地址是否相等，再进行下步操作:不等，返回；等，进行下步操作
					if(device_mac.compareToIgnoreCase(helpAddress) != 0){
						return;
					}
				}else{// 添加，不重复则添加
					//执行下面代码
					addAddress = device_mac;
				}
			}
			Log.i(TAG, "addAddress = " + addAddress+", scanType = "+scanType+", devicecount = "+devicecount);
			if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){
				devicecount += 1;
			}else{
				if(devicecount != 0){ // ?扫描到的非第一个设备Utils.showMsg(getApplicationContext(), "扫描不是第一个设备，device count");
					return;
				}
				devicecount += 1;
			}
			/*runOnUiThread(new Runnable() {
				@Override
				public void run() { // ?成功
//					Intent foundedDevice = new Intent(AddBeaconActivity.this, AddNewTrackerActivity.class);
//					startActivityForResult(foundedDevice,REQEUSET_ADD_IBEACON_NAME);
					Intent connectionSuccess = new Intent(AddBeaconActivity.this, ConnectionStateActivity.class);
					connectionSuccess.putExtra(Consts.CONNECTION_STATE, Consts.EXTRA_ADD_BEACON_OK);
					connectionSuccess.putExtra(Consts.DEVICE_MAC, device_mac);
					if(Consts.EXTRA_SCAN_TYPE_HELP.equals(scanType)){ // 帮助众寻扫描到了
						connectionSuccess.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_HELP);
						startActivityForResult(connectionSuccess, Consts.REQUEST_HELP_SEARCH);
					}else{ // 添加beacon扫描到了
						connectionSuccess.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_ADD);
						startActivityForResult(connectionSuccess, Consts.REQUEST_ADD_BEACON_CONNECTION_STATE);
					}
					ivRemoteView.clearAnimation();
				}
			});*/
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
			Utils.showMsg(getApplicationContext(), "开启蓝牙才能使用添加功能");
            return;
        }
		if(data==null)return;
		if(requestCode == Consts.REQUEST_ADD_BEACON_CONNECTION_STATE){
			if (resultCode == AddBeaconActivity.REQEUSET_ADD_IBEACON_NAME){
				// 返回新添加的Tracker
				data.putExtra("tracker_mac", addAddress);
				data.putExtra("major", major);
				data.putExtra("minor", minor);
				setResult(AddBeaconActivity.REQUEST_ADD_IBEACON, data);
				finish();
			} else if (resultCode == Consts.RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN){
				// TODO 数据恢复
				devicecount = 0;
				// 重新扫描
				setAnimation();
				ScanDeivce(true);
			}else if(resultCode == RESULT_CANCELED){
				Log.i(TAG, Utils.ibeaconArr.size()+", "+devicecount+", scanType = "+scanType);
				devicecount = 0;
			}
		}else if(requestCode == Consts.REQUEST_HELP_SEARCH){//帮助众寻
			if (resultCode == Consts.RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN){
				// TODO 数据恢复
				devicecount = 0;
				
				// 重新扫描
				setAnimation();
				ScanDeivce(true);
			}else if(resultCode == Consts.RESULT_HELP_SEARCH_SATATE){//
				boolean isFound = data.getBooleanExtra("isFound",false);
				if(isFound){
					Intent retIntent = new Intent();
					retIntent.putExtra("isFound", isFound);
					setResult(Consts.RESULT_HELP_SEARCH_SATATE, retIntent);
					AddBeaconActivity.this.finish();
				}
			}else if(resultCode == RESULT_CANCELED){
				// TODO 数据恢复
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
