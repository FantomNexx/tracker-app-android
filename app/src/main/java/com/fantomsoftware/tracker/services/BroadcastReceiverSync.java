package com.fantomsoftware.tracker.services;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnEvent;
import com.fantomsoftware.tracker.interfaces.OnResultTrackArray;
import com.fantomsoftware.tracker.tasks.Task_PointsGetUnsynced;
import com.fantomsoftware.tracker.tasks.Task_PointsSync;


public class BroadcastReceiverSync extends BroadcastReceiver{
//--------------------------------------------------------------------
@Override
public void onReceive( Context context, Intent intent ){
  //AppData.ServiceStartSyncing();
  PointsUnsyncedGet();
}

//--------------------------------------------------------------------
private void PointsUnsyncedGet(){
  //Log.d( AppData.log_key, "ServiceSyncing.handleMessage() START" );

  OnResultTrackArray on_track_got = new OnResultTrackArray(){
    @Override
    public void OnResult( SparseArray<TrackPoint> points ){
      PointsUnsyncedSync( points );
      //Log.d( AppData.log_key, "ServiceSyncing.handleMessage().OnResult()" );
    }//OnResult
  };//on_track_got

  Task_PointsGetUnsynced task = new Task_PointsGetUnsynced();
  task.SetOnResult( on_track_got );
  task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );

  //Log.d( AppData.log_key, "ServiceSyncing.handleMessage() END" );
}
//--------------------------------------------------------------------
private void PointsUnsyncedSync( SparseArray<TrackPoint> points ){

  Log.d( AppData.log_key, "ServiceSyncing.PointsUnsyncedSync()" );

  if( points == null ){
    Log.d( AppData.log_key, "ServiceSyncing.PointsUnsyncedSync() NULL" );
    return;
  }//if

  if( points.size() == 0 && !AppData.IsTracking() ){
    AppData.SyncingSetStopped();
    AppData.NotificationShowSyncFinished();
    Log.d( AppData.log_key, "ServiceSyncing.PointsUnsyncedSync() END NOTHING TO SYNC" );
    return;
  }//if nothing to sync


  OnEvent on_sync_ended = new OnEvent(){
    @Override
    public void OnEvent(){
      ScheduleSyncing.Start();
    }//OnEvent
  };//on_sync_ended

  Task_PointsSync task = new Task_PointsSync();
  task.SetTrackPoints( points );
  task.SetOnSyncEnded( on_sync_ended );
  task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
}
//--------------------------------------------------------------------
}//BroadcastReceiverSync