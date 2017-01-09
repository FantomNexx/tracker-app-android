package com.fantomsoftware.tracker.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnResultTrackArray;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task_init_app return parameter
>
*/

public class Task_PointsGetUnsynced extends AsyncTask<Void, Void, SparseArray<TrackPoint>>{
//--------------------------------------------------------------------
private OnResultTrackArray on_result;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResult( OnResultTrackArray on_result ){
  this.on_result = on_result;
}//SetOnSelected
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected SparseArray<TrackPoint> doInBackground( Void... voids ){

  Log.d( AppData.log_key, "Task_PointsGetUnsynced" );

  if( AppData.instance.db_track_points == null ){
    AppData.instance.InitDatabase();
  }//if no database

  //if we could not init database
  if( AppData.instance.db_track_points == null ){
    return null;
  }//if database cannot be inited

  return AppData.instance.db_track_points.GetNotSynced( 500 );

}//doInBackground
//--------------------------------------------------------------------
@Override
protected void onPostExecute( SparseArray<TrackPoint> track ){

  super.onPostExecute( track );

  if( on_result != null ){
    on_result.OnResult( track );
  }//if

}//onPostExecute
//--------------------------------------------------------------------

}//Task_PointsGetUnsynced