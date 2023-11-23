/*
 * ActivityDiary
 *
 * Copyright (C) 2023 Raphael Mack http://www.raphael-mack.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.rampro.activitydiary.helpers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapKt;

import java.util.ArrayList;
import java.util.Set;

import de.rampro.activitydiary.helpers.BindCondition.Reference;

public class ConditionInfo{
    /*创建activity时检查GPS/蓝牙/WIFI是否开启*/
    public static boolean conditionCheck(Context context,int type){
        switch (type){
            case Reference.Condition_WIFI:
                return WIFI.isWIFIenabled(context);
            case Reference.Condition_Bluetooth:
                return Bluetooth.isBluetoothEnabled(context);
            case Reference.Condition_GPS:
                return GPS.isGPSenabled(context);
        }
        return false;
    }
    public static class WIFI extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if(wm.isWifiEnabled()){
                Log.d("WIFI_info","WIFI已开启");
                WifiInfo info = wm.getConnectionInfo();
                String ssid = info.getSSID();
                String bssid = info.getBSSID();
                if(bssid != null){//wifi连接切换
                    Log.d("WIFI_info","wifi已修改");
                    /*
                    检查当前activity是否与该wifi匹配
                    检查是否有activity与该wifi绑定
                    有->启动
                    无->return*/
                }
                return;
            }
            Log.d("WIFI_info","连接已关闭");
            //wifi关闭
            /*检查当前activity是否与wifi绑定
            有->检查activity是被动启动还是主动启动
                被动->关闭activity
                主动->return
            无->return*/

        }
        public static void changeReceiver(Context context,Intent intent){
            ConditionInfo.WIFI WIFIreceiver = new ConditionInfo.WIFI();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            context.registerReceiver(WIFIreceiver,filter);
            WIFIreceiver.onReceive(context,intent);
        }
        public static boolean isWIFIenabled(Context context){
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wm.isWifiEnabled();
        }
        public static String getSSID(Context context){
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if(info != null){
                String ssid = info.getSSID();
                if(ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"'){
                    return ssid.substring(1,ssid.length()-1);
                }
            }
            return "";
        }
        public static String getBSSID(Context context){
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if(info != null){
                String bssid = info.getBSSID();
                return bssid;
            }
            return "";
        }
    }
    public static class Bluetooth extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){

        }

        public static boolean isBluetoothEnabled(Context context){
            BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter ba = (BluetoothAdapter) bm.getAdapter();
            return (ba != null && ba.isEnabled());
        }
        public static ArrayList<String> getInfos(Context context){
            ArrayList<String> infos = new ArrayList<String>();
            BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            Set<BluetoothDevice> deviceList = bm.getAdapter().getBondedDevices();
            for(BluetoothDevice device : deviceList){
                boolean isConnect = false;
                try {
                    //获取当前连接的蓝牙信息
                    isConnect = (boolean) device.getClass().getMethod("isConnected").invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isConnect) {
                    String name = device.getName();
                    String Mac = device.getAddress();
                    if(name.length()>0) infos.add(name);
                    else infos.add("");
                    if(Mac.length()>0) infos.add(Mac);
                    else infos.add("");
                }

                }
            return infos;
        }

    }
    public static class GPS extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){

        }
        public static boolean isGPSenabled(Context context){
            return false;
        }
    }

}
