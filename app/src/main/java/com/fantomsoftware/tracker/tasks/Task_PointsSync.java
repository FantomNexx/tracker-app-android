package com.fantomsoftware.tracker.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantomsoftware.tracker.cmps.CustomRequest;
import com.fantomsoftware.tracker.consts.ConstsRequest;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnEvent;
import com.fantomsoftware.tracker.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task_init_app return parameter
>
*/

public class Task_PointsSync extends AsyncTask<Void, Void, Boolean>{
//--------------------------------------------------------------------
private SparseArray<TrackPoint> points;
private OnEvent                 on_sync_ended;
private int                     request_counter;

private boolean    is_sync_ended   = false;
private String     last_point_time = null;
private TrackPoint last_point      = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetTrackPoints( SparseArray<TrackPoint> points ){
  this.points = points;
}
//--------------------------------------------------------------------
public void SetOnSyncEnded( OnEvent on_sync_ended ){
  this.on_sync_ended = on_sync_ended;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected Boolean doInBackground( Void... voids ){

  Log.d( AppData.log_key, "Task_PointsSync START" );

  if( points == null || points.size() == 0 ){
    //Log.d( AppData.log_key, "Task_PointsSync.doInBackground() Nothing to sync" );
    return false;
  }//if

  if( !Utils.IsInternetLive() ){
    //Log.d( AppData.log_key, "Task_PointsSync.doInBackground() No Internet" );
    return false;
  }//if

  request_counter = 0;
  is_sync_ended = false;

  RequestQueue queue = Volley.newRequestQueue( AppData.context );

  Response.Listener<JSONObject> l_response        = GetOnResponseHandler();
  Response.ErrorListener        l_error           = GetOnErrorHandler();
  List<Map<String, String>>     list_request_data = GetListRequests( points );

  String url = "http://fantomsoftware.com/fantom_tracker/tracker-server.php";

  for( Map<String, String> request_data : list_request_data ){

    CustomRequest request = new CustomRequest(
        Request.Method.POST, url, request_data, l_response, l_error );

    queue.add( request );

    request_counter++;
  }//for

  while( !is_sync_ended ){

    try{
      Thread.sleep( 100 );
    }catch( InterruptedException e ){
      e.printStackTrace();
    }//try
  }//while

  Log.d( AppData.log_key, "Task_PointsSync END" );

  return true;

}
//--------------------------------------------------------------------
@Override
protected void onPostExecute( Boolean result ){

  super.onPostExecute( result );

  if( on_sync_ended != null ){
    on_sync_ended.OnEvent();
  }//if

}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private Response.Listener<JSONObject> GetOnResponseHandler(){

  return new Response.Listener<JSONObject>(){
    @Override
    public void onResponse( JSONObject response ){

      request_counter--;

      JSONObject data_json  = null;
      boolean    is_json_ok = true;

      try{
        data_json = response.getJSONObject( "data" );
      }catch( JSONException e ){
        Log.d( AppData.log_key, "Bad JSON data" );
        is_json_ok = false;
      }

      if( is_json_ok ){
        SetSyncStateToDB( data_json );
      }//if

      OnSyncEnded();
    }//onResponse

  };//l_response

}
//--------------------------------------------------------------------
private Response.ErrorListener GetOnErrorHandler(){

  return new Response.ErrorListener(){
    @Override
    public void onErrorResponse( VolleyError error ){

      request_counter--;

      String msg = "Task_PointsSync.onErrorResponse (" + request_counter + ") ";
      msg += error.getMessage();
      Log.d( AppData.log_key, msg );

      OnSyncEnded();

    }//onErrorResponse

  };//l_error

}
//--------------------------------------------------------------------
private List<Map<String, String>> GetListRequests(
    SparseArray<TrackPoint> points_to_sync_all ){

  List<Map<String, String>> list_request_data = new ArrayList<>();
  SparseArray<TrackPoint>   points_to_sync    = new SparseArray<>();
  Map<String, String>       request_data      = null;

  TrackPoint track_point;
  JSONArray  points_to_sync_json;
  String     points_to_sync_str;

  int points_total       = points_to_sync_all.size();
  int idx_point_section  = 0;
  int points_per_request = 50;


  for( int idx_point = 0; idx_point < points_total; idx_point++ ){

    if( idx_point_section == 0 ){
      points_to_sync = new SparseArray<>();
    }//if it is a section beginning

    track_point = points_to_sync_all.valueAt( idx_point );
    points_to_sync.put( idx_point_section, track_point );

    idx_point_section++;

    if( idx_point_section < points_per_request &&
        idx_point < points_total - 1 ){
      continue;
    }//if it is not an end of a section


    idx_point_section = 0;

    points_to_sync_json = TrackPoint.GetJSONOArray( points_to_sync );
    points_to_sync_str = points_to_sync_json.toString();

    request_data = new HashMap<>();
    request_data.put( "request", ConstsRequest.SYNC_POINTS );
    request_data.put( "user_id", AppData.user_current_id );
    request_data.put( "user_password", AppData.user_current_password );
    request_data.put( "points_to_sync", points_to_sync_str );

    list_request_data.add( request_data );

  }//for

  return list_request_data;

}
//--------------------------------------------------------------------
private List<TrackPoint> GetListPointsSynced( JSONObject data_json ){

  if( data_json == null ){
    return null;
  }//if

  List<TrackPoint> list_points_synced = new ArrayList<>();

  JSONArray  points_synced_json;
  TrackPoint point;

  try{
    points_synced_json = data_json.getJSONArray( "points_synced" );
  }catch( JSONException e ){
    return null;
  }//catch


  for( int i = 0; i < points_synced_json.length(); i++ ){

    try{
      JSONObject oneObject = points_synced_json.getJSONObject( i );

      point = new TrackPoint();

      point.id = oneObject.getInt( "id" );
      point.id_user = oneObject.getInt( TrackPoint.COLUMN_ID_USER );

      list_points_synced.add( point );

    }catch( JSONException e ){
      return null;
    }//catch

  }//for

  return list_points_synced;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void SetSyncStateToDB( JSONObject data_json ){

  List<TrackPoint> list_points_synced = GetListPointsSynced( data_json );

  if( list_points_synced == null ){
    return;
  }//if not points to sync

  if( AppData.instance.db_track_points == null ){
    AppData.instance.InitDatabase();
  }//if no database

  //if we could not init database
  if( AppData.instance.db_track_points == null ){
    return;
  }//if database cannot be inited

  AppData.instance.db_track_points.SetSyncedState( list_points_synced );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnSyncEnded(){

  if( request_counter != 0 ){
    return;
  }

  if( last_point != null ){
    Utils.SetSharedPref( AppData.SP_POINT_LAST_ID, last_point.id );
  }//if


  ShowNotification_OnSyncEnded();

  //Log.d( AppData.log_key, "Task_PointsSync Sync Ended" );
  is_sync_ended = true;

}
//--------------------------------------------------------------------
private void ShowNotification_OnSyncEnded(){

  try{
    TrackPoint point = points.valueAt( points.size() - 1 );

    if( point != null ){
      last_point = point;
      last_point_time = last_point.datetime;
    }//if

  }catch( Exception e ){
    Log.d( AppData.log_key, "ScheduleSyncing.RequestSyncPoints: cannot get last point" );
    return;
  }//try

  String title   = "Tracker";
  String content = "Last synced at: " + last_point_time;

  AppData.NotificationShowSyncedLast( title, content );
}
//--------------------------------------------------------------------

}//Task_PointsSync