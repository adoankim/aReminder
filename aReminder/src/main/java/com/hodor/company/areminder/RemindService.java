package com.hodor.company.areminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by adoankim on 25/06/14.
 */
public class RemindService extends Service {

    private int time;
    private int task;
    private int unit;
    private AlarmManager alarmManager;
    private NotificationManagerCompat nManager;


    @Override
    public IBinder onBind(Intent intent) {
        this.alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nManager = NotificationManagerCompat.from(RemindService.this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.time = intent.getIntExtra("time", 1);
        this.unit = intent.getIntExtra("unit", 0);
        this.task = intent.getIntExtra("task", 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(time * 1000);
                    Log.d("test", time + " ; icon=" + getNotificationIcon());


                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(RemindService.this)
                                    .setContentTitle(getNotificationTitle())
                                    .setSmallIcon(getNotificationIcon())
                                    .setContentText(getNotificationText());

                    Notification notification =
                            new WearableNotifications.Builder(notificationBuilder)
                                    .build();
// Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(RemindService.this);
// Build the notification and issues it with notification manager.
                    notificationManager.notify(1, notification);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        int xtime = time / ((this.unit == 1) ? 60 : 3600);
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
        super.onDestroy();
    }
}
