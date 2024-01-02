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
import static de.rampro.activitydiary.helpers.BindCondition.Reference.RANGE;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_Bluetooth;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_GPS;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_WIFI;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.EXIST_ACTIVITY;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.EXIST_CONDITION;
import static de.rampro.activitydiary.model.conditions.Condition.mOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.rampro.activitydiary.db.LocalDBHelper;
import de.rampro.activitydiary.model.DiaryActivity;
import de.rampro.activitydiary.model.conditions.Condition;
import de.rampro.activitydiary.ui.generic.EditActivity;
import de.rampro.activitydiary.helpers.ConditionInfo;
import de.rampro.activitydiary.ui.main.MainActivity;

/**
 * 进行condition的绑定*/
public class BindCondition{
    public static class Reference{
        public static final int REQUEST_CODE = 100001;

        public static final int Condition_WIFI = 1;
        public static final int Condition_Bluetooth = 2;
        public static final int Condition_GPS = 3;
        public static final String EXIST_CONDITION = "0";
        public static final String EXIST_ACTIVITY = "1";

        public static String CurrentWIFI = "";
        public static String CurrentBluetooth = "";

        public static int RANGE = 2;  // 纬度/2，经度/4，边长50m
    }
    public static String bindInfo="";
    public static Map<String,String> delInfo= null;
    public static Map<String,String> checkExist(String name,String infos,String Type){
        ConditionQHelper helper = new ConditionQHelper();
        int act_id = helper.getID(name);
        String bind_id;
        String Act_id = (new Integer(act_id)).toString();
        Map<String,String> map = new HashMap<>();
        Cursor cursor;
        if(act_id >= 0){//该activity是否已绑定condition
            cursor = mOpenHelper.getReadableDatabase().query("activity_connection",new String[]{"info","connection_type","_deleted","act_id"},"act_id =?",new String[]{Act_id},null,null,null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    String info = cursor.getString(cursor.getColumnIndexOrThrow("info"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("connection_type"));
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("act_id"));
                    map.put("info",info);map.put("type",type);map.put("id",id);map.put("exist",EXIST_CONDITION);
                    cursor.close();
                    return map;
                }
                cursor.close();
            }
        }
        //该condition是否已被别的activity绑定
        cursor = mOpenHelper.getReadableDatabase().query("activity_connection",new String[]{"act_id"},"info =? And connection_type =?",new String[]{infos,Type},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                bind_id = cursor.getString(cursor.getColumnIndexOrThrow("act_id"));
                cursor.close();
                cursor = mOpenHelper.getReadableDatabase().query("activity",new String[]{"name","_deleted"},"_id =?",new String[]{bind_id},null,null,null);
                if(cursor != null){
                    if(cursor.moveToFirst()){
                        if(cursor.getInt(cursor.getColumnIndexOrThrow("_deleted")) == 0){
                            String act_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                            map.put("info",infos);map.put("type",Type);map.put("id",bind_id);map.put("exist",EXIST_ACTIVITY);map.put("name",act_name);
                            cursor.close();
                            return map;
                        }
                    }
                    cursor.close();
                }
            }
            else cursor.close();
        }

        return map;
    }
    public static Map<String,String> checkExist(String name){
        return checkExist(name,"","0");
    }
    public static int Bind(int type,String name,Context context) throws InterruptedException {
        switch(type){
            case Condition_WIFI:
                return BindWIFI(name,context);
            case Reference.Condition_Bluetooth:
                return BindBluetooth(name,context);
            case Reference.Condition_GPS:
                return BindGPS(name,context);
        }
        return 0;
    }
    public static void finishBind(int type,String name,Context context){
        String info = bindInfo;
        ConditionQHelper helper = new ConditionQHelper(context);
        int act_id = helper.getID(name);
        if(!info.equals("")){
            helper.cHelper("INSERT",info,type,act_id);
            DiaryActivity newAct =  helper.getActivity(act_id);
            newAct.setRunning(true);
            MainActivity.addRunActivities(newAct);
            MainActivity.refreshList();
            switch (type){
                case Condition_WIFI:
                    Toast.makeText(context, "成功绑定WIFI", Toast.LENGTH_LONG).show();break;
                case Condition_Bluetooth:
                    Toast.makeText(context, "成功绑定蓝牙", Toast.LENGTH_LONG).show();break;
                case Condition_GPS:
                    Toast.makeText(context, "成功绑定GPS", Toast.LENGTH_LONG).show();break;
            }
            bindInfo = "";
        }
        Log.d("finishBind","info: "+info);
        Log.d("finishBind","type: "+type);
        Log.d("finishBind","act_id: "+act_id);
    }
    public static void delBind(Context context){
        Map<String,String> exist = delInfo;
        ConditionQHelper helper= new ConditionQHelper();
        if(exist!= null&&!exist.isEmpty()){
            if(bindInfo.equals("")){
                Toast.makeText(context,"成功清除启动条件",Toast.LENGTH_LONG).show();
                MainActivity.removeActivityWithId(Integer.valueOf(exist.get("id")));
                MainActivity.refreshList();
            }
            helper.cHelper("DELETE",exist.get("info"),Integer.valueOf(exist.get("type")),Integer.valueOf(exist.get("id")));
            delInfo= null;
        }
    }
    /**
     * @param name 当前activity的名字
     * @param context
     * @return 如果进行覆盖，返回String[]{type,info},否则返回String[]{}*/
    private static int BindWIFI(String name,Context context) throws InterruptedException {
        String ssid = ConditionInfo.WIFI.getSSID(context);
        String bssid = ConditionInfo.WIFI.getBSSID(context);
        if(!bssid.equals("")){
            String info = ssid + "|" +bssid;
            Map<String,String> exist = checkExist(name,info,""+Condition_WIFI);
            PopupWindows pop = new PopupWindows(context);
            ConditionQHelper helper = new ConditionQHelper();
            if(exist.isEmpty()){
                pop.confirmConnection(Condition_WIFI,info);
            }
            else if(exist.get("exist").equals(EXIST_CONDITION)){
                pop.confirmOwConnection(Integer.valueOf(exist.get("type")) ,exist,info);
                Log.d("EXIST_CONDITION","info: "+exist.get("info"));
                Log.d("EXIST_CONDITION","id: "+exist.get("id"));
                Log.d("EXIST_CONDITION","type: "+exist.get("type"));
            }
            else{
                pop.confirmOwActivity(Integer.valueOf(exist.get("type")),exist,info,exist.get("name"));
                Log.d("EXIST_ACTIVITY","info: "+exist.get("info"));
                Log.d("EXIST_ACTIVITY","id: "+exist.get("id"));
                Log.d("EXIST_ACTIVITY","type: "+exist.get("type"));
                Log.d("EXIST_ACTIVITY","name: "+exist.get("name"));
            }
            return 1;
        }
        else {
            Toast.makeText(context,"请先连接WIFI",Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    private static int BindBluetooth(String name,Context context) throws InterruptedException {
        ArrayList<String> Binfos = ConditionInfo.Bluetooth.getInfos(context);
        ArrayList<String> infos = new ArrayList<String>();
        for(int i=0;i<Binfos.size();i=i+2){
            infos.add(Binfos.get(i)+"|"+Binfos.get(i+1));
        }
        PopupWindows pop = new PopupWindows(context);
        String info = "";
        if(!infos.isEmpty()) {
            info =infos.get(0);
            Map<String,String> exist = checkExist(name,info,""+Condition_Bluetooth);
            ConditionQHelper helper = new ConditionQHelper();
            if(exist.isEmpty()){
                pop.confirmConnection(Condition_Bluetooth,info);
            }
            else if(exist.get("exist").equals(EXIST_CONDITION)){
                pop.confirmOwConnection(Integer.valueOf(exist.get("type")) ,exist,info);
            }
            else{
                pop.confirmOwActivity(Integer.valueOf(exist.get("type")),exist,info,exist.get("name"));
            }
            return 1;
        }
        else {
            Toast.makeText(context,"请连接蓝牙设备",Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    private static int BindGPS(String name,Context context) throws InterruptedException {
        if(LocationHelper.helper.locationManager.isLocationEnabled()){
            Toast.makeText(context, "请先开启定位服务", Toast.LENGTH_LONG).show();
            return 0;
        }
        else{
            ArrayList<String> infos = ConditionInfo.GPS.getInfos(context);
            // ConditionQHelper helper = new ConditionQHelper(context);
            // int act_id = helper.getID(name);
            PopupWindows pop = new PopupWindows(context);

            // 策略是保留四位小数，然后投影除以RANGE
            int latInt = (int)(Double.parseDouble(infos.get(0)) * 10000) / RANGE;
            int lonInt = (int)(Double.parseDouble(infos.get(1)) * 10000) / RANGE / 2;

            String info = latInt + "|" + lonInt;
            // Log.d("GPS info", info);

            Map<String, String> exist = checkExist(name, info, ""+Condition_GPS);

            if(exist.isEmpty()){
                pop.confirmConnection(Condition_GPS, info);
            }
            else if(exist.get("exist").equals(EXIST_CONDITION)){
                pop.confirmOwConnection(Integer.parseInt(exist.get("type")), exist, info);
                Log.d("EXIST_CONDITION","info: "+exist.get("info"));
                Log.d("EXIST_CONDITION","id: "+exist.get("id"));
                Log.d("EXIST_CONDITION","type: "+exist.get("type"));
                /*弹窗*/
            }
            else{
                 pop.confirmOwActivity(Condition_GPS, exist, info, exist.get("name"));
                Log.d("EXIST_ACTIVITY","info: "+exist.get("info"));
                Log.d("EXIST_ACTIVITY","id: "+exist.get("id"));
                Log.d("EXIST_ACTIVITY","type: "+exist.get("type"));
                Log.d("EXIST_ACTIVITY","name: "+exist.get("name"));
                /*弹窗*/
             }

        // Log.d("Latitude", infos.get(0));  // 纬度
        // Log.d("Longitude", infos.get(1));  // 经度
        // Log.d("Altitude", infos.get(2));
        // Toast.makeText(context, "test 3", Toast.LENGTH_LONG).show();
        // if(helper.checkCondition(act_id)){
        //     helper.cHelper("INSERT", info, Condition_GPS, act_id);
        //     int res = helper.cHelper("QUERY", info, Condition_GPS);
        //     Log.d("QUERY_GPS", ""+res);
        //     Toast.makeText(context, "成功绑定GPS", Toast.LENGTH_LONG).show();
        // }

            return 1;
        }

    }

    /**
     * Unbind the activity if it has been bound to a connection.
     */
    public static void Unbind(int act_id, Context context){
        LocalDBHelper mLocalDBHelper = new LocalDBHelper(context);
        String sql = "UPDATE " + "activity_connection" +
                " SET " + "_deleted = 1" +
                " WHERE " + "act_id = " + act_id;
        mLocalDBHelper.getWritableDatabase().execSQL(sql);
    }

}
