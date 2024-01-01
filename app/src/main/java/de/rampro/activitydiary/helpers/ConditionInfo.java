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

import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_Bluetooth;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_WIFI;
import static de.rampro.activitydiary.model.conditions.Condition.mOpenHelper;

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
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.rampro.activitydiary.ActivityDiaryApplication;
import de.rampro.activitydiary.db.ActivityDiaryContract;
import de.rampro.activitydiary.helpers.BindCondition.Reference;
import de.rampro.activitydiary.model.DiaryActivity;

/**
 * 管理condition的改变*/
public class ConditionInfo{
    /*创建activity时检查GPS/蓝牙/WIFI是否开启*/
    public static boolean conditionCheck(Context context,int type){
        switch (type){
            case Condition_WIFI:
                return WIFI.isWIFIenabled(context);
            case Reference.Condition_Bluetooth:
                return Bluetooth.isBluetoothEnabled(context);
            case Reference.Condition_GPS:
                return GPS.isGPSEnabled(context);
        }
        return false;
    }
    public static String[] resolveInfo(String info){
        String[] res = info.split("\\|");
        return  res;
    }
    public static class WIFI extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(action != null){
                ConditionQHelper helper = new ConditionQHelper(context);
                switch (action){
                    case WifiManager.WIFI_STATE_CHANGED_ACTION:
                        int Wstate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0);
                        switch (Wstate){
                            case WifiManager.WIFI_STATE_ENABLED:
                                Log.d("WIFI_info","WIFI已开启");
                                break;
                            case WifiManager.WIFI_STATE_DISABLED:
                                Reference.CurrentWIFI = "";
                                int close_id = helper.getCloseID(Condition_WIFI);
                                Log.d("WIFI_PARA","close_id: "+close_id);
                                if(close_id >= 0) {
                                    ActivityHelper.helper.setCurrentActivity(null);
                                    /*关闭activity*/}
                                Log.d("WIFI_info","WIFI已关闭");
                                break;
                        }
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                        String bssid = ConditionInfo.WIFI.getBSSID(context);
                        if(!Reference.CurrentWIFI.equals(bssid) && !bssid.equals("") && !bssid.equals("00:00:00:00:00:00")){
                            Reference.CurrentWIFI = bssid;
                            String info = getSSID(context) + "|" + getBSSID(context);
                            DiaryActivity current = ActivityHelper.helper.getCurrentActivity();
                            if(current != null){
                                int close_id = current.getId();
                                Log.d("WIFI_PARA","close_id: "+close_id);
                                if(close_id >= 0) {
                                    ActivityHelper.helper.setCurrentActivity(null);
                                    /*关闭activity*/}
                            }
                            int start_id = helper.cHelper("QUERY",info,Condition_WIFI);
                            if(start_id >= 0) {
                                DiaryActivity newAct = helper.getActivity(start_id);
                                int del = helper.getDel(newAct.getName());
                                Log.d("WIFI_PARA","del: "+del);
                                Log.d("WIFI_PARA","start_id: "+start_id);
                                Log.d("WIFI_PARA","name: "+newAct.getName());
                                Log.d("WIFI_PARA","color: "+newAct.getColor());
                                Log.d("WIFI_PARA","_id: "+newAct.getId());
                                Log.d("WIFI_PARA","type: "+newAct.getConnection());
                                ActivityHelper.helper.setCurrentActivity(newAct);
                            }

                            Log.d("WIFI_info","WIFI已修改"+Reference.CurrentWIFI);
                        }
                        break;
                }
            }
        }

        public static void changeReceiver(Context context,Intent intent){
            ConditionInfo.WIFI WIFIreceiver = new ConditionInfo.WIFI();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            context.registerReceiver(WIFIreceiver,filter);
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
                if(bssid != null) return bssid;
            }
            return "";
        }
    }

    public static class Bluetooth extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(action != null){
                ConditionQHelper helper = new ConditionQHelper(context);
                switch (action) {
                    case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                        int Astate = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,0);
                        switch (Astate){
                            case BluetoothAdapter.STATE_CONNECTED:
                                Log.d("Bluetooth_info","蓝牙已连接");
                                ArrayList<String> Binfos = ConditionInfo.Bluetooth.getInfos(context);
                                ArrayList<String> infos = new ArrayList<String>();
                                for(int i=0;i<Binfos.size();i=i+2){
                                    infos.add(Binfos.get(i)+"|"+Binfos.get(i+1));
                                }
                                for (String info : infos){
                                    int start_id = helper.cHelper("QUERY",info,Condition_Bluetooth);
                                    if(start_id >= 0) {/*启动activity*/}
                                }
                                /*check 蓝牙是否有绑定activity
                                    有->启动
                                    无->return*/
                                break;
                            case BluetoothAdapter.STATE_DISCONNECTED:
                                Log.d("Bluetooth_info","蓝牙已断开");
                                int close_id = helper.getCloseID(Condition_Bluetooth);
                                if(close_id >= 0) {/*关闭activity*/}
                            /*check activity是否绑定蓝牙
                                    有->关闭
                                    无->return*/
                                break;
                        }
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int Cstate = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                        switch (Cstate){
                            case BluetoothAdapter.STATE_ON:
                                Log.d("Bluetooth","蓝牙已开启");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                Log.d("Bluetooth_info","蓝牙已关闭");
                                int close_id = helper.getCloseID(Condition_Bluetooth);
                                if(close_id >= 0) {/*关闭activity*/}
                                break;
                    }
                    break;
                }
            }
        }

        public static void changeReceiver(Context context,Intent intent){
            ConditionInfo.Bluetooth bluetoothReceiver = new ConditionInfo.Bluetooth();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            context.registerReceiver(bluetoothReceiver,filter);
            BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if(bm.getAdapter()!= null && bm.getAdapter().isEnabled()) Log.d("Bluetooth","蓝牙已开启test");
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
            // 该方法会在绑定一个gps后、手机设置开关 Location 服务时自动回调
            // Log.d("Location_Check", "onReceive可执行");
            String action = intent.getAction();
            switch (action) {
                case LocationManager.MODE_CHANGED_ACTION:
                    // if (intent.getBooleanExtra(LocationManager.EXTRA_LOCATION_ENABLED, false)){
                    if (LocationHelper.helper.locationManager.isLocationEnabled()){
                        Log.d("Location_Info", "定位可用");
                    }
                    else{
                        Log.d("Location_Info", "定位不可用");
                    }
                    break;

                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    // location providers 的状态一次只能检查一个，无法直接显示是否至少一个可用，
                    // 暂时没有比较好的实现，这一段很可能是冗余的。
                    String providerName = intent.getStringExtra(LocationManager.EXTRA_PROVIDER_NAME);
                    if (intent.getBooleanExtra(LocationManager.EXTRA_PROVIDER_ENABLED, true)){
                    // if (LocationHelper.helper.locationManager.isProviderEnabled(providerName)){
                    // 该方法会导致崩溃，原因未知
                        Log.d("LocationProvider_Info", "Provider: " + providerName + " 可用");
                    }
                    else{
                        Log.d("LocationProvider_Info", "Provider: " + providerName + " 不可用");
                    }
                    break;
            }
        }

        public static boolean isGPSEnabled(Context context){
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return lm.isLocationEnabled();  // need API 28
            }
            else {
                Log.d("Method: isGPSEnabled()", "API VERSION EXCEPTION");
                return false;
            }
        }

        public static void changeReceiver(Context context,Intent intent){
            ConditionInfo.GPS GPSReceiver = new ConditionInfo.GPS();
            IntentFilter filter = new IntentFilter();
            filter.addAction(LocationManager.MODE_CHANGED_ACTION);
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            context.registerReceiver(GPSReceiver, filter);
        }

        public static ArrayList<String> getInfos(Context context){
            ArrayList<String> infos = new ArrayList<>();
            // 直接调用了LocationHelper中的方法，得到的数据本身合法，应该无需检查
            Location location = LocationHelper.helper.getCurrentLocation();
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            // Log.d("DEBUG_Latitude", latitude);
            // 已经可以收到正确的定位信息
            infos.add(latitude);
            infos.add(longitude);

            /* 海拔
            if (location.hasAltitude()) {
                String altitude = String.valueOf(LocationHelper.helper.getCurrentLocation().getAltitude());
                infos.add(altitude);
            }
            */

            return infos;
        }
    }

}
