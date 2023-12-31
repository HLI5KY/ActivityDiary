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
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_WIFI;
import static de.rampro.activitydiary.model.conditions.Condition.mOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.rampro.activitydiary.db.LocalDBHelper;
import de.rampro.activitydiary.model.conditions.Condition;
import de.rampro.activitydiary.ui.generic.EditActivity;
import de.rampro.activitydiary.helpers.ConditionInfo;
/**
 * 进行condition的绑定*/
public class BindCondition{
    public static class Reference{
        public static final int REQUEST_CODE = 100001;

        public static final int Condition_WIFI = 1;
        public static final int Condition_Bluetooth = 2;
        public static final int Condition_GPS = 3;

        public static String CurrentWIFI = "";
        public static String CurrentBluetooth = "";
    }


    public static int Bind(int type,String name,Context context){
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

    private static int BindWIFI(String name,Context context){
        String ssid = ConditionInfo.WIFI.getSSID(context);
        String bssid = ConditionInfo.WIFI.getBSSID(context);
        String info = ssid + "|" +bssid;
        ConditionQHelper helper = new ConditionQHelper(context);
        int act_id = helper.getID(name);
//        /*test*/
//        String res;
//        helper.cHelper("INSERT",info,Condition_WIFI);
//        res = helper.cHelper("QUERY",info,Condition_WIFI);
//        Log.d("QUERY","info1: "+res);
//        helper.cHelper("UPDATE","test",Condition_WIFI);
//        res = helper.cHelper("QUERY","test",Condition_WIFI);
//        Log.d("QUERY","info2: "+res);
//        helper.cHelper("DELETE","test",Condition_WIFI);
//        res = helper.cHelper("QUERY","test",Condition_WIFI);
//        Log.d("QUERY","info3: "+res);
//        /*test*/
        if(helper.checkCondition(act_id)){//检查该activity是否已绑定一个condition
            /*show a window to confirm*/
            helper.cHelper("INSERT",info,Condition_WIFI,act_id);             //插入wifi数据
            Toast.makeText(context, "成功绑定WIFI", Toast.LENGTH_LONG).show();
        }
        return 1;
    }

    private static int BindBluetooth(String name,Context context){
        ArrayList<String> Binfos = ConditionInfo.Bluetooth.getInfos(context);
        ArrayList<String> infos = new ArrayList<String>();
        for(int i=0;i<Binfos.size();i=i+2){
            infos.add(Binfos.get(i)+"|"+Binfos.get(i+1));
        }
        Toast.makeText(context, "test 2", Toast.LENGTH_LONG).show();
        return 1;
    }

    private static int BindGPS(String name,Context context){
        ArrayList<String> infos = ConditionInfo.GPS.getInfos(context);
        Log.d("Latitude", infos.get(0));  // 纬度
        Log.d("Longitude", infos.get(1));  // 经度
        // Log.d("Altitude", infos.get(2));
        Toast.makeText(context, "test 3", Toast.LENGTH_LONG).show();
        return 1;
    }

    /**
     * Unbind the activity if it has been bound to a connection.
     */
    private static void Unbind(int activity, Context context){
        LocalDBHelper mLocalDBHelper = new LocalDBHelper(context);
        String sql = "UPDATE " + "activity_connection" +
                " SET " + "_deleted = 1" +
                " WHERE " + "act_id = " + activity;
        mLocalDBHelper.getWritableDatabase().execSQL(sql);
    }

}
