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
package com.hodor.company.areminder.timer;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.hodor.company.areminder.ui.MainActivity;
import com.hodor.company.areminder.ui.MainActivity.units;

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
