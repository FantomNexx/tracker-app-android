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

public class Task_PointsGetLatest extends AsyncTask<Void, Void, SparseArray<TrackPoint>>{
//--------------------------------------------------------------------
private OnResultTrackArray on_result;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResult( OnResultTrackArray on_result ){
  this.on_result = on_result;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected SparseArray<TrackPoint> doInBackground( Void... voids ){

  Log.d( AppData.log_key, "Task_PointsGetLatest" );

  if( AppData.instance.db_track_points == null ){
    AppData.instance.InitDatabase();
  }//if no database

  //if we could not init database
  if( AppData.instance.db_track_points == null ){
    return null;
  }//if database cannot be inited

  SparseArray<TrackPoint> points =
      AppData.instance.db_track_points.GetLatest( 500 );

  if( points == null ){
    return null;
  }//if no points


  SparseArray<TrackPoint> points_filtered = new SparseArray<>();

  TrackPoint p1;
  TrackPoint p2;


  double time_diff;
  int    counter = 0;

  p1 = points.valueAt( 0 );
  points_filtered.put( counter++, p1 );


  for( int i = 1; i < points.size(); i++ ){

    p2 = points.valueAt( i );

    //show points no longer than for chk_time_diff
    time_diff = p1.timestamp - p2.timestamp;

    if( time_diff > AppData.TIME_PERIOD_MAX_DIFFERENCE ){
      break;
    }//if

    points_filtered.put( counter++, p2 );
    p1 = p2;
  }//for

  //Log.d( AppData.log_key, "Task_PointsGetLatest END" );

  return points_filtered;
}
//--------------------------------------------------------------------
@Override
protected void onPostExecute( SparseArray<TrackPoint> track ){
  super.onPostExecute( track );

  if( on_result != null ){
    on_result.OnResult( track );
  }//if
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private SparseArray<TrackPoint> GetLastPoints(){

  if( AppData.instance.db_track_points == null ){
    AppData.instance.InitDatabase();
  }//if no database

  //if we could not init database
  if( AppData.instance.db_track_points == null ){
    return null;
  }//if database cannot be inited

  return AppData.instance.db_track_points.GetLatest( 500 );
}
//--------------------------------------------------------------------

}//Task_PointsGetLatest