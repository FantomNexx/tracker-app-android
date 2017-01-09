package com.fantomsoftware.tracker.data;


import android.util.SparseArray;

import com.fantomsoftware.tracker.utils.UtilsDate;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.GregorianCalendar;


public class TrackPoint{
//--------------------------------------------------------------------
public int id        = -1;
public int id_srv_db = -1;
public int id_user   = -1;

public long   timestamp = 0;
public int    accuracy  = 0;
public double latitude  = 0;
public double longitude = 0;

public int is_synced = 0;

public String datetime = "";
public LatLng position = null;

public static final String COLUMN_ID_LOCAL  = "id";
public static final String COLUMN_ID        = "id_srv_db";
public static final String COLUMN_ID_USER   = "id_user";
public static final String COLUMN_TIMESTAMP = "timestamp";
public static final String COLUMN_ACCURACY  = "accuracy";
public static final String COLUMN_LATITUDE  = "latitude";
public static final String COLUMN_LONGITUDE = "longitude";
public static final String COLUMN_IS_SYNCED = "is_synced";

//--------------------------------------------------------------------


//--------------------------------------------------------------------
public TrackPoint(){
}//TrackPoint
//--------------------------------------------------------------------
public void UpdateDateTime(){
  datetime = UtilsDate.LongToDateStringFull( timestamp );
}//UpdateDateTime
//--------------------------------------------------------------------
public void UpdatePosition(){
  position = new LatLng( latitude, longitude );
}//UpdatePosition
//--------------------------------------------------------------------
public void SetTimestampCurrent(){
  timestamp = UtilsDate.DateToLong( new GregorianCalendar() );
}//SetTimestampCurrent
//--------------------------------------------------------------------

//--------------------------------------------------------------------
public static JSONObject GetJSONObject( TrackPoint points ){

  JSONObject json_object = new JSONObject();

  try{
    json_object.put( COLUMN_ID_LOCAL, points.id );
    json_object.put( COLUMN_ID_USER, points.id_user );
    json_object.put( COLUMN_TIMESTAMP, points.timestamp );
    json_object.put( COLUMN_ACCURACY, points.accuracy );
    json_object.put( COLUMN_LATITUDE, points.latitude );
    json_object.put( COLUMN_LONGITUDE, points.longitude );

  }catch( JSONException e ){
  }//try

  return json_object;

}//GetJSONObject
//--------------------------------------------------------------------
public static JSONArray GetJSONOArray( SparseArray<TrackPoint> points ){

  JSONArray json_array = new JSONArray();

  TrackPoint track_point;
  JSONObject track_point_json;

  for( int i = 0; i < points.size(); i++ ){

    track_point = points.valueAt( i );
    track_point_json = GetJSONObject( track_point );

    json_array.put( track_point_json );
  }//for

  return json_array;

}//GetJSONObject
//--------------------------------------------------------------------

}//TrackPoint
