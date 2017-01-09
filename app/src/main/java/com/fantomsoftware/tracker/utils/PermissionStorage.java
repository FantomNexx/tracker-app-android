package com.fantomsoftware.tracker.utils;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.consts.ConstsAct;
import com.fantomsoftware.tracker.data.AppData;


public class PermissionStorage extends Permission{

  //------------------------------------------------------------------
  public static boolean IsGranted(){

    int state_storage_read = ActivityCompat.checkSelfPermission(
        AppData.context, Manifest.permission.READ_EXTERNAL_STORAGE );

    int state_storage_write = ActivityCompat.checkSelfPermission(
        AppData.context, Manifest.permission.WRITE_EXTERNAL_STORAGE );

    return (state_storage_read == PackageManager.PERMISSION_GRANTED &&
        state_storage_write == PackageManager.PERMISSION_GRANTED);
  }//IsGranted
  //------------------------------------------------------------------
  public static void Request(){

    boolean is_should_storage_read =
        ActivityCompat.shouldShowRequestPermissionRationale(
            AppData.app_instance.activity, Manifest.permission.READ_EXTERNAL_STORAGE );

    boolean is_should_storage_write =
        ActivityCompat.shouldShowRequestPermissionRationale(
            AppData.app_instance.activity, Manifest.permission.WRITE_EXTERNAL_STORAGE );

    if( !(is_should_storage_read && is_should_storage_write) ){
      RequestUser();
      return;
    }//if need to ask user


    //if access already blocked - let user know

    View view = AppData.app_instance.activity.findViewById( android.R.id.content );
    String msg = Utils.GetString( R.string.permission_app_needs_storage );
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
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };//permissions

    ActivityCompat.requestPermissions(
        AppData.app_instance.activity, permissions, ConstsAct.REQUEST_STORAGE );

  }//RequestUser
  //------------------------------------------------------------------

}//PermissionInternet
