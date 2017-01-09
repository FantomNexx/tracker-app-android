package com.fantomsoftware.tracker.data;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.cmps.CustomExceptionHandler;
import com.fantomsoftware.tracker.data.db.DBTrackPoints;
import com.fantomsoftware.tracker.services.BroadcastReceiverSync;
import com.fantomsoftware.tracker.services.ScheduleSyncing;
import com.fantomsoftware.tracker.services.ScheduleTracking;
import com.fantomsoftware.tracker.services.ScheduleUpdating;
import com.fantomsoftware.tracker.services.ServiceSyncing;
import com.fantomsoftware.tracker.ui_acts.Act;
import com.fantomsoftware.tracker.utils.Utils;


public class AppData extends Application{
//--------------------------------------------------------------------
public static Context   context      = null;
public static AppData   instance     = null;
public static Resources resources    = null;
public static String    package_name = null;
public static String    log_key      = null;
public static int       density      = 1;

public static int LOCATION_ACCURACY_MIN          = 200;//minimal accuracy in meters
public static int TIME_PERIOD_SYNC_REPEAT        = 15000;//15 seconds
public static int TIME_PERIOD_MAP_UPDATE         = 15000;//15 seconds
public static int TIME_PERIOD_LOC_UPDATE_FASTEST = 1000;//1 seconds
public static int TIME_PERIOD_LOC_UPDATE_SLOWEST = 5000;//5 seconds
public static int TIME_PERIOD_MAX_DIFFERENCE     = 3600000;//60 minutes

public static String user_current_id       = "111";
public static String user_current_password = "111";
public static int    notification_id       = -1;

public static  String  IS_TRACKING  = "IS_TRACKING";
public static  String  IS_SYNCING   = "IS_SYNCING";
private static Boolean IS_UI_PAUSED = false;

public static String SP_POINT_LAST_ID   = "SP_POINT_LAST_ID";
public static String SP_NOTIFICATION_ID = "SP_NOTIFICATION_ID";

public static final String KEY_STOP_TRACKING  = "com.fantomsoftware.tracker.StopTracking";
public static final String KEY_START_TRACKING = "com.fantomsoftware.tracker.StartTracking";
public static final String KEY_SYNC           = "com.fantomsoftware.tracker.Sync";

public static final int STATE_UNKNOWN  = -1;
public static final int STATE_STARTED  = 1;
public static final int STATE_STOPPING = 2;
public static final int STATE_STOPPED  = 3;
public              int tracker_state  = -1;

public Storage               storage_local   = null;
public DBTrackPoints         db_track_points = null;
public AppCompatActivity     activity        = null;
public BroadcastReceiverSync sync_receiver   = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
public void onCreate(){
  super.onCreate();

  AppData.context = getApplicationContext();//AppData;
  AppData.instance = (AppData) getApplicationContext();//AppData;
  AppData.package_name = AppData.context.getPackageName();
  AppData.resources = AppData.context.getResources();
  AppData.log_key = "fgg";//AppData.package_name;

  AppData.density = (int) TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP, 1,
      AppData.resources.getDisplayMetrics() );

  storage_local = new Storage();
  storage_local.Init();

  sync_receiver = new BroadcastReceiverSync();

