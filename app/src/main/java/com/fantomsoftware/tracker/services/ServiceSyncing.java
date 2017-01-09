package com.fantomsoftware.tracker.services;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnEvent;
import com.fantomsoftware.tracker.interfaces.OnResultTrackArray;
import com.fantomsoftware.tracker.tasks.Task_PointsGetUnsynced;
import com.fantomsoftware.tracker.tasks.Task_PointsSync;


public class ServiceSyncing extends Service{
//--------------------------------------------------------------------
private ServiceHandler service_handler;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
public void onCreate(){

  HandlerThread thread = new HandlerThread(
      "ServiceStartArguments", Process.THREAD_PRIORITY_DEFAULT );

  thread.start();

  Looper service_looper = thread.getLooper();
  service_handler = new ServiceHandler( service_looper );
}
//--------------------------------------------------------------------
@Override
public int onStartCommand( Intent intent, int flags, int service_id ){

  // For each start request, send a message to start a job and
  // deliver the start ID so we know which request we're stopping
  // when we finish the job
  Message msg = service_handler.obtainMessage();
  msg.arg1 = service_id;
  service_handler.sendMessage( msg );

  // If we get killed, after returning from here, restart
  return START_STICKY;

}
//--------------------------------------------------------------------
@Override
public IBinder onBind( Intent intent ){
  return null;// We don't provide binding, so return null
}
//--------------------------------------------------------------------
@Override
public void onDestroy(){
  //nothing to clear
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private final class ServiceHandler extends Handler{
  //------------------------------------------------------------------
  public ServiceHandler( Looper looper ){
    // Handler that receives messages from the thread
    super( looper );
  }
  //------------------------------------------------------------------
  @Override
  public void handleMessage( Message msg ){
    PointsUnsyncedGet();
  }
  //------------------------------------------------------------------


  //------------------------------------------------------------------
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
  //------------------------------------------------------------------
  private void PointsUnsyncedSync( SparseArray<TrackPoint> points ){

    Log.d( AppData.log_key, "ServiceSyncing.PointsUnsyncedSync()" );

    if( points == null ){
      Log.d( AppData.log_key, "ServiceSyncing.PointsUnsyncedSync() NULL" );
      return;
    }//if

    if( points.size() == 0 && !AppData.IsTracking() ){
      stopSelf();
      AppData.SyncingSetStopped();
      AppData.NotificationShowSyncFinished();
      Log.d( AppData.log_key, "ServiceSyncing.PointsUnsyncedSync() END NOTHING TO SYNC" );
      return;
    }//if nothing to sync


    OnEvent on_sync_ended = new OnEvent(){
      @Override
      public void OnEvent(){
        ScheduleSyncing.Start();
        stopSelf();
      }//OnEvent
    };//on_sync_ended

    Task_PointsSync task = new Task_PointsSync();
    task.SetTrackPoints( points );
    task.SetOnSyncEnded( on_sync_ended );
    task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
  }
  //------------------------------------------------------------------
}//ServiceHandler
//--------------------------------------------------------------------

}//ServiceSyncing
