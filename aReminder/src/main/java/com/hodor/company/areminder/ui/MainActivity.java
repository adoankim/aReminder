/**
 aReminder - an Android + Google wear application test for I/O 2014

 Copyright (C) 2014  Toni Martinez / Adam Doan Kim

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package com.hodor.company.areminder.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hodor.company.areminder.R;
import com.hodor.company.areminder.model.CategoryModel;
import com.hodor.company.areminder.service.TimerService;
import com.hodor.company.areminder.timer.CountDown;
import com.hodor.company.areminder.util.ImagesUtil;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NOTIFICATION_ID = 13;

    public static final String ACTION_REMOVE_TIMER = "action_remove_timer";
    public static final String ACTION_SHOW_ALARM = "action_show_alarm";

    public static final String ALARM_TIME = "alarm_time";

    public MainActivity() {
    }

    public long getTime() {
        if (this.mTimePicker != null) {
            return mTimePicker.getValue() * ((unit == units.MINUTES) ? 1:60);//60 : 3600);
        }
        return 0;
    }

    public static enum units {HOURS, MINUTES}

    public static enum categories {FOOD, WORK, SPORT}

    private units unit = units.MINUTES;
    private categories category = categories.FOOD;

    private NumberPicker mTimePicker;
    private Switch mTineUnit;
    private ListView mCategory;

    private Button startButton;
    private Button stopButton;
    private ImageButton closeButton;

    private View mLayoutChronometer;
    private TextView mChronometer;
    private CountDown mCountDown;
    CategoryAdapter mCategoriesAdapter;

    private NotificationManagerCompat mNotificationManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!processRequest()) {
            setContentView(R.layout.activity_main);

            initLayout();
            setup();
            configureMaxValuePicker();
            initEvents();
        }
    }

    private boolean processRequest() {
        if (getIntent().getIntExtra(ACTION_REMOVE_TIMER, 0) > 0) {
            Log.d(TAG, "boorando");
            stopAlarm();
            finish();
            return true;
        }
        return false;
    }

    private NotificationManagerCompat getNotificationManager() {
        if (this.mNotificationManager == null) {
            this.mNotificationManager = NotificationManagerCompat.from(this);
        }
        return this.mNotificationManager;
    }


    private void initLayout() {
        this.mCategory = (ListView) findViewById(R.id.category);
        this.startButton = (Button) findViewById(R.id.startButtton);
        this.stopButton = (Button) findViewById(R.id.stopButtton);
        this.mTineUnit = (Switch) findViewById(R.id.time_unit);
        this.mTimePicker = (NumberPicker) findViewById(R.id.time);
        this.mLayoutChronometer = findViewById(R.id.layout_chronometer);
        this.mChronometer = (TextView) findViewById(R.id.chronometer);
        this.closeButton = (ImageButton) findViewById(R.id.close);
    }

    private void setup() {
        this.mTimePicker.setMinValue(1);
        this.mTimePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        initAdapter();
        this.mCategory.setAdapter(this.mCategoriesAdapter);

        this.mLayoutChronometer.setVisibility(View.GONE);
    }

    private void initAdapter() {
        ArrayList<CategoryModel> lCategories = new ArrayList<CategoryModel>();
        lCategories.add(new CategoryModel(getString(R.string.category_food), R.drawable.food));
        lCategories.add(new CategoryModel(getString(R.string.category_work), R.drawable.work));
        lCategories.add(new CategoryModel(getString(R.string.category_sport), R.drawable.sport));
        this.mCategoriesAdapter = new CategoryAdapter(this, lCategories);
    }

    private void configureMaxValuePicker() {
        mTimePicker.setMaxValue((unit == units.HOURS) ? 24 : 60);
    }

    private void initEvents() {
        this.mTineUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                unit = (isChecked) ? units.HOURS : units.MINUTES;
                configureMaxValuePicker();
            }
        });
        this.mCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = (mCategoriesAdapter.getItemId(position) == R.string.category_food) ?
                        categories.FOOD :
                        (mCategoriesAdapter.getItemId(position) == R.string.category_work) ?
                                categories.WORK :
                                categories.SPORT;

                for (int i = 0; i < parent.getChildCount(); i++) {
                    ((ImageView) parent.getChildAt(i).findViewById(R.id.picture)).setColorFilter(ImagesUtil.getGrayScaleFilter());
                    parent.getChildAt(i).setAlpha(0.4f);
                }
                ((ImageView) view.findViewById(R.id.picture)).clearColorFilter();
                view.setAlpha(1);
            }
        });
        this.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopChronometer();
                stopRemind(v);
                setEnableMainActivity(true);
            }
        });
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnableMainActivity(true);
            }
        });
    }

    private void setEnableMainActivity(boolean enable) {
        this.mCategory.setEnabled(enable);
        this.mTineUnit.setEnabled(enable);
        this.mTimePicker.setEnabled(enable);
        this.startButton.setEnabled(enable);
        if (enable) {
            this.mLayoutChronometer.setVisibility(View.GONE);
        } else {
            this.stopButton.setVisibility(View.VISIBLE);
            this.closeButton.setVisibility(View.GONE);
            this.closeButton.setEnabled(false);
            this.mLayoutChronometer.setVisibility(View.VISIBLE);
        }
    }

    private void startChronometer(long seconds) {
        mCountDown = new CountDown(seconds * 1000, 1000, mChronometer);
        mCountDown.setOnFinishEventListener(new CountDown.OnFinishEventListener() {
            @Override
            public void onFinish() {
                stopButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.VISIBLE);
                closeButton.setEnabled(true);
            }
        });
        mCountDown.start();
    }

    private void stopChronometer() {
        mCountDown.cancel();
    }

    public void startRemind(View view) {
        Toast.makeText(this, R.string.startReminder, Toast.LENGTH_SHORT).show();

        startChronometer(getTime());

        setEnableMainActivity(false);
        startAlarm();
    }

    public void stopRemind(View view) {
        stopAlarm();
    }

    public void startAlarm() {
        long time = getTime();
        NotificationManagerCompat notificationManager = getNotificationManager();
        notificationManager.cancel(NOTIFICATION_ID);

        Intent removeIntent = new Intent(ACTION_REMOVE_TIMER, null, this, TimerService.class);
        PendingIntent pendingRemoveIntent = PendingIntent.getService(this, 0, removeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager.notify(NOTIFICATION_ID, buildNotification(time * 1000, pendingRemoveIntent));
        registerAlarmManager(time * 1000);
    }

    public void stopAlarm() {
        NotificationManagerCompat notificationManager = getNotificationManager();
        notificationManager.cancel(NOTIFICATION_ID);
    }



    private void registerAlarmManager(long duration) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_SHOW_ALARM, null, this, TimerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long time = System.currentTimeMillis() + duration;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
}