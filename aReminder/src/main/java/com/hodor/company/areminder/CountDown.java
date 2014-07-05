package com.hodor.company.areminder;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.hodor.company.areminder.MainActivity.units;

/**
 * Created by Toni Martinez on 02/07/2014.
 */
public class CountDown extends CountDownTimer {

    TextView mChrono;
    units format;
    OnFinishEventListener mCallback;
    private long secondsToFinish;

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
        this.secondsToFinish = millisUntilFinished/1000;
        String time;
        if(this.format == units.HOURS) {
            time = String.format("%02d:%02d:%02d", this.secondsToFinish / 3600,
                    (this.secondsToFinish % 3600) / 60, (this.secondsToFinish % 60));
        } else {
            time = String.format("%02d:%02d", (this.secondsToFinish % 3600) / 60,
                    (this.secondsToFinish % 60));
        }
        mChrono.setText(time);
    }

    public long getSecondsToFinish() {
        return this.secondsToFinish;
    }
}