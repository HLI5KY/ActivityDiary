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

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

import de.rampro.activitydiary.db.ActivityDiaryContract;
import de.rampro.activitydiary.model.DiaryActivity;

public class ConditionQHandler extends AsyncQueryHandler {
    public ConditionQHandler(ContentResolver cr) {
        super(cr);
    }
    protected void onQueryComplete(int token, Object cookie,
                                   Cursor cursor) {
        if ((cursor != null)) {
            switch (token) {
                case Condition_WIFI:
                    break;
                case Condition_Bluetooth:
                    break;
                case Condition_GPS:
                    break;
            }
            cursor.close();
        }
    }
}
