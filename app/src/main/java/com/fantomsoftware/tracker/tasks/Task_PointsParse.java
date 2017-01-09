package com.fantomsoftware.tracker.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnResultTrackList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task_init_app return parameter
>
*/

public class Task_PointsParse extends AsyncTask<Void, Void, List<TrackPoint>>{
//--------------------------------------------------------------------
private OnResultTrackList on_track_parse_finished;
private JSONObject        data_json;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetDataJSON( JSONObject data_json ){
  this.data_json = data_json;
}//SetDataJSON
//--------------------------------------------------------------------
public void TrackSetOnParseFinished( OnResultTrackList on_track_parse_finished ){
  this.on_track_parse_finished = on_track_parse_finished;
}//SetOnSelected
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected List<TrackPoint> doInBackground( Void... voids ){

  Log.d( AppData.log_key, "Task_PointsParse" );

  ArrayList<TrackPoint> track = new ArrayList<>();

  JSONArray points_json;

  try{
    points_json = data_json.getJSONArray( "points" );
  }catch( JSONException e ){
    return track;
  }//catch


  for( int i = 0; i < points_json.length(); i++ ){

    try{
      JSONObject oneObject = points_json.getJSONObject( i );

      TrackPoint track_point = new TrackPoint();

      track_point.timestamp = oneObject.getLong( TrackPoint.COLUMN_TIMESTAMP );
      track_point.accuracy = oneObject.getInt( TrackPoint.COLUMN_ACCURACY );
      track_point.latitude = oneObject.getDouble( TrackPoint.COLUMN_LATITUDE );
      track_point.longitude = oneObject.getDouble( TrackPoint.COLUMN_LONGITUDE );

      track_point.UpdateDateTime();
      track_point.UpdatePosition();

      track.add( track_point );

    }catch( JSONException e ){
      return track;
    }//catch

  }//for

  return track;

}//doInBackground
//--------------------------------------------------------------------
@Override
protected void onPostExecute( List<TrackPoint> track ){

  super.onPostExecute( track );

  if( on_track_parse_finished != null ){
    on_track_parse_finished.OnResult( track );
  }//if

}//onPostExecute
//--------------------------------------------------------------------

}//Task_PointsParse