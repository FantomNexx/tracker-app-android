package com.fantomsoftware.tracker.cmps;


import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fantomsoftware.tracker.consts.ConstsAct;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class CmpLocationGet{
//--------------------------------------------------------------------
public interface OnResultTrackPoint{
  void OnResultTrackPoint( TrackPoint track_point );
}
//--------------------------------------------------------------------
private OnResultTrackPoint on_result_track_point;
private GoogleApiClient    google_api_client;
private LocationRequest    location_request;

private boolean is_requesting_location_updates = true;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResultTrackPoint(
    OnResultTrackPoint on_result_track_point ){
  this.on_result_track_point = on_result_track_point;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public CmpLocationGet(){

  InitGoogleAPI();
  InitLocationRequest();
  CheckLocationSettings();

}
//--------------------------------------------------------------------
private void InitGoogleAPI(){

  if( google_api_client != null ) return;

  GoogleApiClient.ConnectionCallbacks connection_callbacks =
      new GoogleApiClient.ConnectionCallbacks(){
        //----------------------------------------------------------
        @Override
        public void onConnected( @Nullable Bundle bundle ){

          if( is_requesting_location_updates ){
            StartLocationUpdates();
          }//if we need to request location updates

        }//onConnected
        //----------------------------------------------------------
        @Override
        public void onConnectionSuspended( int i ){
        }//onConnectionSuspended
        //----------------------------------------------------------
      };//connection_callbacks


  GoogleApiClient.OnConnectionFailedListener on_connection_failed =
      new GoogleApiClient.OnConnectionFailedListener(){
        //----------------------------------------------------------
        @Override
        public void onConnectionFailed(
            @NonNull ConnectionResult connectionResult ){

        }//onConnectionFailed
        //----------------------------------------------------------
      };//on_connection_failed


  google_api_client = new GoogleApiClient.Builder( AppData.context )
      .addConnectionCallbacks( connection_callbacks )
      .addOnConnectionFailedListener( on_connection_failed )
      .addApi( LocationServices.API )
      .build();

}
//--------------------------------------------------------------------
private void InitLocationRequest(){

  location_request = new LocationRequest();
  location_request.setInterval( AppData.TIME_PERIOD_LOC_UPDATE_FASTEST );
  location_request.setFastestInterval( AppData.TIME_PERIOD_LOC_UPDATE_SLOWEST );
  location_request.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void CheckLocationSettings(){

  LocationSettingsRequest.Builder builder =
      new LocationSettingsRequest.Builder()
          .addLocationRequest( location_request );

  ResultCallback<LocationSettingsResult> result_callback =
      new ResultCallback<LocationSettingsResult>(){
        //----------------------------------------------------------
        @Override
        public void onResult( @NonNull LocationSettingsResult result ){

          Status status      = result.getStatus();
          int    status_code = status.getStatusCode();

          switch( status_code ){

            case LocationSettingsStatusCodes.SUCCESS:
              // All location settings are satisfied. The client can
              // initialize location requests here.
              break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              // Location settings are not satisfied, but this can
              // be fixed by showing the user a dialog.
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().


              try{
                status.startResolutionForResult(
                    AppData.app_instance.activity,
                    ConstsAct.REQUEST_LOCATION_SETTINGHS );

              }catch( IntentSender.SendIntentException e ){
                // Ignore the error.
              }
              break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
              // Location settings are not satisfied.
              // However, we have no way to fix the settings so
              // we won't show the dialog.
              break;
          }//switch

        }//onResult
        //----------------------------------------------------------
      };//result_callback


  PendingResult<LocationSettingsResult> location_settings_result =
      LocationServices.SettingsApi.checkLocationSettings(
          google_api_client, builder.build() );

  location_settings_result.setResultCallback( result_callback );

}
//--------------------------------------------------------------------
private void StartLocationUpdates(){

  if( location_request == null ){
    return;
  }//if

  LocationListener location_listener = new LocationListener(){
    @Override
    public void onLocationChanged( Location location ){
      OnLocationChanged( location );
    }//onLocationChanged
  };//location_listener

  try{

    LocationServices.FusedLocationApi.requestLocationUpdates(
        google_api_client, location_request, location_listener );

  }catch( SecurityException e ){

  }//try

}
//--------------------------------------------------------------------
private void StopLocationUpdates(){

  LocationCallback location_callback = new LocationCallback(){
    @Override
    public void onLocationResult( LocationResult result ){
      super.onLocationResult( result );
    }//onLocationResult
  };//location_callback

  LocationServices.FusedLocationApi.removeLocationUpdates(
      google_api_client, location_callback );

}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void Start(){
  google_api_client.connect();
}
//--------------------------------------------------------------------
public void Stop(){
  google_api_client.disconnect();
}
//--------------------------------------------------------------------
public void Pause(){
  StopLocationUpdates();
}
//--------------------------------------------------------------------
public void Resume(){

  if( google_api_client.isConnected() &&
      is_requesting_location_updates ){
    StartLocationUpdates();
  }//if

}
//--------------------------------------------------------------------

//--------------------------------------------------------------------
private void OnLocationChanged( Location location ){

  if( location == null || on_result_track_point == null ){
    return;
  }//if

  //location_current = location;

  TrackPoint track_point = new TrackPoint();

  track_point.accuracy = (int) location.getAccuracy();
  track_point.latitude = location.getLatitude();
  track_point.longitude = location.getLongitude();

  try{
    track_point.id_user = Integer.parseInt( AppData.user_current_id );
  }catch( NumberFormatException nfe ){
    track_point.id_user = 0;
  }

  track_point.SetTimestampCurrent();

  on_result_track_point.OnResultTrackPoint( track_point );

}
//--------------------------------------------------------------------


}//CmpLocationGet
