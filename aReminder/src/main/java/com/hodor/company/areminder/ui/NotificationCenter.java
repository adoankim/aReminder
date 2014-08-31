package com.hodor.company.areminder.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.hodor.company.areminder.R;
import com.hodor.company.areminder.util.SimpleSharedPreferences;
import com.hodor.company.areminder.util.TimeUtil;

/**
 * Created by toni on 31/08/14.
 */
public class NotificationCenter {

    private static NotificationCenter sIntance = null;
    private static String TITLE = "aReminder";

    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private SimpleSharedPreferences mPreference;

    public NotificationCenter(Context context) {
        this.mContext = context;
        this.mNotificationManager = NotificationManagerCompat.from(this.mContext);
        this.mPreference = SimpleSharedPreferences.getSimpleSharedPreference(context);
    }

    public static NotificationCenter getNotificationCenter(Context context) {
        if(NotificationCenter.sIntance == null) {
            NotificationCenter.sIntance = new NotificationCenter(context);
        }
        return NotificationCenter.sIntance;
    }

    private NotificationCompat.Builder getBaseBuilder(int category) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.mContext)
                .setSmallIcon(getNotificationIcon(category))
                .setContentTitle(TITLE)
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.mContext.getResources(),
                                getNotificationIcon(category)
                        )
                );
        return builder;
    }

    public Notification buildAlarmNotification(int category, int duration, PendingIntent pendingIntent ) {
        NotificationCompat.Builder builder = getBaseBuilder(category);
        return builder.setContentText(getNotificationText(duration))
                .setUsesChronometer(true)
                .setWhen(this.mPreference.read(MainActivity.ALARM_TIME, 0))
                .addAction(android.R.drawable.ic_delete,
                        this.mContext.getString(R.string.action_remove_timer),
                        pendingIntent)
                .addAction(android.R.drawable.ic_lock_idle_alarm,
                        this.mContext.getString(R.string.action_expand_timer),
                        pendingIntent)
                .build();
    }

    private Notification buildFinishNotification(int category) {
        NotificationCompat.Builder builder = getBaseBuilder(category);
        String text = String.format(
                this.mContext.getString(R.string.finish),
                this.mContext.getString(category)
        );
        builder.setContentText(text);
        return builder.build();
    }

    private int getNotificationIcon(int category) {
        switch (category) {
            case 0:
                return R.drawable.food;
            case 1:
                return R.drawable.work;
            case 2:
                return R.drawable.sport;

        }
        return R.drawable.ic_launcher;
    }

    private CharSequence getNotificationText(long time) {
        return TimeUtil.getTimeString(time);
    }

    private CharSequence getNotificationTitle(int category) {
        switch (category) {
            case 0:
                return "Comida";
            case 1:
                return "Trabajo";
            case 2:
                return "Deporte";

        }
        return "nada en especial";
    }

}
