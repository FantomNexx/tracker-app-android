package com.fantomsoftware.tracker.ui_frags;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.interfaces.OnResultTrackArray;
import com.fantomsoftware.tracker.tasks.Task_PointsGetLatest;
import com.fantomsoftware.tracker.utils.Utils;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;


public class Frag_TrackMe extends Fragment{
//--------------------------------------------------------------------
public static final String TAG = "Frag_TrackMe";

public static Handler handler_update_map = null;

private View     root_view;
private MenuItem menu_item_toggle_track;

private MapboxMap map;
private MapView   map_view;
private Icon      icon_track_point_synced;
private Icon      icon_track_point_synced_not;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Nullable
@Override
public View onCreateView(
    LayoutInflater inflater, ViewGroup vg, Bundle bundle ){

  root_view = inflater.inflate( R.layout.frag_track_me, vg, false );
  setHasOptionsMenu( true );

  Init();

  return root_view;
}
//--------------------------------------------------------------------

//--------------------------------------------------------------------
public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ){
  super.onCreateOptionsMenu( menu, inflater );
  inflater.inflate( R.menu.menu_track_me, menu );
}
//--------------------------------------------------------------------
@Override
public void onPrepareOptionsMenu( Menu menu ){
  super.onPrepareOptionsMenu( menu );

  menu_item_toggle_track = menu.findItem( R.id.menu_item_toggle_track );

  UpdateABMenuItem();
}
//--------------------------------------------------------------------
@Override
public boolean onOptionsItemSelected( MenuItem item ){

  switch( item.getItemId() ){

    case R.id.menu_item_toggle_track:
      AppData.TrackingToggle();
      UpdateABMenuItem();
      break;

    case R.id.menu_item_refresh:
      AppData.UpdatingStart();
      break;

    case R.id.menu_item_sync:
      AppData.SyncingStart();
      break;

    case R.id.menu_item_exp:
      AppData.RestartNow();
      break;

    default:
      return super.onOptionsItemSelected( item );
  }//switch

  return true;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
public void onSaveInstanceState( Bundle outState ){
  super.onSaveInstanceState( outState );
  if( map_view != null ) map_view.onSaveInstanceState( outState );
}
//--------------------------------------------------------------------
@Override
public void onResume(){
  super.onResume();
  if( map_view != null ){ map_view.onResume(); }//if
  AppData.OnResume();
}
//--------------------------------------------------------------------
@Override
public void onPause(){
  super.onPause();
  if( map_view != null ) map_view.onPause();
  AppData.OnPause();
}
//--------------------------------------------------------------------
@Override
public void onLowMemory(){
  super.onLowMemory();
  if( map_view != null ) map_view.onLowMemory();
}
//--------------------------------------------------------------------
@Override
public void onDestroy(){
  super.onDestroy();
  if( map_view != null ) map_view.onDestroy();
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Init(){
  InitMapUpdateHandler();
  InitMapIcons();
  InitMap();
  InitBroadcasts();
}
//--------------------------------------------------------------------
private void InitMapUpdateHandler(){
  handler_update_map = new Handler(){
    public void handleMessage( Message msg ){
      super.handleMessage( msg );
      Map_UpdateTrack();
    }//handleMessage
  };
}
//--------------------------------------------------------------------
private void InitMap(){

  map_view = (MapView) root_view.findViewById( R.id.map );

  map_view.onCreate( null );

  OnMapReadyCallback on_map_ready = new OnMapReadyCallback(){
    @Override
    public void onMapReady( MapboxMap mapbox ){

      map_view.setAlpha( 1 );
      map = mapbox;

      Map_UpdateTrack();

    }//onMapReady
  };//OnMapReadyCallback

  map_view.getMapAsync( on_map_ready );
}
//--------------------------------------------------------------------
private void InitMapIcons(){

  IconFactory factory = IconFactory.getInstance( AppData.context );

  Drawable drawable_synced     = Utils.GetDrawable( R.drawable.ic_tack_point_synced );
  Drawable drawable_synced_not = Utils.GetDrawable( R.drawable.ic_tack_point_synced_not );

  if( drawable_synced == null ) return;
  if( drawable_synced_not == null ) return;

  icon_track_point_synced = factory.fromDrawable( drawable_synced );
  icon_track_point_synced_not = factory.fromDrawable( drawable_synced_not );
}
//--------------------------------------------------------------------
private void InitBroadcasts(){

  IntentFilter filter = new IntentFilter();
  filter.addAction( AppData.KEY_START_TRACKING );
  filter.addAction( AppData.KEY_STOP_TRACKING );

  BroadcastReceiver receiver = new BroadcastReceiver(){
    @Override
    public void onReceive( Context context, Intent intent ){

      switch( intent.getAction() ){

        case AppData.KEY_START_TRACKING:
          AppData.NotificationCancelAll();
          AppData.TrackingStart();
          UpdateABMenuItem();
          break;

        case AppData.KEY_STOP_TRACKING:
          AppData.NotificationCancelAll();
          AppData.TrackingStop();
          UpdateABMenuItem();
          break;
      }//switch
    }//onReceive
  };

  AppData.context.registerReceiver( receiver, filter );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void UpdateABMenuItem(){

  if( menu_item_toggle_track == null ){
    return;
  }//if

  if( AppData.IsTracking() ){
    menu_item_toggle_track.setTitle( R.string.stop_track );
  }else{
    menu_item_toggle_track.setTitle( R.string.start_track );
  }//if

}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void Map_UpdateTrack(){

  OnResultTrackArray on_result = new OnResultTrackArray(){
    @Override
    public void OnResult( SparseArray<TrackPoint> track ){
      //Log.d( AppData.log_key, "Frag_TrackMe.Map_UpdateTrack() ON_RESULT" );
      Map_DrawTrack( track );
    }//OnResult
  };//on_track_parse_finished

  Task_PointsGetLatest task = new Task_PointsGetLatest();
  task.SetOnResult( on_result );
  task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
}
//--------------------------------------------------------------------
private void Map_DrawTrack( SparseArray<TrackPoint> track ){

  Log.d( AppData.log_key, "Frag_TrackMe.Map_DrawTrack()" );

  if( map == null ) return;
  if( track == null ) return;
  if( track.size() <= 2 ) return;


  TrackPoint track_point;

  List<MarkerOptions> list_markers        = new ArrayList<>();
  List<LatLng>        list_points_segment = new ArrayList<>();


  for( int i = 1; i < track.size(); i++ ){

    track_point = track.valueAt( i );

    list_points_segment.add( track_point.position );

    if( track_point.is_synced == 1 ){
      list_markers.add( new MarkerOptions()
          .position( track_point.position )
          .icon( icon_track_point_synced )
      );//add
    }else{
      list_markers.add( new MarkerOptions()
          .position( track_point.position )
          .icon( icon_track_point_synced_not )
      );//add
    }//if

  }//for

  LatLng[] points_segment = new LatLng[list_points_segment.size()];
  list_points_segment.toArray( points_segment );

  Map_Clear();

  Map_SetCameraPosition( track.valueAt( 0 ) );

  map.addPolyline( new PolylineOptions()
      .add( points_segment )
      .color( Color.argb( 150, 200, 0, 0 ) )
      .width( 4 ) );

  map.addMarkers( list_markers );

  Map_AddMarkerLastPoint( track.valueAt( 0 ) );

  //Log.d( AppData.log_key, "Frag_TrackMe.Map_DrawTrack() END" );
}
//--------------------------------------------------------------------
private void Map_SetCameraPosition( TrackPoint track_point ){

  LatLng target = track_point.position;
  double zoom   = 16;
  double tilt   = 0;

  CameraPosition camera_position = new CameraPosition.Builder()
      .target( target ).tilt( tilt ).zoom( zoom ).build();

  map.setCameraPosition( camera_position );
}
//--------------------------------------------------------------------
private void Map_AddMarkerLastPoint( TrackPoint track_point ){

  String accuracy = Utils.GetString( R.string.accuracy );
  String meters   = Utils.GetString( R.string.meters );

  String snippet = String.format(
      ("%s \n" + accuracy + ": %d" + meters),
      track_point.datetime, track_point.accuracy );

  map.addMarker(
      new MarkerOptions()
          .position( track_point.position )
          .title( "Alex Verzun" )
          .snippet( snippet )
  );//map.addMarker
}
//--------------------------------------------------------------------
private void Map_Clear(){
  //Log.d( AppData.log_key, "Map_Clear START" );
  for( Marker marker : map.getMarkers() ){
    map.removeMarker( marker );
  }//for

  map.removeAnnotations();
  //Log.d( AppData.log_key, "Map_Clear END" );
}
//--------------------------------------------------------------------


}//Frag_TrackMe

