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

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.hodor.company.areminder.R;
import com.hodor.company.areminder.ui.MainActivity;
import com.hodor.company.areminder.ui.NotificationCenter;

public class TimerService extends IntentService
{

    private static final String TAG = TimerService.class.getSimpleName();

    public TimerService() {
        super( "TimerService" );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if( MainActivity.ACTION_SHOW_ALARM.equals( action ) ) {
            showAlarm(intent.getIntExtra("category", R.string.category_default));
        } else if( MainActivity.ACTION_REMOVE_TIMER.equals( action ) ) {
            Log.e(TAG, "Remove alarm");
            removeAlarm();
        }
    }

    private void showAlarm(int category) {
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = { 0, getResources().getInteger( R.integer.vibration_duration ) };
        v.vibrate( pattern, -1 );
        Intent intent = new Intent( this, MainActivity.class )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(MainActivity.ACTION_REMOVE_TIMER, 1);
        startActivity( intent );

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Notification notification = NotificationCenter.getNotificationCenter(this).buildFinishNotification(category);
        notificationManager.notify(MainActivity.END_NOTIFICATION_ID, notification);
    }

    private void removeAlarm() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel( 1 );

        AlarmManager alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE );

        Intent intent = new Intent( MainActivity.ACTION_SHOW_ALARM, null, this, TimerService.class );
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel( pendingIntent );
    }
}
