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


    public static enum ROLE{SERVER, CONSUMER};
    public static enum ACTION{ASK_TIME, STOP_TIMER, PAUSE_TIMER, CONTINUE_TIMER, TIMER_FINISH}
    private final static String actionStrings[];
    private final static String roleStrings[];
    private final TimeAskAction actions[];
    static{
        roleStrings = new String[ROLE.values().length];
        roleStrings[ROLE.SERVER.ordinal()] = "com.hodor.company.areminder.SERVER";
        roleStrings[ROLE.CONSUMER.ordinal()] = "com.hodor.company.areminder.CONSUMER";
        actionStrings = new String[ACTION.values().length];
        actionStrings[ACTION.ASK_TIME.ordinal()] = "com.hodor.company.areminder.ASK_TIME";
        actionStrings[ACTION.STOP_TIMER.ordinal()] = "com.hodor.company.areminder.STOP_TIMER";
        actionStrings[ACTION.PAUSE_TIMER.ordinal()] = "com.hodor.company.areminder.PAUSE_TIMER";
        actionStrings[ACTION.CONTINUE_TIMER.ordinal()] = "com.hodor.company.areminder.CONTINUE_TIMER";
        actionStrings[ACTION.TIMER_FINISH.ordinal()] = "com.hodor.company.areminder.TIMER_FINISH";

    }

    {
        actions = new TimeAskAction[ACTION.values().length];

        actions[ACTION.ASK_TIME.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                Long timeLeft;
                int roleId = getRole(intent);
                
                if(roleId == ROLE.CONSUMER.ordinal()) {
                    timeLeft = intent.getLongExtra("message", 0);
                    ((TimeConsumer) client).receiveTimeLeft(timeLeft);
                }else if(roleId == ROLE.SERVER.ordinal()){
                    timeLeft = ((TimeProducer) client).giveTimeLeft();
                    TimeAsking.this.notifyTimeLeft(timeLeft);
                }
            }
        };


        actions[ACTION.STOP_TIMER.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                if(client instanceof TimeProducer) {
                    client.stopReminder();
                }
            }
        };


        actions[ACTION.PAUSE_TIMER.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                if(client instanceof TimeProducer) {
                    client.pauseReminder();
                }
            }
        };


        actions[ACTION.CONTINUE_TIMER.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                if(client instanceof TimeProducer) {
                    client.continueReminder();
                }
            }
        };

        actions[ACTION.TIMER_FINISH.ordinal()] = new TimeAskAction() {
            @Override
            public void perform(Intent intent) {
                if(client instanceof TimeProducer) {
                    ((TimeConsumer) client).timerFinish();
                }
            }
        };

    }

    public TimeAsking(TimeAskingClient client, ROLE role, ACTION ... actions){
        this.client = client;
        filter = new IntentFilter();
        filter.addAction(roleStrings[role.ordinal()]);
        for(ACTION action : actions) {
            filter.addAction(actionStrings[action.ordinal()]);
        }
        client.getContext().registerReceiver(this, filter);
        isRegistered = true;
    }

    public void askForTimeLeft(){
        sendConsumerPetition(ACTION.ASK_TIME);
    }

    public void notifyTimeLeft(long timeLeft){
        Intent intent = new Intent();
        intent.setAction(actionStrings[ACTION.ASK_TIME.ordinal()]);
        intent.setAction(roleStrings[ROLE.SERVER.ordinal()]);
        intent.putExtra("message", timeLeft);
        client.getContext().sendBroadcast(intent);
    }

    public void notifyFinish(){
        sendServerPetition(ACTION.TIMER_FINISH);
    }

    public void sendStopTimer() {
        sendConsumerPetition(ACTION.STOP_TIMER);
    }

    public void sendPauseTimer() {
        sendConsumerPetition(ACTION.PAUSE_TIMER);
    }

    public void sendContinueTimer() {
        sendConsumerPetition(ACTION.CONTINUE_TIMER);
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



    private void sendServerPetition(ACTION action){
        sendPetition(ROLE.CONSUMER, action);
    }

    private void sendConsumerPetition(ACTION action){
        sendPetition(ROLE.SERVER, action);
    }

    private void sendPetition(ROLE role, ACTION action){
        Intent intent = new Intent();
        intent.setAction(actionStrings[action.ordinal()]);
        intent.setAction(roleStrings[role.ordinal()]);
        client.getContext().sendBroadcast(intent);
    }


    private int getRole(Intent intent) {
        String role = intent.getStringExtra("ROLE");
        if(role  == null){
            return -1;
        }
        return java.util.Arrays.binarySearch(roleStrings, role);
    }
    private interface TimeAskAction{
        public void perform(Intent intent);
    }

}