  AppData.InitCrashReporter();//todo uncomment
}
//--------------------------------------------------------------------
public void InitDatabase(){

  storage_local.Init();

  db_track_points = new DBTrackPoints();
  db_track_points.open();
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void OnResume(){
  IS_UI_PAUSED = false;
  TrackingResume();
}
//--------------------------------------------------------------------
public static void OnPause(){
  IS_UI_PAUSED = true;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void SetCurrentActivity( AppCompatActivity activity ){
  AppData.instance.activity = activity;
}
//--------------------------------------------------------------------
private static void InitCrashReporter(){

  if( Thread.getDefaultUncaughtExceptionHandler()
      instanceof CustomExceptionHandler ){
    return;
  }//if handler already set

  String path = AppData.instance.storage_local.GetAppRootFolder();

  Thread.setDefaultUncaughtExceptionHandler(
      new CustomExceptionHandler( path ) );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void ServiceStart( Class<?> service_class ){
  Intent intent = new Intent( AppData.context, service_class );
  AppData.instance.activity.startService( intent );
}
//--------------------------------------------------------------------
public static void ServiceStop( Class<?> service_class ){
  Intent intent = new Intent( AppData.context, service_class );
  AppData.instance.activity.stopService( intent );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void TrackingToggle(){
  if( AppData.IsTracking() ){
    AppData.TrackingStop();
  }else{
    AppData.TrackingStart();
  }//if
}
//--------------------------------------------------------------------
public static void TrackingResume(){
  if( AppData.IsTracking() ){
    AppData.TrackingStart();
  }else{
    AppData.TrackingStop();
  }//if
}
//--------------------------------------------------------------------
public static void TrackingStart(){

  Log.d( AppData.log_key, "||||||| AppData.TrackingStart()" );

  ScheduleTracking.ScheduleStart();
  ScheduleSyncing.Start();
  ScheduleUpdating.ScheduleStart();

  NotificationShowTrackingStarted();
}
//--------------------------------------------------------------------
public static void TrackingStop(){
  Log.d( AppData.log_key, "||||||| AppData.TrackingStop()" );
  ScheduleTracking.ScheduleStop();
}
//--------------------------------------------------------------------
public static void SyncingStart(){
  ScheduleSyncing.Start();
}
//--------------------------------------------------------------------
public static void SyncingSetStarted(){
  Utils.SetSharedPref( AppData.IS_SYNCING, true );
  Log.d( AppData.log_key, "SYNCING STARTED" );
}
//--------------------------------------------------------------------
public static void SyncingSetStopped(){
  Utils.SetSharedPref( AppData.IS_SYNCING, false );
  Log.d( AppData.log_key, "SYNCING STOPPED" );
}
//--------------------------------------------------------------------
public static void UpdatingStart(){
  ScheduleUpdating.ScheduleStart();
}
//--------------------------------------------------------------------
public static void UpdatingStop(){
  ScheduleUpdating.ScheduleStop();
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static boolean IsTracking(){
  return Utils.GetSharedPrefBool( AppData.IS_TRACKING );
  //return Utils.IsServiceRunning( ServiceTracking.class );
}//IsTracking
//--------------------------------------------------------------------
public static boolean IsSyncing(){
  return Utils.GetSharedPrefBool( AppData.IS_SYNCING );
  //return Utils.IsServiceRunning( ServiceSyncing.class );
}//IsSyncing
//--------------------------------------------------------------------
public static boolean IsUIPaused(){
  return IS_UI_PAUSED;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static NotificationCompat.Builder NotificationGet(){

  final Bitmap large_icon = BitmapFactory.decodeResource(
      AppData.resources, R.mipmap.ic_app_logo_tracker );


  Intent intent_content = new Intent( AppData.context, Act.class );

  intent_content.setFlags(
      Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );

  PendingIntent pending_intent = PendingIntent.getActivity(
      AppData.context, 0, intent_content, PendingIntent.FLAG_CANCEL_CURRENT );


  NotificationCompat.Builder notification_builder;

  notification_builder = new NotificationCompat.Builder( AppData.context );
  notification_builder.setDefaults( 0 );
  notification_builder.setPriority( Notification.PRIORITY_DEFAULT );
  notification_builder.setSmallIcon( R.drawable.ic_app_logo_tracker_notif );
  notification_builder.setLargeIcon( large_icon );
  notification_builder.setAutoCancel( false );
  notification_builder.setContentIntent( pending_intent );

  return notification_builder;
}
//--------------------------------------------------------------------
public static void NotificationShow( NotificationCompat.Builder notification_builder ){

  NotificationManager notification_manager = (NotificationManager)
      AppData.context.getSystemService( Context.NOTIFICATION_SERVICE );


  AppData.notification_id =
      Utils.GetSharedPrefInt( AppData.SP_NOTIFICATION_ID );

  if( AppData.notification_id == -1 ){
    AppData.notification_id = Utils.GenerateNotificationId();

    Utils.SetSharedPref(
        AppData.SP_NOTIFICATION_ID, AppData.notification_id );
  }//if


  notification_manager.notify(
      AppData.notification_id, notification_builder.build() );
}
//--------------------------------------------------------------------
public static void NotificationShowTrackingStarted(){

  String title   = "Tracker";
  String content = "Started at: " + Utils.GetCurrentTimeStamp();
  content = AppData.TrackerStateGetSting() + " " + content;


  Intent intent_action = new Intent( AppData.KEY_STOP_TRACKING );
  PendingIntent pending_intent = PendingIntent.getBroadcast(
      AppData.context, 0, intent_action, 0 );


  NotificationCompat.Builder notification_builder =
      AppData.NotificationGet();

  notification_builder.setContentTitle( title );
  notification_builder.setContentText( content );
  notification_builder.setOngoing( true );//cannot by swiped
  notification_builder.addAction( 0, "STOP", pending_intent );

  AppData.NotificationCancelAll();
  AppData.NotificationShow( notification_builder );
}
//--------------------------------------------------------------------
public static void NotificationShowSyncedLast( String title, String content ){

  Intent intent_action = new Intent( AppData.KEY_STOP_TRACKING );
  PendingIntent pending_intent = PendingIntent.getBroadcast(
      AppData.context, 0, intent_action, 0 );


  NotificationCompat.Builder notification_builder =
      AppData.NotificationGet();

  content = AppData.TrackerStateGetSting() + " " + content;

  notification_builder.setContentTitle( title );
  notification_builder.setContentText( content );
  notification_builder.setOngoing( true );//cannot by swiped

  if( AppData.TrackerStateGet() == STATE_STARTED ){
    notification_builder.addAction( 0, "STOP", pending_intent );
  }//if

  AppData.NotificationShow( notification_builder );
}
//--------------------------------------------------------------------
public static void NotificationShowSyncFinished(){

  String title   = "Tracker";
  String content = "Sync finished at: " + Utils.GetCurrentTimeStamp();
  content = AppData.TrackerStateGetSting() + " " + content;

  Intent intent_action = new Intent( AppData.KEY_START_TRACKING );
  PendingIntent pending_intent = PendingIntent.getBroadcast(
      AppData.context, 0, intent_action, 0 );


  NotificationCompat.Builder notification_builder =
      AppData.NotificationGet();

  notification_builder.setContentTitle( title );
  notification_builder.setContentText( content );
  notification_builder.setOngoing( false );//cannot by swiped
  notification_builder.addAction( 0, "START AGAIN", pending_intent );

  AppData.NotificationCancelAll();
  AppData.NotificationShow( notification_builder );

  Utils.RemoveSharedPref( AppData.SP_NOTIFICATION_ID );
}
//--------------------------------------------------------------------
public static void NotificationCancelAll(){

  if( AppData.context == null ){
    return;
  }//if

  NotificationManager notification_manager = (NotificationManager)
      AppData.context.getSystemService( Context.NOTIFICATION_SERVICE );

  notification_manager.cancelAll();
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private static String TrackerStateGetSting(){

  switch( AppData.TrackerStateGet() ){
    case STATE_STARTED:
      return "(STARTED)";

    case STATE_STOPPING:
      return "(STOPPING)";

    case STATE_STOPPED:
      return "(STOPPED)";

    default:
      return "";
  }//switch
}
//--------------------------------------------------------------------
private static int TrackerStateGet(){

  Boolean is_tracking = Utils.GetSharedPrefBool( AppData.IS_TRACKING );
  Boolean is_syncing  = Utils.GetSharedPrefBool( AppData.IS_SYNCING );

  if( !is_tracking && is_syncing ){
    return STATE_STOPPING;
  }else if( is_tracking ){
    return STATE_STARTED;
  }else{
    return STATE_STOPPED;
  }
}
//--------------------------------------------------------------------
private static void SaveTrackPoint( TrackPoint point ){

  if( AppData.instance.db_track_points == null ){
    AppData.instance.InitDatabase();
  }//if no database

  //if we could not init database
  if( AppData.instance.db_track_points == null ){
    return;
  }//if database cannot be inited

  AppData.instance.db_track_points.Add( point );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void Terminate(){

  AppData.NotificationCancelAll();
  ScheduleUpdating.ScheduleShutDown();

  try{
    ((AppData) AppData.instance).db_track_points.close();
  }catch( Exception e ){
    Log.d( log_key, "Failed to close DB: " + e.getMessage() );
    CustomExceptionHandler.LogException( e );
  }//try
}
//--------------------------------------------------------------------
public static void TerminateActivity(){

  if( instance.activity != null ){
    instance.activity.finish();
  }//if

  AppData.Terminate();

  android.os.Process.killProcess( android.os.Process.myPid() );
}
//--------------------------------------------------------------------
public static void RestartNow(){

  PackageManager package_mng  = AppData.context.getPackageManager();
  String         package_name = AppData.context.getPackageName();

  Intent intent = package_mng.getLaunchIntentForPackage( package_name );
  intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

  instance.activity.startActivity( intent );
}
//--------------------------------------------------------------------
public static void RestartDelayed(){

  if( AppData.context == null ){
    return;
  }//if

  Context context = AppData.context;

  Intent restart_intent = context.getPackageManager().
      getLaunchIntentForPackage( context.getPackageName() );

  PendingIntent pending_intent = PendingIntent.getActivity(
      context, 0, restart_intent, PendingIntent.FLAG_CANCEL_CURRENT );

  AlarmManager alarm_manager =
      (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );

  alarm_manager.set(
      AlarmManager.RTC, System.currentTimeMillis() + 1000, pending_intent );
}
//--------------------------------------------------------------------

}//AppData
