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

package de.rampro.activitydiary.model.conditions;

import de.rampro.activitydiary.model.DiaryActivity;

public class ViewModel {
    private String mNote;
    private String mDuration;
    private String mAvgDuration;
    private String mStartOfLast;
    private String mTotalToday;
    private String mTotalWeek;
    private String mTotalMonth;
    private DiaryActivity mCurrentActivity;
    private long mDiaryEntryId;
    public ViewModel(){}
    public String getNote(){return mNote;}
    public void setNote(String note){mNote=note;}
    public String getDuration(){return mDuration;}
    public void setDuration(String duration){mDuration=duration;}
    public String getAvgDuration(){return mAvgDuration;}
    public void setAvgDuration(String duration){mAvgDuration=duration;}
    public String getStartOfLast(){return mStartOfLast;}
    public void setStartOfLast(String time){mStartOfLast=time;}
    public String getToday(){return mTotalToday;}
    public void setToday(String date){mTotalToday=date;}
    public String getWeek(){return mTotalWeek;}
    public void setWeek(String week){mTotalWeek=week;}
    public String getMonth(){return mTotalMonth;}
    public void setMonth(String month){mTotalMonth=month;}
    public DiaryActivity getCurrentActivity(){return mCurrentActivity;}
    public void setCurrentActivity(DiaryActivity act){mCurrentActivity=act;}
}
