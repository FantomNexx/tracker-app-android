package com.fantomsoftware.tracker.utils;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.consts.ConstsAct;
import com.fantomsoftware.tracker.data.AppData;


public class PermissionLocation extends Permission{

  //------------------------------------------------------------------
  public static boolean IsGranted(){

    int state_coarse_location = ActivityCompat.checkSelfPermission(
        AppData.context, Manifest.permission.ACCESS_COARSE_LOCATION );

    int state_fine_location = ActivityCompat.checkSelfPermission(
        AppData.context, Manifest.permission.ACCESS_FINE_LOCATION );

    return (state_coarse_location == PackageManager.PERMISSION_GRANTED &&
        state_fine_location == PackageManager.PERMISSION_GRANTED);

  }//IsGranted
  //------------------------------------------------------------------
  public static void Request(){

    boolean is_should_coarse_location =
        ActivityCompat.shouldShowRequestPermissionRationale(
            AppData.app_instance.activity, Manifest.permission.ACCESS_COARSE_LOCATION );

    boolean is_should_fine_location =
        ActivityCompat.shouldShowRequestPermissionRationale(
            AppData.app_instance.activity, Manifest.permission.ACCESS_FINE_LOCATION );

    if( !(is_should_coarse_location && is_should_fine_location) ){
      RequestUser();
      return;
    }//if need to ask user


    //if access already blocked - let user know

    View view = AppData.app_instance.activity.findViewById( android.R.id.content );
    String msg = Utils.GetString( R.string.permission_app_needs_location );
    String action_text = Utils.GetString( R.string.permission_btn_choose );


    if( view == null ){
      return;
    }//if


    View.OnClickListener cl_grant = new View.OnClickListener(){
      @Override
      public void onClick( View view ){
        RequestUser();
      }//onClick
    };//cl_grant

    Snackbar.make( view, msg, Snackbar.LENGTH_INDEFINITE ).
        setAction( action_text, cl_grant ).show();

  }//RequestSync
  //------------------------------------------------------------------
  public static void RequestUser(){

    String[] permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    };//permissions

    ActivityCompat.requestPermissions(
        AppData.app_instance.activity, permissions, ConstsAct.REQUEST_LOCATION );

  }//RequestUser
  //------------------------------------------------------------------

}//PermissionLocation
