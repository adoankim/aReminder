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
package com.hodor.company.areminder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity extends Activity {

    private Intent remindServiceIntent;
    public static enum units {HOURS, MINUTES};

    public static enum categories {FOOD, WORK, SPORT};

    private units unit = units.MINUTES;
    private categories category = categories.FOOD;

    private NumberPicker mTimePicker;
    private RadioGroup mTineUnit;
    private RadioGroup mCategory;

    private Button startButton;
    private Button stopButton;
    private ImageButton closeButton;

    private View mLayoutChronometer;
    private TextView mChronometer;
    private CountDown mCountDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        setup();
        configureMaxValuePicker();

        initEvents();
    }

    private void initLayout() {
        this.startButton = (Button) findViewById(R.id.startButtton);
        this.stopButton = (Button) findViewById(R.id.stopButtton);
        this.mTineUnit = (RadioGroup) findViewById(R.id.time_unit);
        this.mCategory = (RadioGroup) findViewById(R.id.category);
        this.mTimePicker = (NumberPicker) findViewById(R.id.time);
        this.mLayoutChronometer = findViewById(R.id.layout_chronometer);
        this.mChronometer = (TextView)findViewById(R.id.chronometer);
        this.closeButton = (ImageButton)findViewById(R.id.close);
    }

    private void setup() {
        this.mTimePicker.setMinValue(1);
        this.mTimePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        if(checkServiceState()){
            //this.startButton.setVisibility(View.GONE);
            //this.stopButton.setVisibility(View.VISIBLE);
        //}else{
            //this.stopButton.setVisibility(View.GONE);
            //this.startButton.setVisibility(View.VISIBLE);
        }
        this.mLayoutChronometer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("test", "pausas");
    }

    private void configureMaxValuePicker() {
        mTimePicker.setMaxValue((unit == units.HOURS) ? 24 : 60);
    }

    private void initEvents() {
        this.mTineUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                unit = (checkedId == R.id.time_hours) ? units.HOURS : units.MINUTES;
                configureMaxValuePicker();
            }
        });

        this.mCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                category = (checkedId == R.id.food) ?
                        categories.FOOD :
                        (checkedId == R.id.work) ? categories.WORK : categories.SPORT;
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
        if(enable) {
            this.mLayoutChronometer.setVisibility(View.GONE);
        } else {
            this.stopButton.setVisibility(View.VISIBLE);
            this.closeButton.setEnabled(false);
            this.mLayoutChronometer.setVisibility(View.VISIBLE);
        }
    }

    private void startChronometer(int seconds) {
        mCountDown = new CountDown(seconds*1000, 1000, mChronometer);
        mCountDown.setOnFinishEventListener(new CountDown.OnFinishEventListener() {
            @Override
            public void onFinish() {
                stopButton.setVisibility(View.GONE);
                closeButton.setEnabled(true);
            }
        });
        mCountDown.start();
    }

    private void stopChronometer() {
        mCountDown.cancel();
    }

    public void startRemind(View view) {
        //Toast.makeText(this, R.string.startReminder, Toast.LENGTH_SHORT).show();

        this.remindServiceIntent.putExtra("time", 3);//mTimePicker.getValue() * ((unit == units.MINUTES) ? 60 : 3600));
        this.remindServiceIntent.putExtra("unit", unit.ordinal());
        this.remindServiceIntent.putExtra("task", category.ordinal());
        startService(this.remindServiceIntent);

        startChronometer(mTimePicker.getValue() * ((unit == units.MINUTES) ? 60 : 3600));
        setEnableMainActivity(false);
    }

    public void stopRemind(View view) {
        stopService(this.remindServiceIntent);
    }

    private boolean checkServiceState() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.hodor.company.RemindService".equals(service.service.getClassName())) {
                return true;
            }
        }
        this.remindServiceIntent = new Intent(this, RemindService.class);
        return false;
    }
}