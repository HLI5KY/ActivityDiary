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
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_GPS;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_WIFI;
import static de.rampro.activitydiary.model.conditions.Condition.mOpenHelper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 管理涉及condition的数据库操作*/
public class ConditionQHelper {
    int act_id = 0;
    Context context;
    public ConditionQHelper(int act_id,Context context){
        this.act_id = act_id;
        this.context = context;
    }
    public void cHelper(String operation,String info,int type){
        switch (operation){
            case "INSERT":
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                ContentValues insertValues = new ContentValues();
                insertValues.put("info",info);
                insertValues.put("act_id",this.act_id);
                insertValues.put("connection_type",type);
                insertValues.put("_deleted",0);
                db.insert("activity_connection",null,insertValues);
                break;
            case "UPDATE":
                break;
            case "QUERY":
                break;
            case "DELETE":
                break;
        }

    }

}
