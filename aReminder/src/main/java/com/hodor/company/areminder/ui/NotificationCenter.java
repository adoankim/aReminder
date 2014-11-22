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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.hodor.company.areminder.R;
import com.hodor.company.areminder.util.SimpleSharedPreferences;
import com.hodor.company.areminder.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by toni on 31/08/14.
 */
public class NotificationCenter {

    private static NotificationCenter sIntance = null;
    private static String TITLE = "aReminder";

    private Context mContext;
    private SimpleSharedPreferences mPreference;

    public NotificationCenter(Context context) {
        this.mContext = context;
        this.mPreference = SimpleSharedPreferences.getSimpleSharedPreference(context);
    }

    public static NotificationCenter getNotificationCenter(Context context) {
        if(NotificationCenter.sIntance == null) {
            NotificationCenter.sIntance = new NotificationCenter(context);
        }
        return NotificationCenter.sIntance;
    }

    private NotificationCompat.Builder getBaseBuilder(int category) {
        return new NotificationCompat.Builder(this.mContext)
                .setSmallIcon(getNotificationIcon(category))
                .setContentTitle(TITLE)
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.mContext.getResources(),
                                getNotificationIcon(category)
                        )
                );
    }

    public Notification buildAlarmNotification(int category, int duration, PendingIntent remove, PendingIntent show) {
        NotificationCompat.Builder builder = getBaseBuilder(category);
        return builder.setContentText(getNotificationText(duration))
                .setContentText(TimeUtil.getTimeString( duration ) )
                .setUsesChronometer(true)
                .setWhen( System.currentTimeMillis() + duration )
                .addAction(android.R.drawable.ic_delete,
                        this.mContext.getString(R.string.action_remove_timer),
                        remove)
                .addAction(android.R.drawable.ic_lock_idle_alarm,
                        this.mContext.getString(R.string.action_expand_timer),
                        show)
                .build();



    }

    public Notification buildFinishNotification(int category) {
        NotificationCompat.Builder builder = getBaseBuilder(category);
        String text = String.format(
                this.mContext.getString(R.string.finish),
                this.getNotificationTitle(category)
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
        int stringID = R.string.category_default;
        switch (category) {
            case 0:
                stringID = R.string.category_food;
                break;
            case 1:
                stringID = R.string.category_work;
                break;
            case 2:
                stringID = R.string.category_sport;
                break;

        }
        return this.mContext.getString(stringID);
    }

}
