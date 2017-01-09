package com.fantomsoftware.tracker.ui_acts;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.consts.ConstsAct;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.interfaces.OnEvent;
import com.fantomsoftware.tracker.interfaces.OnResultString;
import com.fantomsoftware.tracker.tasks.Task_ApplicationInit;


public class Act_Splash extends AppCompatActivity{
//--------------------------------------------------------------------
private TextView tv_state = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected void onCreate( Bundle savedInstanceState ){
  super.onCreate( savedInstanceState );
  setContentView( R.layout.act_splash );
  AppData.SetCurrentActivity( this );
}
//--------------------------------------------------------------------
@Override
protected void onResume(){
  super.onResume();
  AppData.SetCurrentActivity( this );
}
//--------------------------------------------------------------------
@Override
protected void onDestroy(){
  super.onDestroy();
}//onDestroy
//--------------------------------------------------------------------
@Override
public void onAttachedToWindow(){
  super.onAttachedToWindow();
  StartTask_InitApplication();
}
//--------------------------------------------------------------------
@Override
public void onBackPressed(){
  super.onBackPressed();

  if( Task_ApplicationInit.is_need_to_exit ){
    AppData.TerminateActivity();
  }//if
}
//--------------------------------------------------------------------
@Override
public void onRequestPermissionsResult(
    int request,
    @NonNull String permissions[],
    @NonNull int[] grant_permissions ){

  switch( request ){
    case ConstsAct.REQUEST_STORAGE:
    case ConstsAct.REQUEST_INTERNET:
    case ConstsAct.REQUEST_LOCATION:
      Task_ApplicationInit.is_waiting_for_permission = false;
      break;
  }//switch
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void StartTask_InitApplication(){

  OnEvent on_success = new OnEvent(){
    @Override
    public void OnEvent(){
      Exit();
    }//OnEvent
  };//event

  OnResultString on_update = new OnResultString(){
    @Override
    public void OnResult( String value ){
      SetState( value );
    }//OnEvent
  };//event

  Task_ApplicationInit task = new Task_ApplicationInit();
  task.SetOnResult( on_success );
  task.SetOnUpdate( on_update );
  task.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetState( String state ){

  if( tv_state == null ){
    tv_state = (TextView) findViewById( R.id.tv_state );
  }//if

  if( tv_state != null ){
    tv_state.setText( state );
  }//if
}
//--------------------------------------------------------------------
public void Exit(){
  Intent intent = new Intent( this, Act.class );
  startActivity( intent );
  finish();
}
//--------------------------------------------------------------------

}//Act_Splash
