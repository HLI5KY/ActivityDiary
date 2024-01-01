/*
 * ActivityDiary
 *
 * Copyright (C) 2024 Raphael Mack http://www.raphael-mack.de
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

import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_GPS;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_WIFI;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

/**
* bind时使用的弹窗，有单条目型和多条目型*/
public class BindPopup {
    public static void confirmCon(Context context,int type,String info,int act_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("存在一个与该活动绑定的启动条件");
        String[] detailInfo = ConditionInfo.resolveInfo(info);
        switch (type){
            case Condition_WIFI:
                builder.setMessage("类型： WIFI\n" +
                        "名称： " + detailInfo[0] +"\n"+
                        "地址： " + detailInfo[1]);
                break;
            case Condition_GPS:
                break;
        }
        builder.setPositiveButton("覆盖", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                BindCondition.Unbind(act_id,context);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }
    public static void single(int type,String info,Context context){}
    public static void multiple(int type,String[] infos,Context context){}
}
