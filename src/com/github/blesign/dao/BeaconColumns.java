package com.github.blesign.dao;


public class BeaconColumns {
	
		/** use mac as ID*/
		public final static String ID="id";
		/** The user defined sensor name */
		public final static String NAME = "name";
		/** The beacon service uuid */
		public final static String UUID = "uuid";
		public final static String MAC="mac";
		/** The beacon major number */
//		public final static String MAJOR = "major";
		/** The beacon minor */
//		public final static String MINOR = "minor";
		/** The last signal strength in percentage */
		public final static String IMGURL = "imgurl";
		/** beacon set distance of alarm*/
		public final static String DISTANCE="distance";
		/** 1 if beacon notifications are enabled, 0 if disabled */
		public final static String ENABLED = "enabled";
		
		/**
		 * 众寻状态
		 * public static final int TRACKER_STATE_UNSELECTED = 0; // 未连接
			public static final int TRACKER_STATE_LOST =1;		// 已丢失
			public static final int TRACKER_STATE_TRACKING = 2;	// 正在防丢
		 */
		public final static String STATE="state";
		
		public final static String RING_NAME = "ringName";
		public final static String RING_URI = "ringUri";
	
		
		public static  String[] BEACON_PROJECTION = new String[] { BeaconColumns.ID, 
															 BeaconColumns.NAME,
															 BeaconColumns.UUID,
															 BeaconColumns.MAC, 
															 BeaconColumns.IMGURL, 
															 BeaconColumns.DISTANCE,
															 BeaconColumns.ENABLED,
															 BeaconColumns.STATE,
															 BeaconColumns.RING_NAME,
															 BeaconColumns.RING_URI
															 };
	
}
