/*
 * ActivityDiary
 *
 * Copyright (C) 2017 Raphael Mack http://www.raphael-mack.de
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

package de.rampro.activitydiary.model;

public class DiaryActivity implements Cloneable{
    private String mName;
    private int mId;
    private int mColor;
    private int mConnection;
    private boolean running;
    public DiaryActivity(int id, String name, int color, int connection){
        mId = id;
        mName = name;
        mColor = color;
        mConnection = connection;
        running = false;
    }

    public String getName(){
        return mName;
    }
    public void setName(String name){ mName = name;}
    public int getColor(){
        return mColor;
    }
    public void setColor(int color){ mColor = color;}
    public int getId() { return mId;}
    public void setId(int id) { mId = id;}
    public int getConnection(){
        return mConnection;
    }
    public void setConnection(int connection){ mConnection = connection;}
    public boolean getRunning(){return running;}
    public void setRunning(boolean  b){running=b;}

    public boolean equals(Object other){
        return other != null && other instanceof DiaryActivity && ((DiaryActivity)other).getName().equals(mName);
    }

    @Override
    public String toString(){
        return mName + " (" + mId + ")";
    }

    @Override
    public int hashCode(){
        return mId;
    }

    @Override
    public Object clone(){
        DiaryActivity act =null;
        try{
            act = (DiaryActivity) super.clone();
        }catch (Exception e){
            e.printStackTrace();
        }
        return act;
    }
}
