package com.fantomsoftware.tracker.tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.interfaces.OnEvent;
import com.fantomsoftware.tracker.interfaces.OnResultString;
import com.fantomsoftware.tracker.utils.PermissionInternet;
import com.fantomsoftware.tracker.utils.PermissionLocation;
import com.fantomsoftware.tracker.utils.PermissionStorage;
import com.fantomsoftware.tracker.utils.Utils;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task_init_app return parameter
>
*/

public class Task_ApplicationInit extends AsyncTask<Void, Integer, Boolean>{
//--------------------------------------------------------------------
public static boolean is_waiting_for_permission = false;
public static boolean is_need_to_exit           = false;

private OnEvent        on_result = null;
private OnResultString on_update = null;

private String prefix_init     = "";
private String prefix_complete = "";
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResult( OnEvent on_result ){
  this.on_result = on_result;
}//SetOnSelected
//--------------------------------------------------------------------
public void SetOnUpdate( OnResultString on_update ){
  this.on_update = on_update;
}//SetOnSelected
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected Boolean doInBackground( Void... param ){

  prefix_init = Utils.GetString( R.string.app_init_initialization );
  prefix_complete = Utils.GetString( R.string.app_init_completed );

  publishProgress( 15, 250 );
  Utils.SetAppShortcutIcon();

  publishProgress( 25, 250 );
  if( !PermissionStorage() ){
    return false;
  }//if app_data has no permissions for storage_local access

  publishProgress( 35, 250 );
  if( !PermissionInternet() ){
    return false;
  }//if app_data has no permissions for internet access

  publishProgress( 45, 250 );
  if( !PermissionLocation() ){
    return false;
  }//if app_data has no permissions for location access


  publishProgress( 50, 250 );
  AppData.app_instance.InitDatabase();

  publishProgress( 100, 400 );

  return true;
}//doInBackground
//--------------------------------------------------------------------
@Override
protected void onProgressUpdate( Integer... progress ){

  String state_update = prefix_init + "\n" + progress[0] + "%";
  on_update.OnResult( state_update );

  try{
    Thread.sleep( progress[1] );
  }catch( InterruptedException e ){
    e.printStackTrace();
  }//try

}//onProgressUpdate
//--------------------------------------------------------------------
@Override
protected void onPostExecute( Boolean result ){

  if( result ){
    OnTaskSuccess();
  }else{
    OnTaskFailed();
  }//if

}//onPostExecute
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnTaskSuccess(){

  String state_success = prefix_complete + "\n" + "100%";

  on_update.OnResult( state_success );
  on_result.OnEvent();

}//OnTaskSuccess
//--------------------------------------------------------------------
private void OnTaskFailed(){

  View.OnClickListener on_action_click = new View.OnClickListener(){
    @Override
    public void onClick( View view ){
      AppData.TerminateActivity();
    }//onClick
  };//on_action_click


  Task_ApplicationInit.is_need_to_exit = true;

  String state_failed = Utils.GetString( R.string.app_init_failed );
  on_update.OnResult( state_failed );

  View   view     = AppData.app_instance.activity.findViewById( android.R.id.content );
  String msg      = Utils.GetString( R.string.app_init_failed_end );
  String btn_text = Utils.GetString( R.string.app_int_btn_close );

  if( view == null ){
    return;
  }//if

  Snackbar.make( view, msg, Snackbar.LENGTH_INDEFINITE ).
      setAction( btn_text, on_action_click ).show();

}//OnTaskSuccess
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private boolean PermissionStorage(){

  if( PermissionStorage.IsGranted() ){
    return true;
  }//if

  PermissionStorage.Request();

  WaitForPermission();

  return PermissionStorage.IsGranted();

}//PermissionStorage
//--------------------------------------------------------------------
private boolean PermissionInternet(){

  if( PermissionInternet.IsGranted() ){
    return true;
  }//if

  PermissionInternet.Request();

  WaitForPermission();

  return PermissionInternet.IsGranted();

}//PermissionInternet
//--------------------------------------------------------------------
private boolean PermissionLocation(){

  if( PermissionLocation.IsGranted() ){
    return true;
  }//if

  PermissionLocation.Request();

  WaitForPermission();

  return PermissionLocation.IsGranted();

}//PermissionLocation
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void WaitForPermission(){

  Task_ApplicationInit.is_waiting_for_permission = true;

  while( Task_ApplicationInit.is_waiting_for_permission ){

    try{
      Thread.sleep( 500 );
    }catch( InterruptedException e ){
      e.printStackTrace();
    }//try
  }//while

}//WaitForPermission
//--------------------------------------------------------------------


}//Task_ApplicationInit