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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ConditionInfo {
    public static class WIFI{
        public static String getSSID(Context context){
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if(wm != null){
                WifiInfo info = wm.getConnectionInfo();
                String ssid = info.getSSID();
                if(ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"'){
                    return ssid.substring(1,ssid.length()-1);
                }
            }
            return "";
        }
        public static String getBSSID(Context context){
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if(wm != null){
                WifiInfo info = wm.getConnectionInfo();
                String bssid = info.getBSSID();
                return bssid;
            }
            return "";
        }
    }
    public class Bluetooth{

    }
    public class GPS{

    }

}
