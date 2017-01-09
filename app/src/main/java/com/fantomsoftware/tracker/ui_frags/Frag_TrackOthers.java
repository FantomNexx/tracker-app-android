package com.fantomsoftware.tracker.ui_frags;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.cmps.CmpPointsGet;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnResultJSON;
import com.fantomsoftware.tracker.interfaces.OnResultTrackList;
import com.fantomsoftware.tracker.tasks.Task_PointsParse;
import com.fantomsoftware.tracker.utils.Utils;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONObject;

import java.util.List;


public class Frag_TrackOthers extends Fragment{
//--------------------------------------------------------------------
public static final String TAG = "Frag_TrackOthers";

private View root_view;

private CmpPointsGet     srv_request;
private Task_PointsParse task;

private MapView   map_view;
private MapboxMap map;

private Icon icon_track_point;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Nullable
@Override
public View onCreateView(
    LayoutInflater inflater, ViewGroup cntr, Bundle bundle ){

  root_view = inflater.inflate( R.layout.frag_track_others, cntr, false );

  Init();

  return root_view;

}//onCreateView
//--------------------------------------------------------------------
@Override
public void onStart(){
  super.onStart();
}//onStart
//--------------------------------------------------------------------
@Override
public void onStop(){
  super.onStop();
}//onStop
//--------------------------------------------------------------------
@Override
public void onResume(){
  super.onResume();
  if( map_view != null ) map_view.onResume();
}//onResume
//--------------------------------------------------------------------

@Override
public void onPause(){
  super.onPause();
  if( map_view != null ) map_view.onPause();
}//onPause
//--------------------------------------------------------------------
@Override
public void onSaveInstanceState( Bundle outState ){
  super.onSaveInstanceState( outState );
  if( map_view != null ) map_view.onSaveInstanceState( outState );
}//onSaveInstanceState
//--------------------------------------------------------------------
@Override
public void onLowMemory(){
  super.onLowMemory();
  if( map_view != null ) map_view.onLowMemory();
}//onLowMemory
//--------------------------------------------------------------------
@Override
public void onDestroy(){
  super.onDestroy();
  if( map_view != null ) map_view.onDestroy();
}//onDestroy
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Init(){

  InitIconTrackPoint();

  map_view = (MapView) root_view.findViewById( R.id.map );

  if( map_view == null ){
    return;
  }//if

  map_view.onCreate( null );

  OnMapReadyCallback on_map_ready = new OnMapReadyCallback(){
    @Override
    public void onMapReady( MapboxMap mapbox ){

      map = mapbox;
      RequestForTrack();

    }//onMapReady
  };//OnMapReadyCallback


  map_view.getMapAsync( on_map_ready );//

}//Init
//--------------------------------------------------------------------
private void InitIconTrackPoint(){

  IconFactory factory  = IconFactory.getInstance( AppData.context );
  Drawable    drawable = Utils.GetDrawable( R.drawable.ic_track_point );

  if( drawable == null ) return;

  icon_track_point = factory.fromDrawable( drawable );

}//InitIconTrackPoint
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void RequestForTrack(){

  OnResultJSON on_result = new OnResultJSON(){
    @Override
    public void OnResult( JSONObject data_json ){

      ParseTrack( data_json );

    }//OnResult
  };//on_result

  srv_request = new CmpPointsGet();
  srv_request.SetOnResult( on_result );
  srv_request.Request();

}//RequestForTrack
//--------------------------------------------------------------------
private void ParseTrack( JSONObject track_data ){

  if( track_data == null ){
    return;
  }//if

  OnResultTrackList on_track_parse_finished = new OnResultTrackList(){
    @Override
    public void OnResult( List<TrackPoint> track ){
      OnTrackParced( track );
    }//OnResult
  };//on_track_parse_finished


  task = new Task_PointsParse();
  task.SetDataJSON( track_data );
  task.TrackSetOnParseFinished( on_track_parse_finished );
  task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );

}//ParseTrack
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnTrackParced( List<TrackPoint> track ){

  if( map == null ) return;
  if( !(track.size() > 0) ) return;

  SetMapPosition( track.get( 0 ) );
  AddMarker_LastPoisition( track.get( 0 ) );

  //drawing path
  for( int i = 0; i < track.size() - 1; i++ ){

    if( i > 0 ){
      AddMarker_TrackPoint( track.get( i ) );
    }//if

    LatLng[] track_segment = new LatLng[2];
    track_segment[0] = track.get( i ).position;
    track_segment[1] = track.get( i + 1 ).position;

    //int alfa = (255 / (track.size() - i));
    int alfa = 255 - ((255 / track.size()) * (i + 2));

    map.addPolyline( new PolylineOptions()
        .add( track_segment )
        .color( Color.argb( alfa, 200, 0, 0 ) )
        .width( 4 ) );
  }//for


  //Draw Points on MapView
    /*
    map.addPolyline( new PolylineOptions()
        .add( track_points )
        .color( Color.parseColor( "#cc0000" ) )
        .width( 8 ) );
    */
    /*
    map.addPolyline( new PolylineOptions()
        .add( track_points )
        .color( Color.argb( 100, 200, 0, 0 ) )
        .width( 8 ) );
    */


}//OnTrackReceived
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void SetMapPosition( TrackPoint track_point ){

  LatLng target = track_point.position;
  double zoom   = 16;
  double tilt   = 45;

  CameraPosition camera_position = new CameraPosition.Builder()
      .target( target ).tilt( tilt ).zoom( zoom ).build();

  map.setCameraPosition( camera_position );
}
//--------------------------------------------------------------------
private void AddMarker_LastPoisition( TrackPoint track_point ){

  String accuracy = Utils.GetString( R.string.accuracy );
  String meters   = Utils.GetString( R.string.meters );

  String snippet = String.format(
      ("%s \n" + accuracy + ": %d" + meters),
      track_point.datetime, track_point.accuracy );

  map.addMarker( new MarkerOptions()
      .position( track_point.position )
      .title( "Alex Verzun" )
      .snippet( snippet )
  );

}//AddMarker_LastPoisition
//--------------------------------------------------------------------
private void AddMarker_TrackPoint( TrackPoint track_point ){

  if( icon_track_point == null ) return;

  String accuracy = Utils.GetString( R.string.accuracy );
  String meters   = Utils.GetString( R.string.meters );

  String snippet = String.format(
      ("%s \n" + accuracy + ": %d" + meters),
      track_point.datetime, track_point.accuracy );

  map.addMarker( new MarkerOptions()
      .position( track_point.position )
      .title( "Alex Verzun" )
      .snippet( snippet )
      .icon( icon_track_point )
  );

}//AddMarker_LastPoisition
//--------------------------------------------------------------------


}//Frag_TrackOthers

