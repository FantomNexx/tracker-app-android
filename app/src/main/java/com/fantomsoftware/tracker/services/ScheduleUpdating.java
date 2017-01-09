package com.fantomsoftware.tracker.services;

import android.util.Log;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.ui_frags.Frag_TrackMe;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;


public class ScheduleUpdating{
//--------------------------------------------------------------------
private static ScheduleUpdating         instance  = null;
private        ScheduledExecutorService scheduler = null;
private        Future<?>                future    = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Start(){

  Log.d( AppData.log_key, "ScheduleUpdating.Start()" );

  if( scheduler == null ){
    scheduler = Executors.newSingleThreadScheduledExecutor();
  }//if


  final Runnable runnable = new Runnable(){
    public void run(){

      if( !AppData.IsUIPaused() ){
        Frag_TrackMe.handler_update_map.sendEmptyMessage( 0 );
      }//if

      if( !AppData.IsSyncing() ){
        Stop();
      }//if need to stop
    }//run
  };


  if( future != null ){
    future.cancel( true );
  }//if

  future = scheduler.scheduleAtFixedRate(
      runnable, 0, AppData.TIME_PERIOD_MAP_UPDATE,
      java.util.concurrent.TimeUnit.MILLISECONDS );

  Log.d( AppData.log_key, "IS_UPDATING TRUE" );
}
//--------------------------------------------------------------------
private void Stop(){

  Log.d( AppData.log_key, "ScheduleUpdating.Stop()" );

  if( future != null ){
    future.cancel( true );
  }//if thread exist

  Log.d( AppData.log_key, "IS_UPDATING FALSE" );
}
//--------------------------------------------------------------------
private void ShutDown(){

  Log.d( AppData.log_key, "ScheduleUpdating.ShutDown()" );

  if( scheduler != null ){
    scheduler.shutdown();
  }//if thread exist
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void ScheduleStart(){

  if( ScheduleUpdating.instance == null ){
    ScheduleUpdating.instance = new ScheduleUpdating();
  }//if

  ScheduleUpdating.instance.Start();
}
//--------------------------------------------------------------------
public static void ScheduleStop(){
  if( ScheduleUpdating.instance != null ){
    ScheduleUpdating.instance.Stop();
  }
}
//--------------------------------------------------------------------
public static void ScheduleShutDown(){
  if( ScheduleUpdating.instance != null ){
    ScheduleUpdating.instance.ShutDown();
  }
}
//--------------------------------------------------------------------

}//ScheduleUpdating
