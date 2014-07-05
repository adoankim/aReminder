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
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private Intent remindServiceIntent;
    public static enum units {HOURS, MINUTES};

    public static enum categories {FOOD, WORK, SPORT};

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
        this.mCategory = (ListView) findViewById(R.id.category);
        this.startButton = (Button) findViewById(R.id.startButtton);
        this.stopButton = (Button) findViewById(R.id.stopButtton);
        this.mTineUnit = (Switch) findViewById(R.id.time_unit);
        this.mTimePicker = (NumberPicker) findViewById(R.id.time);
        this.mLayoutChronometer = findViewById(R.id.layout_chronometer);
        this.mChronometer = (TextView)findViewById(R.id.chronometer);
        this.closeButton = (ImageButton)findViewById(R.id.close);
    }

    private void setup() {
        this.mTimePicker.setMinValue(1);
        this.mTimePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        initAdapter();
        this.mCategory.setAdapter(this.mCategoriesAdapter);

        if(checkServiceState()){
            //this.startButton.setVisibility(View.GONE);
            //this.stopButton.setVisibility(View.VISIBLE);
            //}else{
            //this.stopButton.setVisibility(View.GONE);
            //this.startButton.setVisibility(View.VISIBLE);
        }
        this.mLayoutChronometer.setVisibility(View.GONE);
    }

    private void initAdapter() {
        ArrayList<int[]> lCategories = new ArrayList<int[]>();
        lCategories.add(new int[] {R.string.food, R.drawable.food});
        lCategories.add(new int[] {R.string.work, R.drawable.work});
        lCategories.add(new int[] {R.string.sport, R.drawable.sport});
        this.mCategoriesAdapter = new CategoryAdapter(this, lCategories);
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
                category = ( mCategoriesAdapter.getItemId(position) == R.string.food)?
                        categories.FOOD:
                        (mCategoriesAdapter.getItemId(position) == R.string.work)?
                                categories.WORK:
                                categories.SPORT;

                for(int i=0; i < parent.getChildCount(); i++) {
                    ((ImageView)parent.getChildAt(i).findViewById(R.id.picture)).setColorFilter(Utils.getGrayScaleFilter());
                    parent.getChildAt(i).setAlpha(0.4f);
                }
                ((ImageView)view.findViewById(R.id.picture)).clearColorFilter();
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
        if(enable) {
            this.mLayoutChronometer.setVisibility(View.GONE);
        } else {
            this.stopButton.setVisibility(View.VISIBLE);
            this.closeButton.setVisibility(View.GONE);
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