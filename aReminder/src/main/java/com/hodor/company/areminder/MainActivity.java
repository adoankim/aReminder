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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private Intent remindServiceIntent;
    public static enum units {HOURS, MINUTES};

    public static enum categories {FOOD, WORK, SPORT};

    private units unit = units.MINUTES;
    private categories category = categories.FOOD;
    private NumberPicker mPicker;
    private Button start;
    private Button stop;

    private View mLayoutChronometer;
    private TextView mChronometer;
    private CountDown mCountDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
        configureMaxValuePicker();

        initEvents();
    }

    private void setup() {
        this.start = ((Button) findViewById(R.id.startButtton));
        this.stop = ((Button) findViewById(R.id.stopButtton));
        mPicker = ((NumberPicker) findViewById(R.id.time));
        mPicker.setMinValue(1);
        mPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        if(checkServiceState()){
            this.stop.setVisibility(View.VISIBLE);
            this.start.setVisibility(View.GONE);
        }else{
            this.stop.setVisibility(View.GONE);
            this.start.setVisibility(View.VISIBLE);
        }

        mLayoutChronometer = findViewById(R.id.layout_chronometer);
        mLayoutChronometer.setVisibility(View.GONE);
        mChronometer = (TextView)findViewById(R.id.chronometer);
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
    private void configureMaxValuePicker() {
        mPicker.setMaxValue((unit == units.HOURS) ? 24 : 60);
    }

    private void initEvents() {
        ((RadioGroup)findViewById(R.id.time_unit)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                unit = (checkedId==R.id.time_hours)?units.HOURS:units.MINUTES;
                configureMaxValuePicker();
            }
        });

        ((RadioGroup)findViewById(R.id.category)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                category = (checkedId==R.id.food)?
                        categories.FOOD:
                        (checkedId==R.id.work)?categories.WORK:categories.SPORT;
            }
        });
        ((Button)findViewById(R.id.countDownButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDown.cancel();
                stopRemind(null);
                mLayoutChronometer.setVisibility(View.GONE);
                mCountDown = null;
            }
        });
    }

    public void startRemind(View view) {
        Toast.makeText(this, R.string.startReminder, Toast.LENGTH_SHORT).show();
        this.remindServiceIntent.putExtra("time", 3);//mPicker.getValue() * ((unit == units.MINUTES) ? 60 : 3600));
        this.remindServiceIntent.putExtra("unit", unit.ordinal());
        this.remindServiceIntent.putExtra("task", category.ordinal());
        startService(this.remindServiceIntent);

        view.setVisibility(View.GONE);
        this.stop.setVisibility(View.VISIBLE);

        startChronometer(mPicker.getValue() * ((unit == units.MINUTES) ? 60 : 3600));
    }
    public void stopRemind(View view){
        stopService(this.remindServiceIntent);
        if(view!=null) {
            view.setVisibility(View.GONE);
        }
        this.start.setVisibility(View.VISIBLE);
    }

    private void startChronometer(int seconds) {
        mLayoutChronometer.setVisibility(View.VISIBLE);
        mCountDown = new CountDown(seconds*1000, 1000, mChronometer);
        mCountDown.start();
    }

    public class CountDown extends CountDownTimer {

        TextView mChrono;
        units format;

        public CountDown(long millisInFuture, long countDownInterval, TextView chronoView) {
            super(millisInFuture, countDownInterval);
            mChrono = chronoView;
            format = (millisInFuture>=3600000)?units.HOURS:units.MINUTES;
        }

        @Override
        public void onFinish() {
            mChrono.setText("Done!");
            ((Button)findViewById(R.id.countDownButton)).setText("Close");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long durationSeconds = millisUntilFinished/1000;
            String time;
            if(this.format == units.HOURS) {
                time = String.format("%02d:%02d:%02d", durationSeconds / 3600,
                        (durationSeconds % 3600) / 60, (durationSeconds % 60));
            } else {
                time = String.format("%02d:%02d", (durationSeconds % 3600) / 60,
                        (durationSeconds % 60));
            }
            mChrono.setText(time);
        }
    }
}