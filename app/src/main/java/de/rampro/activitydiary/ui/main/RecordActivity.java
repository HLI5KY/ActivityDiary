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

package de.rampro.activitydiary.ui.main;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.rampro.activitydiary.ActivityDiaryApplication;
import de.rampro.activitydiary.R;
import de.rampro.activitydiary.db.ActivityDiaryContract;
import de.rampro.activitydiary.helpers.ActivityHelper;
import de.rampro.activitydiary.helpers.DateHelper;
import de.rampro.activitydiary.helpers.TimeSpanFormatter;
import de.rampro.activitydiary.model.DetailViewModel;
import de.rampro.activitydiary.model.DiaryActivity;
import de.rampro.activitydiary.ui.generic.BaseActivity;
import de.rampro.activitydiary.ui.main.DetailNoteFragment;
import de.rampro.activitydiary.ui.main.DetailPictureFragement;
import de.rampro.activitydiary.ui.main.DetailStatFragement;
import de.rampro.activitydiary.ui.main.MainActivity;
import de.rampro.activitydiary.ui.settings.SettingsActivity;

public class RecordActivity extends BaseActivity {
    private DiaryActivity currentActivity;
    private static DetailViewModel viewModel;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static final int QUERY_CURRENT_ACTIVITY_STATS = 1;
    private static final int QUERY_CURRENT_ACTIVITY_TOTAL = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_record, null, false);
        setContent(contentView);

        Intent i = getIntent();
//        viewModel = (DetailViewModel) i.getParcelableExtra("activityViewModel");
        int actId = i.getIntExtra("activityID", -1);
        int actName = i.getIntExtra("activityName", -1);
        int actColor = i.getIntExtra("activityColor", -1);
        if (actId == -1) {
            currentActivity = null;
        } else {
            currentActivity = ActivityHelper.helper.activityWithId(actId);
        }

        viewModel = MainActivity.getViewModel();
        Log.d("viewModel", "get viewModel from MainActivity: " + viewModel.mStartOfLast.getValue() + " " + viewModel.mTotalWeek.getValue());

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailStatFragement(), getResources().getString(R.string.fragment_detail_stats_title));
        adapter.addFragment(new DetailNoteFragment(), getResources().getString(R.string.fragment_detail_note_title));
        adapter.addFragment(new DetailPictureFragement(), getResources().getString(R.string.fragment_detail_pictures_title));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private RecordActivity.MainAsyncQueryHandler mQHandler = new RecordActivity.MainAsyncQueryHandler(ActivityDiaryApplication.getAppContext().getContentResolver());

    private class MainAsyncQueryHandler extends AsyncQueryHandler {
        public MainAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
            super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            if ((cursor != null) && cursor.moveToFirst()) {
                if (token == QUERY_CURRENT_ACTIVITY_TOTAL) {
                    RecordActivity.StatParam p = (RecordActivity.StatParam) cookie;
                    long total = cursor.getLong(cursor.getColumnIndexOrThrow(ActivityDiaryContract.DiaryStats.DURATION));

                    String x = DateHelper.dateFormat(p.field).format(p.end);
                    x = x + ": " + TimeSpanFormatter.format(total);
                    switch (p.field) {
                        case Calendar.DAY_OF_YEAR:
                            viewModel.mTotalToday.setValue(x);
                            break;
                        case Calendar.WEEK_OF_YEAR:
                            viewModel.mTotalWeek.setValue(x);
                            break;
                        case Calendar.MONTH:
                            viewModel.mTotalMonth.setValue(x);
                            break;
                    }
                }
            }
        }
    }

    public void queryAllTotals() {
        // TODO: move this into the DetailStatFragement
        DiaryActivity a = viewModel.mCurrentActivity.getValue();
        if (a != null) {
            int id = a.getId();

            long end = System.currentTimeMillis();
            queryTotal(Calendar.DAY_OF_YEAR, end, id);
            queryTotal(Calendar.WEEK_OF_YEAR, end, id);
            queryTotal(Calendar.MONTH, end, id);
        }
    }

    private void queryTotal(int field, long end, int actID) {
        Calendar calStart = DateHelper.startOf(field, end);
        long start = calStart.getTimeInMillis();
        Uri u = ActivityDiaryContract.DiaryStats.CONTENT_URI;
        u = Uri.withAppendedPath(u, Long.toString(start));
        u = Uri.withAppendedPath(u, Long.toString(end));

        mQHandler.startQuery(QUERY_CURRENT_ACTIVITY_TOTAL, new RecordActivity.StatParam(field, end),
                u,
                new String[]{
                        ActivityDiaryContract.DiaryStats.DURATION
                },
                ActivityDiaryContract.DiaryActivity.TABLE_NAME + "." + ActivityDiaryContract.DiaryActivity._ID
                        + " = ?",
                new String[]{
                        Integer.toString(actID)
                },
                null);
    }

    private class StatParam {
        public int field;
        public long end;

        public StatParam(int field, long end) {
            this.field = field;
            this.end = end;
        }
    }

    public static DetailViewModel getViewModel(){return viewModel;}
}
