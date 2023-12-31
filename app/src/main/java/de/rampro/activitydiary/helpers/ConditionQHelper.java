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

import de.rampro.activitydiary.model.DiaryActivity;

/**
 * 管理涉及condition的数据库操作*/
public class ConditionQHelper {
    Context context;
    public ConditionQHelper(Context context){
        this.context = context;
    }
    /**
     * 返回>=0成功*/
    public int cHelper(String operation,String info,int type,int act_id){
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String  Act_id =(new Integer(act_id)).toString();
        String Type = (new Integer(type)).toString();
        switch (operation){
            case "INSERT":
                values.clear();
                values.put("info",info);
                values.put("act_id",act_id);
                values.put("connection_type",type);
//                values.put("_deleted",1);
                db.insert("activity_connection",null,values);
                return 1;
            case "UPDATE":
                values.clear();
                values.put("info",info);
                values.put("connection_type",type);
                db.update("activity_connection",values,"act_id =?",new String[]{Act_id});
                return 1;
            case "QUERY":
                Cursor cursor = db.query("activity_connection",new String[]{"act_id","info"},"info =? AND connection_type =?",new String[]{info,Type},null,null,null);
                if(cursor != null){
                    if(cursor.moveToFirst()){
                        int res = cursor.getInt(cursor.getColumnIndexOrThrow("act_id"));
                        cursor.close();
                        return res;
                    }
                    cursor.close();
                    return -1;
                }
                return -1;
            case "DELETE":
                db.delete("activity_connection","act_id =? AND info =? AND connection_type =?",new String[]{Act_id,info,Type});
                return 1;
        }
        return -1;
    }
    public int cHelper(String operation,String info,int type){
        return cHelper(operation,info,type,-1);
    }
    /**
     * 如果activity已绑定过condition且未被删除，返回false*/
    public boolean checkCondition(int act_id){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String  Act_id =(new Integer(act_id)).toString();
        Cursor cursor = db.query("activity_connection",new String[]{"act_id","_deleted","info"},"act_id =?",new String[]{Act_id},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                if(cursor.getInt(cursor.getColumnIndexOrThrow("_deleted"))  == 0) {cursor.close();return false;}
            }
            cursor.close();
            return true;
        }
        return true;
    }
    public int getCloseID(int type){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String  Type =(new Integer(type)).toString();
        Cursor cursor = db.query("activity_connection",new String[]{"act_id","_deleted","connection_type"},"connection_type =?",new String[]{Type},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                if(cursor.getInt(cursor.getColumnIndexOrThrow("_deleted")) == 0) {
                    int act_id = cursor.getInt(cursor.getColumnIndexOrThrow("act_id"));
                    cursor.close();
                    return act_id;}
            }
            cursor.close();
            return -1;
        }
        return -1;
    }
    public int getCloseID(String info){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("activity_connection",new String[]{"act_id","_deleted","connection_type"},"info =?",new String[]{info},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                if(cursor.getInt(cursor.getColumnIndexOrThrow("_deleted")) == 0) {
                    int act_id = cursor.getInt(cursor.getColumnIndexOrThrow("act_id"));
                    cursor.close();
                    return act_id;}
            }
            cursor.close();
            return -1;
        }
        return -1;
    }

    public DiaryActivity setActivity(int act_id){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String Act_id = (new Integer(act_id)).toString();
        int type=-1; String name = ""; int color = 0;
        Cursor cursor = db.query("activity_connection",new String[]{"act_id","_deleted","connection_type"},"act_id =?",new String[]{Act_id},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                if(cursor.getInt(cursor.getColumnIndexOrThrow("_deleted")) == 0) {
                    type = cursor.getInt(cursor.getColumnIndexOrThrow("connection_type"));
                    }
            }
        }
        cursor = db.query("activity",new String[]{"_id","name","color"},"_id =?",new String[]{Act_id},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                color = cursor.getInt(cursor.getColumnIndexOrThrow("color"));
            }
        }
        cursor.close();
        return new DiaryActivity(act_id,name,color,type);
    }

    public int getID(String name){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("activity",new String[]{"_id"},"name =?",new String[]{name},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                cursor.close();
                return id;
            }
            cursor.close();
        }
        return -1;
    }
    public int getDel(String name){
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("activity",new String[]{"_deleted"},"name =?",new String[]{name},null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                int del = cursor.getInt(cursor.getColumnIndexOrThrow("_deleted"));
                cursor.close();
                return del;
            }
            cursor.close();
        }
        return -1;
    }
}


