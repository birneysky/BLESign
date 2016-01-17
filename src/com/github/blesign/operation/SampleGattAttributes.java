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

package com.github.blesign.operation;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();//ʲô����
    public static String HEART_RATE_MEASUREMENT = "0000C004-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String SELECTED_CONFIG = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0";
    public static String SETTING_ALARM_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
    public static String SETTING_CAMERA_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
    public static String SETTING_ALARM_CHARACTERISTIC = "00002a06-0000-1000-8000-00805f9b34fb";
    public static String SETTING_CAMERA_CHARACTERISTIC = "00002a07-0000-1000-8000-00805f9b34fb";
    
    static {
        // Sample Services.
        attributes.put("0000fff00000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(SETTING_ALARM_SERVICE, "Setting Alarm Service");
        attributes.put(SETTING_CAMERA_SERVICE, "Setting Camera Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put(SELECTED_CONFIG, "-----------my ble device zijin--------------");
        attributes.put(SETTING_ALARM_CHARACTERISTIC, "Setting Alarm Characteristic");
        attributes.put(SETTING_CAMERA_CHARACTERISTIC, "Setting Camera Characteristic");
    }
    
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

 