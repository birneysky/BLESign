/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.blesign.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.github.blesign.MainActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.widget.Toast;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
@SuppressLint("SimpleDateFormat")
public class Utils {
	private static final String TAG=Utils.class.getSimpleName();
	private static final String LOCATIONFILE="ibeaconlocation.txt";
	private static final String BLE_PASS="987123";
	/**
	 * 已经添加的Beacon地址集合
	 */
	public static ArrayList<String> ibeaconArr= new ArrayList<String>();
	
	public static ArrayList<String> connectorArr = new ArrayList<String>();
	
	private static int txPower =-59;//设备距离一米的dBm值
	
	public static double calculateDistance(double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;
        double distance;
        if (ratio < 1.0) {
            distance =  Math.pow(ratio,10);
        }
        else {
            distance =  (0.42093)*Math.pow(ratio,6.9476) + 0.54992;
        }
//        System.out.println("avg mRssi: %s distance: %s"+rssi+distance);
        DecimalFormat df = new DecimalFormat(".00");
        double result= Double.parseDouble(df.format(distance));
        return result;
    }
	/** 
     * 通过Uri返回File文件 
     * 注意：通过相机的是类似content://media/external/images/media/97596 
     * 通过相册选择的：file:///storage/sdcard0/DCIM/Camera/IMG_20150423_161955.jpg 
     * 通过查询获取实际的地址 
     * @param uri 
     * @return pathname
     */  
    public static String getFileByUri(Uri uri, Context context) {  
        String path = null;  
        if ("file".equals(uri.getScheme())) {  
            path = uri.getEncodedPath();  
            if (path != null) {  
                path = Uri.decode(path);  
                ContentResolver cr = context.getContentResolver();  
                StringBuffer buff = new StringBuffer();  
                buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");  
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID, Images.ImageColumns.DATA }, buff.toString(), null, null);  
                int index = 0;  
                int dataIdx = 0;  
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {  
                    index = cur.getColumnIndex(Images.ImageColumns._ID);  
                    index = cur.getInt(index);  
                    dataIdx = cur.getColumnIndex(Images.ImageColumns.DATA);  
                    path = cur.getString(dataIdx);  
                }  
                cur.close();  
                if (index == 0) {  
                } else {  
                    Uri u = Uri.parse("content://media/external/images/media/" + index);  
                    LogUtil.i(TAG, "temp uri is :" + u);  
                }  
            }  
            if (path != null) {  
            	LogUtil.i(TAG, "path = "+path);
//                return new File(path); 
            	return path;
            }  
        } else if ("content".equals(uri.getScheme())) {  
            // 4.2.2以后  
            String[] proj = { MediaStore.Images.Media.DATA };  
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);  
            if (cursor.moveToFirst()) {  
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
                path = cursor.getString(columnIndex);  
            }  
            cursor.close();  
            LogUtil.i(TAG, "path = "+path);
