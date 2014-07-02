package com.hodor.company.areminder;

import android.app.PendingIntent;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import com.hodor.company.areminder.MainActivity.units;

/**
 * Created by E437812 on 02/07/2014.
 */
public class CountDown extends CountDownTimer {

    TextView mChrono;
    units format;
    OnFinishEventListener mCallback;

    public interface OnFinishEventListener {
        public void onFinish();
    }

    public CountDown(long millisInFuture, long countDownInterval, TextView chronoView) {
        super(millisInFuture, countDownInterval);
        mChrono = chronoView;
        format = (millisInFuture>=3600000)? MainActivity.units.HOURS:units.MINUTES;
    }

    public void setOnFinishEventListener(OnFinishEventListener listener) {
        this.mCallback = listener;
    }

    @Override
    public void onFinish() {
        mChrono.setText("Done!");
        if(this.mCallback!=null) {
            mCallback.onFinish();
        }
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
