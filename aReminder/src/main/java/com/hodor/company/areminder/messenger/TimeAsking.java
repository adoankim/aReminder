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
package com.hodor.company.areminder.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by adoankim on 06/07/14.
 */
public class TimeAsking extends BroadcastReceiver {
    private TimeAskingClient client;
    private IntentFilter filter;
    private boolean isRegistered = false;

    public static enum ACTION{ASK_TIME, SEND_TIME, TIMER_FINISH}
    private final static String actionStrings[];
    private final TimeAskAction actions[];
    static{

        actionStrings = new String[ACTION.values().length];
        actionStrings[ACTION.ASK_TIME.ordinal()] = "com.hodor.company.areminder.ASK_TIME";
        actionStrings[ACTION.SEND_TIME.ordinal()] = "com.hodor.company.areminder.SEND_TIME";
        actionStrings[ACTION.TIMER_FINISH.ordinal()] = "com.hodor.company.areminder.TIMER_FINISH";

    }

    {
        actions = new TimeAskAction[ACTION.values().length];

        actions[ACTION.ASK_TIME.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                Long timeLeft = intent.getLongExtra("message", 0);
                Log.d("Activity", "reciving time left" + timeLeft);
                ((TimeConsumer)client).receiveTimeLeft(timeLeft);
            }
        };

        actions[ACTION.SEND_TIME.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                Long timeLeft = ((TimeProducer)client).giveTimeLeft();
                Log.d("Service", "sending time left" + timeLeft);
                TimeAsking.this.notifyTimeLeft(timeLeft);

            }
        };

        actions[ACTION.TIMER_FINISH.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                Long timeLeft = ((TimeProducer)client).giveTimeLeft();
                TimeAsking.this.notifyTimeLeft(timeLeft);

            }
        };

    }

    public TimeAsking(TimeAskingClient client, ACTION action){
        this.client = client;
        filter = new IntentFilter();
        filter.addAction(actionStrings[action.ordinal()]);
        client.getContext().registerReceiver(this, filter);
        isRegistered = true;
    }

    public void askForTimeLeft(){
        Intent intent = new Intent();
        intent.setAction(actionStrings[ACTION.SEND_TIME.ordinal()]);
        client.getContext().sendBroadcast(intent);
    }

    public void notifyTimeLeft(long timeLeft){
        Intent intent = new Intent();
        intent.setAction(actionStrings[ACTION.ASK_TIME.ordinal()]);
        intent.putExtra("message", timeLeft);
        client.getContext().sendBroadcast(intent);
    }

    public void unregister(){
        if(isRegistered) {
            Log.d("Client", "unregistering=" + this.client.getClass().getName());
            this.client.getContext().unregisterReceiver(this);
            isRegistered = false;
        }
    }

    public void register(){
        if(!isRegistered) {
            Log.d("Client", "registering=" + this.client.getClass().getName());
            this.client.getContext().registerReceiver(this, filter);
            isRegistered = true;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int idx = java.util.Arrays.binarySearch(actionStrings, intent.getAction());
        actions[idx].perform(intent);
    }


    private interface TimeAskAction{
        public void perform(Intent intent);
    }

}
