package com.fantomsoftware.tracker.utils;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.consts.ConstsAct;
import com.fantomsoftware.tracker.data.AppData;


public class PermissionInternet extends Permission{

//------------------------------------------------------------------
public static boolean IsGranted(){

  int state_internet = ActivityCompat.checkSelfPermission(
      AppData.context, Manifest.permission.INTERNET );

  int state_network = ActivityCompat.checkSelfPermission(
      AppData.context, Manifest.permission.ACCESS_NETWORK_STATE );

  return (state_internet == PackageManager.PERMISSION_GRANTED &&
      state_network == PackageManager.PERMISSION_GRANTED);

}//IsGranted
//------------------------------------------------------------------
public static void Request(){

  boolean is_should_storage_read =
      ActivityCompat.shouldShowRequestPermissionRationale(
          AppData.app_instance.activity, Manifest.permission.INTERNET );

  boolean is_should_storage_write =
      ActivityCompat.shouldShowRequestPermissionRationale(
          AppData.app_instance.activity, Manifest.permission.ACCESS_NETWORK_STATE );

  if( !(is_should_storage_read && is_should_storage_write) ){
    RequestUser();
    return;
  }//if need to ask user


  //if access already blocked - let user know
  View   view        = AppData.app_instance.activity.findViewById( android.R.id.content );
  String msg         = Utils.GetString( R.string.permission_app_needs_internet );
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
      Manifest.permission.INTERNET,
      Manifest.permission.ACCESS_NETWORK_STATE,
  };//permissions

  ActivityCompat.requestPermissions(
      AppData.app_instance.activity, permissions, ConstsAct.REQUEST_INTERNET );

}//RequestUser
//------------------------------------------------------------------

}//PermissionInternet
