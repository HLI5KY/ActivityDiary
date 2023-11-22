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
import android.widget.Toast;

import de.rampro.activitydiary.ui.generic.EditActivity;
import de.rampro.activitydiary.helpers.ConditionInfo;

public class BindCondition extends EditActivity{
    private static final int WIFI =  1;
    private static final int Bluetooth =  2;
    private static final int GPS =  3;
    public static int Bind(int type,int activity,Context context){
        switch(type){
            case WIFI:
                return BindWIFI(activity,context);
            case Bluetooth:
                return BindBluetooth(activity,context);
            case GPS:
                return BindGPS(activity,context);
        }
        return 0;
    }
    private static int BindWIFI(int activity,Context context){
        String ssid = ConditionInfo.WIFI.getSSID(context);
        String bssid = ConditionInfo.WIFI.getBSSID(context);
        System.out.println("SSID:"+ "  "+ssid+"\n");
        System.out.println("BSSID:"+ "  "+bssid+"\n");
        Toast.makeText(context, "test 1", Toast.LENGTH_LONG).show();
        return 1;
    }
    private static int BindBluetooth(int activity,Context context){
        Toast.makeText(context, "test 2", Toast.LENGTH_LONG).show();
        return 1;
    }
    private static int BindGPS(int activity,Context context){
        Toast.makeText(context, "test 3", Toast.LENGTH_LONG).show();
        return 1;
    }

}
