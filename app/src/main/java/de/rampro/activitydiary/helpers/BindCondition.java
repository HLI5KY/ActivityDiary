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
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.rampro.activitydiary.ui.generic.EditActivity;
import de.rampro.activitydiary.helpers.ConditionInfo;

public class BindCondition{
    public class Reference{
        public static final int REQUEST_CODE = 100001;

        public static final int Condition_WIFI = 1;
        public static final int Condition_Bluetooth = 2;
        public static final int Condition_GPS = 3;
    }
    public static int Bind(int type,int activity,Context context){
        switch(type){
            case Reference.Condition_WIFI:
                return BindWIFI(activity,context);
            case Reference.Condition_Bluetooth:
                return BindBluetooth(activity,context);
            case Reference.Condition_GPS:
                return BindGPS(activity,context);
        }
        return 0;
    }
    private static int BindWIFI(int activity,Context context){
            String ssid = ConditionInfo.WIFI.getSSID(context);
            String bssid = ConditionInfo.WIFI.getBSSID(context);
            Log.d("SSID",ssid);
            Log.d("BSSID",bssid);
            Toast.makeText(context, "test 1", Toast.LENGTH_LONG).show();
            return 1;

    }
    private static int BindBluetooth(int activity,Context context){
        ArrayList<String> infos = ConditionInfo.Bluetooth.getInfos(context);
        for(int i=0;i<infos.size();i=i+2){
            Log.d("Name",infos.get(i));
            Log.d("Mac",infos.get(i+1));
        }
        Toast.makeText(context, "test 2", Toast.LENGTH_LONG).show();
        return 1;
    }
    private static int BindGPS(int activity,Context context){
        Toast.makeText(context, "test 3", Toast.LENGTH_LONG).show();
        return 1;
    }

}
