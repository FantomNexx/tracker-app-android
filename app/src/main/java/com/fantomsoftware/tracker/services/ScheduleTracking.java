package com.fantomsoftware.tracker.services;

import android.util.Log;

import com.fantomsoftware.tracker.cmps.CmpLocationGet;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.utils.Utils;


public class ScheduleTracking{
//--------------------------------------------------------------------
private static ScheduleTracking instance     = null;
private        CmpLocationGet   cmp_location = null;
private        TrackPoint       last_point   = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Start(){

  if( cmp_location != null ){
    cmp_location.Stop();
  }//if

  CmpLocationGet.OnResultTrackPoint on_result_track_point =
      new CmpLocationGet.OnResultTrackPoint(){
        //----------------------------------------------------------
        @Override
        public void OnResultTrackPoint( TrackPoint track_point ){
          SaveTrackPoint( track_point );
        }//OnResultTrackPoint
        //----------------------------------------------------------
      };//on_result_track_point

  cmp_location = new CmpLocationGet();
  cmp_location.SetOnResultTrackPoint( on_result_track_point );
  cmp_location.Start();

  Utils.SetSharedPref( AppData.IS_TRACKING, true );
  Log.d( AppData.log_key, "IS_TRACKING TRUE" );
}
//--------------------------------------------------------------------
private void Stop(){
  cmp_location.Stop();
  Utils.SetSharedPref( AppData.IS_TRACKING, false );
  Log.d( AppData.log_key, "IS_TRACKING FALSE" );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void SaveTrackPoint( TrackPoint point ){

  if( point == null ){
    return;
  }//if

  if( last_point == null ){
    last_point = point;
    return;
  }//if

  if( point.accuracy > AppData.LOCATION_ACCURACY_MIN ){
    return;
  }//if too bad accuracy


  if( AppData.instance.db_track_points == null ){
    AppData.instance.InitDatabase();
  }//if no database

  //if we could not init database
  if( AppData.instance.db_track_points == null ){
    return;
  }//if database cannot be inited


  AppData.instance.db_track_points.Add( point );

  last_point = point;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void ScheduleStart(){

  if( ScheduleTracking.instance == null ){
    ScheduleTracking.instance = new ScheduleTracking();
  }

  ScheduleTracking.instance.Start();
}
//--------------------------------------------------------------------
public static void ScheduleStop(){
  if( ScheduleTracking.instance != null ){
    ScheduleTracking.instance.Stop();
  }
}
//--------------------------------------------------------------------

}//ScheduleTracking
