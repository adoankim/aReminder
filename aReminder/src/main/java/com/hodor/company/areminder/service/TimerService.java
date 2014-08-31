package com.hodor.company.areminder.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.hodor.company.areminder.R;
import com.hodor.company.areminder.ui.MainActivity;


/**
 * Created by PaulTR on 6/29/14.
 */
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
            showAlarm();
        } else if( MainActivity.ACTION_REMOVE_TIMER.equals( action ) ) {
            Log.e(TAG, "Remove alarm");
            removeAlarm();
        }
    }

    private void showAlarm() {
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = { 0, getResources().getInteger( R.integer.vibration_duration ) };
        v.vibrate( pattern, -1 );
        Intent intent = new Intent( this, MainActivity.class )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(MainActivity.ACTION_REMOVE_TIMER, 1);
        startActivity( intent );
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
