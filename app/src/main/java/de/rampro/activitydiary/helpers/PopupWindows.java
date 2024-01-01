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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.rampro.activitydiary.R;

/**
 * 用于生成两种弹窗
 * <p>+ 确认覆盖 Condition 的弹窗
 * <p>+ 显示多条可选信息的弹窗
 */
public class PopupWindows extends DialogFragment {

    private int type;
    private boolean mulInfo;  // 是否为多info
    private int res = -1;  // “覆盖”弹窗的返回值
    private int index = -2;  // 选择弹窗的返回值的索引
    private Context context;  // 填充用
    private AlertDialog alertDialog;  // 弹窗本体

    /**
     * 确认覆盖 Condition 的弹窗，只包含一条信息和确认/取消(OK/Cancel)按钮
     * @param type
     * @param info 弹窗的内容
     * @return 1代表点击确认，0代表点击取消，-1异常
     */
    public int confirmOwConnection (int type, String info){
        res = -1;
        String infoShow = info;  // 弹窗正文，可以根据需要再修改
        String title = "Condition Overwrite Confirm";

        alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(infoShow)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {  // OK按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        res = 1;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  // Cancel按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        res = 0;
                    }
                })
                .create();;

        Log.d("Popup1", "res=" + res);
        alertDialog.show();
        return res;
    }

    /**
     * 从多个选项中选择一个的弹窗
     * @param type
     * @param info 包含多个选项
     * @return String类型，info 中的一个如果选择确认；或null如果选择取消；或ERROR如果异常
     */
    public String chooseFromInfo(int type, String[] info){
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

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  // Cancel按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        index = -1;
                    }
                })
                .create();
        alertDialog.show();

        String res2;

        if(index >= 0)
            res2 =  info[index];
        else if(index == -1)
            res2 =  null;
        else
            res2 =  "ERROR";

        return res2;
    }

}