//            return new File(path); 
            return path;
        } else {  
            LogUtil.i(TAG, "Uri Scheme:" + uri.getScheme());  
        }  
        return null;  
    }
    
	/**
	 * @param fileName
	 * @param btnText
	 */
	public static void removeFromSDcardFile(String btnText) {
		// 获取扩展SD卡设备状�?
		String sDStateString = android.os.Environment.getExternalStorageState();
		File myFile = null;
		FileReader reader = null;
		FileWriter fwriter = null;
		BufferedReader bRreader = null;
		BufferedWriter bwriter = null;
		String str;
		List<String> strs = new ArrayList<String>();
		if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			try {
				// 获取扩展存储设备的文件目�?
               File SDFile = android.os.Environment.getExternalStorageDirectory();
               File destDir = new File(SDFile.getAbsolutePath() +File.separator+ "ibeacon");//文件目录
				// 打开文件
				myFile = new File(destDir + File.separator +LOCATIONFILE);
				reader = new FileReader(myFile);
				bRreader = new BufferedReader(reader);
				// 把删掉之后的按钮存在list�?
        	   while((str = bRreader.readLine()) != null ){
					if (!str.equals(btnText))
						strs.add(str);
				}
				fwriter = new FileWriter(myFile, false);
				bwriter = new BufferedWriter(fwriter);
				for (String st : strs) {
					bwriter.write(st);
					bwriter.newLine();
				}
				bwriter.flush();
				bwriter.close();// 关闭�?
				bRreader.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(TAG, "发生异常:" + e);
			}

		}
	}
	
	/**
	 * 取消蓝牙配对
	 */
	/** 
     * 与设备配对 参考源码：platform/packages/apps/Settings.git 
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java 
     */  
    static public boolean createBond(Class btClass, BluetoothDevice btDevice)  
    throws Exception  
    {  
        Method createBondMethod = btClass.getMethod("createBond");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  
   
    /** 
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git 
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java 
     */  
    static public boolean removeBond(Class btClass, BluetoothDevice btDevice)  
            throws Exception  
    {  
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  
   
    static public boolean setPin(Class btClass, BluetoothDevice btDevice,  
            String str) throws Exception  
    {  
        try  
        {  
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",  
                    new Class[]  
                    {byte[].class});  
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,  
                    new Object[]  
                    {str.getBytes()});  
            System.out.println("returnValue"+ returnValue);  
        }  
        catch (SecurityException e)  
        {  
            e.printStackTrace();  
        }  
        catch (IllegalArgumentException e)  
        {  
            e.printStackTrace();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        return true;  
   
    }  
   
    // 取消用户输入  
    static public boolean cancelPairingUserInput(Class btClass,  
            BluetoothDevice device)  
   
    throws Exception  
    {  
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);  
        return returnValue.booleanValue();  
    }  
   
    // 取消配对  
    static public boolean cancelBondProcess(Class btClass,  
            BluetoothDevice device)  
   
    throws Exception  
    {  
        Method createBondMethod = btClass.getMethod("cancelBondProcess");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);  
        return returnValue.booleanValue();  
    }  
   
    /** 
     * 反射：获取所有这个类的方法
     * @param clsShow 
     */  
    static public void printAllInform(Class clsShow)  
    {  
        try  
        {  
            // 取得所有方法  
            Method[] hideMethod = clsShow.getMethods();  
            int i = 0;  
            for (; i < hideMethod.length; i++)  
            {  
            	Log.i(TAG, "method name "+hideMethod[i].getName() + ";and the i is:" + i);
            }  
            // 取得所有常量  
            Field[] allFields = clsShow.getFields();  
            for (i = 0; i < allFields.length; i++)  
            {  
            	Log.i(TAG, "Field name "+allFields[i].getName());
            }  
        }  
        catch (SecurityException e)  
        {  
            e.printStackTrace();  
        }  
        catch (IllegalArgumentException e)  
        {  
            e.printStackTrace();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
          
      
    static public boolean pair(String strAddr)  
    {  
        boolean result = false;  
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
        bluetoothAdapter.cancelDiscovery();  
   
        if (!bluetoothAdapter.isEnabled())  
        {  
            bluetoothAdapter.enable();  
        }  
   
        if (!BluetoothAdapter.checkBluetoothAddress(strAddr))  
        { // 检查蓝牙地址是否有效  
        	Log.i(TAG, "mylog: "+ "devAdd un effient!");
        }  
   
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);  
   
        if (device.getBondState() != BluetoothDevice.BOND_BONDED)  
        {  
            try  
            {  
                Log.i(TAG, "mylog: "+ "NOT BOND_BONDED");
                Utils.setPin(device.getClass(), device, BLE_PASS); // 手机和蓝牙采集器配对  
                Utils.createBond(device.getClass(), device);  
//                remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice  
                result = true;  
            }  
            catch (Exception e)  
            {  
                Log.i(TAG, "mylog "+ "setPiN failed!");
                e.printStackTrace();  
            } //  
   
        }  
        else  
        {  
            Log.i(TAG, "mylog "+ "HAS BOND_BONDED");
            try  
            {  
                Utils.createBond(device.getClass(), device);  
                Utils.setPin(device.getClass(), device, BLE_PASS); // 手机和蓝牙采集器配对  
                Utils.createBond(device.getClass(), device);  
//                remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice  
                result = true;  
            }  
            catch (Exception e)  
            {  
                Log.i(TAG, "mylog"+"setPiN failed!");
                e.printStackTrace();  
            }  
        }  
        return result;  
    }
    
    /** 一次性压缩多个文件，文件存放至一个文件夹中*/
    public static File ZipMultiFile(List<File> files) {
    	InputStream input = null;
    	ZipOutputStream zipOut = null;
    	File zipFile = null;
    	try{
            zipFile = new File(Consts.ZIP_IMG_TEMP);
            if(zipFile.exists()){
            	zipFile.delete();
            }else{
				zipFile.createNewFile();
            }
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            for(int i = 0; i < files.size(); ++i){
				input = new FileInputStream(files.get(i));
				zipOut.putNextEntry(new ZipEntry(files.get(i).getName()));
                int temp = 0;
				while((temp = input.read()) != -1){
				    zipOut.write(temp);
				}
            }
            return zipFile;
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(zipOut != null){
				try {
					zipOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return zipFile;
    }
    /**  解压缩（压缩文件中包含多个文件）可代替上面的方法使用。
     * ZipInputStream类
     * */
    public static void upZipMultiFile(String zippath ,String outzippath){
    	try {
    		File file = new File(zippath);
    		File outFile = null;
    		ZipFile zipFile = new ZipFile(file);
    		ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
    		ZipEntry entry = null;
    		InputStream input = null;
    		OutputStream output = null;
    		while((entry = zipInput.getNextEntry()) != null){
    			System.out.println("解压缩" + entry.getName() + "文件");
    			outFile = new File(outzippath + File.separator + entry.getName());
    			if(!outFile.getParentFile().exists()){
    				outFile.getParentFile().mkdir();
    			}
    			if(!outFile.exists()){
    				outFile.createNewFile();
    			}
    			input = zipFile.getInputStream(entry);
    			output = new FileOutputStream(outFile);
    			int temp = 0;
    			while((temp = input.read()) != -1){
    				output.write(temp);
    			}
    		}
    			input.close();
    			output.close();
    		} catch (Exception e) {
    		   e.printStackTrace();
    		   }
    }
    
    /**
     * 获取当前日期
     */
    public static String getCurrentDate(){
    	Date date = new Date();
    	SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
    	return formate.format(date);
    }
    /**
     * 获取当前时间
     */
    public static String getCurrentTime(){
    	Date date = new Date();
    	SimpleDateFormat formate = new SimpleDateFormat("HH:mm:ss");
    	return formate.format(date);
    }
    /**
     * Toast 显示信息
     * @param context 
     * @param msg 要显示的文字
     */
    public static void showMsg(Context context,String msg){
    	Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    /**
     * 获取当前日期是星期几: 1,2,3,4,5,6,7
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate() {
    	Date date =new Date();
        String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    public static String bytesToHexString(byte[] src){  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    } 
}
