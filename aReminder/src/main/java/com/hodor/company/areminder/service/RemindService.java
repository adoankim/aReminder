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


package com.hodor.company.areminder.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hodor.company.areminder.R;
import com.hodor.company.areminder.messenger.TimeAsking;
import com.hodor.company.areminder.messenger.TimeProducer;

public class RemindService extends Service implements TimeProducer {

    private Long time;
    private int task;
    private int unit;
    private Long timeLeft;
    private TimeAsking timeAskingManager;
    private CountDownTimer timer;
    private boolean isPaused = false;

    @Override
    public IBinder onBind(Intent intent) { return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        timeAskingManager = new TimeAsking(this, TimeAsking.ROLE.SERVER ,new TimeAsking.ACTION[] {
                TimeAsking.ACTION.ASK_TIME,
                TimeAsking.ACTION.STOP_TIMER,
                TimeAsking.ACTION.PAUSE_TIMER,
                TimeAsking.ACTION.CONTINUE_TIMER
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "staring!");
        this.time = intent.getLongExtra("time", 0) * 1000;
        this.unit = intent.getIntExtra("unit", 0);
        this.task = intent.getIntExtra("task", 0);
        this.timeLeft = time;
        Log.d("Service", "time received="+time);
        timeAskingManager.notifyTimeLeft(this.timeLeft);
        if(timer == null) {
            timer = new CountDown(timeLeft);
            timer.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }




    private int getNotificationIcon() {
        Log.d("task", this.task+"");
        switch (this.task) {
            case 0:
                return R.drawable.food;
            case 1:
                return R.drawable.work;
            case 2:
                return R.drawable.sport;

        }
        return R.drawable.ic_launcher;
    }

    private CharSequence getNotificationText() {
        long xtime = time / ((this.unit == 1) ? 60 : 3600);
        String string_unit = (this.unit == 1) ? "minutos" : "horas";
        return "Han pasado " + xtime + " " + string_unit;
    }

    private CharSequence getNotificationTitle() {
        switch (this.task) {
            case 0:
                return "Comida";

            case 1:
                return "Trabajo";
            case 2:
                return "Deporte";

        }
        return "";
    }


    @Override
    public void onDestroy() {
        timeAskingManager.unregister();
        super.onDestroy();
    }

    private class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture) {
            super(millisInFuture, 1000);
            Log.d("Service", "its the final coundown!: " + millisInFuture);
        }

        @Override
        public void onTick(long l) {
            timeLeft = l;
            Log.d("Service", "Tick " + timeLeft);
        }

        @Override
        public void onFinish() {
            Log.d("test", time + " ; icon=" + getNotificationIcon());


            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(RemindService.this)
                            .setContentTitle(getNotificationTitle())
                            .setSmallIcon(getNotificationIcon())
                            .setContentText(getNotificationText());

            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(RemindService.this);
            // Build the notification and issues it with notification manager.
            notificationManager.notify(1, notificationBuilder.build());
            timeAskingManager.notifyFinish();
            timer = null;
        }
    }

    @Override
    public Long giveTimeLeft() {
        return this.timeLeft;
    }

    @Override
    public void stopReminder() {
        Log.d("reminder", "Don't stop me nowww!");
        timer.cancel();
        this.stopSelf();
    }

    @Override
    public void pauseReminder() {
        if(!this.isPaused) {
            timer.cancel();
            this.isPaused = true;
        }
    }

    @Override
    public void continueReminder() {
        if(this.isPaused){
            timer = new CountDown(this.timeLeft);
            timer.start();
            this.isPaused = false;
        }

    }

    @Override
    public Context getContext() {
        return this;
    }
}
