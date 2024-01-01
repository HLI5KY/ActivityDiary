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

import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_Bluetooth;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_GPS;
import static de.rampro.activitydiary.helpers.BindCondition.Reference.Condition_WIFI;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Map;
import java.util.concurrent.Future;

import de.rampro.activitydiary.R;

/**
 * 用于生成两种弹窗
 * <p>+ 确认覆盖 Condition 的弹窗
 * <p>+ 显示多条可选信息的弹窗
 */
public class PopupWindows {

    private int type;
    private boolean mulInfo;  // 是否为多info
    private int index = -2;  // 选择弹窗的返回值的索引
    private Context context;  // 填充用
    private AlertDialog alertDialog;  // 弹窗本体

    PopupWindows(Context _context){
        context = _context;
    }

    /**
     * 确认覆盖 Condition 的弹窗，只包含一条信息和确认/取消(OK/Cancel)按钮
     * @param type
     * @param info 弹窗的内容
     */

    public void confirmOwConnection (int type, Map<String,String> exist, String info) throws InterruptedException {
        String existInfo = exist.get("info");
        String[] detailInfo = ConditionInfo.resolveInfo(existInfo);
        String infoShow ="";   // 弹窗正文，可以根据需要再修改
        String title = "已存在启动条件";
        switch (type){
            case Condition_WIFI:
                infoShow = "类型: "+"WIFI"+"\n"+
                        "名称: " + detailInfo[0]+"\n"+
                        "地址: " + detailInfo[1];
                break;
            case Condition_Bluetooth:
                infoShow = "类型: "+"蓝牙"+"\n"+
                        "名称: " + detailInfo[0]+"\n"+
                        "地址: " + detailInfo[1];
                break;
            case Condition_GPS:
                infoShow = "类型: "+"GPS"+"\n"+
                        "经度: " + detailInfo[0]+"\n"+
                        "纬度: " + detailInfo[1];
                break;
        }
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(infoShow)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("覆盖", new DialogInterface.OnClickListener() {  // OK按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BindCondition.delInfo = exist;
                        BindCondition.bindInfo = info;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {  // Cancel按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BindCondition.bindInfo = "";
                    }
                })
                .create();
        alertDialog.show();
    }
    public void confirmOwActivity (int type, Map<String,String> exist,String info,String name) throws InterruptedException {
        String existInfo = exist.get("info");
        String[] detailInfo = ConditionInfo.resolveInfo(existInfo);
        String infoShow ="";   // 弹窗正文，可以根据需要再修改
        String title = "该启动条件已被绑定";
        switch (type){
            case Condition_WIFI:
                infoShow = "活动： " + name+"\n"+
                        "类型: "+"WIFI"+"\n"+
                        "名称: " + detailInfo[0]+"\n"+
                        "地址: " + detailInfo[1];
                break;
            case Condition_Bluetooth:
                infoShow = "活动： " + name+"\n"+
                        "类型: "+"蓝牙"+"\n"+
                        "名称: " + detailInfo[0]+"\n"+
                        "地址: " + detailInfo[1];
                break;
            case Condition_GPS:
                infoShow = "活动： " + name+"\n"+
                        "类型: "+"GPS"+"\n"+
                        "经度: " + detailInfo[0]+"\n"+
                        "纬度: " + detailInfo[1];
                break;
        }
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(infoShow)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("覆盖", new DialogInterface.OnClickListener() {  // OK按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BindCondition.delInfo = exist;
                        BindCondition.bindInfo = info;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {  // Cancel按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BindCondition.bindInfo ="";
                    }
                })
                .create();

        alertDialog.show();

    }

    /**
     * 从多个选项中选择一个的弹窗
     * @param type
     * @param info 包含多个选项
     */
    public void chooseFromInfo(int type, String[] info){
        index = -2;
        String[] infoShow = info;  // 同上，可以修改
        String title = "Please Choose One";

        alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.mipmap.ic_launcher)
                .setSingleChoiceItems(infoShow, 0, new DialogInterface.OnClickListener() {  // 添加单选框
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        index = i;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {  // OK按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (index == -2) {
                            index = -1;
                            Handle2(null);
                        }
                        else {
                            Handle2(info[index]);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  // Cancel按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        index = -1;
                        Handle2(null);
                    }
                })
                .create();
        alertDialog.show();

        // String res2;

        // if(index >= 0)
            // res2 =  info[index];
        // else if(index == -1)
            // res2 =  null;
        // else
            // res2 =  "ERROR";

        // Log.d("Popup2", "res2=" + res2);
        // return res2;
    }

    /**
     * 处理确认覆盖的方法（仅作样例）
     * @param res 1 点击 OK, 0 点击 Cancel
     */
    void Handle1(int res){
        Log.d("Handle1", res+"");
    }

    /**
     * 处理选择的方法（仅作样例）
     * @param res 选项
     */
    void Handle2(String res){
        Log.d("Handle2", res);
    }

}
