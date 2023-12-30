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
    /**
     * 返回>=0成功*/
    public String cHelper(String operation,String info,int type){
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String  Act_id =(new Integer(this.act_id)).toString();
        String Type = (new Integer(type)).toString();
        switch (operation){
            case "INSERT":
                values.clear();
                values.put("info",info);
                values.put("act_id",this.act_id);
                values.put("connection_type",type);
                values.put("_deleted",0);
                db.insert("activity_connection",null,values);
                return "1";
            case "UPDATE":
                values.clear();
                values.put("info",info);
                values.put("connection_type",type);
                db.update("activity_connection",values,"act_id =?",new String[]{Act_id});
                return "1";
            case "QUERY":
                Cursor cursor = db.query("activity_connection",new String[]{"act_id","info"},"info =? AND connection_type =?",new String[]{info,Type},null,null,null);
                if(cursor != null){
                    if(cursor.moveToFirst()){
                        String res = cursor.getString(cursor.getColumnIndexOrThrow("info"));
                        cursor.close();
                        return res;
                    }
                    cursor.close();
                    return "1";
                }
                return "1";
            case "DELETE":
                db.delete("activity_connection","act_id =? AND info =? AND connection_type =?",new String[]{Act_id,info,Type});
                return "1";
        }
        return "-1";

    }
    /**
     * 如果activity已绑定过condition且未被删除，返回false*/
    public boolean checkCondition(String info,int type){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String  Act_id =(new Integer(act_id)).toString();
        Cursor cursor = db.query("activity_connection",new String[]{"act_id","_deleted","info"},"act_id =?",new String[]{Act_id},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                if(cursor.getColumnIndexOrThrow("_deleted") == 0) {cursor.close();return false;}
            }
            cursor.close();
            return true;
        }
        return true;
    }

}
