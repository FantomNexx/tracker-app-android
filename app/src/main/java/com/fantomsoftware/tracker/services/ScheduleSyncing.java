package com.fantomsoftware.tracker.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.fantomsoftware.tracker.data.AppData;


public class ScheduleSyncing{
//--------------------------------------------------------------------
public static void Start(){

  Log.d( AppData.log_key, "ScheduleSyncing.Start()" );

  Intent intent_action = new Intent( AppData.KEY_SYNC );
  PendingIntent pending_intent = PendingIntent.getBroadcast(
      AppData.context, 0, intent_action, 0 );

  AlarmManager alarmManager = (AlarmManager)
      AppData.context.getSystemService( Context.ALARM_SERVICE );

  if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
    alarmManager.cancel( pending_intent );
    alarmManager.setAndAllowWhileIdle(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + AppData.TIME_PERIOD_SYNC_REPEAT,
        pending_intent
    );
  }else{
    alarmManager.cancel( pending_intent );
    alarmManager.set(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + AppData.TIME_PERIOD_SYNC_REPEAT,
        pending_intent
    );
  }//if SKD

  AppData.SyncingSetStarted();
}
//--------------------------------------------------------------------

}//ScheduleSyncing
