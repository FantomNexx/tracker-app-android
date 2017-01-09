package com.fantomsoftware.tracker.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.SparseArray;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.ui_acts.Act;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils{

//--------------------------------------------------------------------
private static SharedPreferences GetSharedPreferences(){
  return AppData.context.getSharedPreferences(
      AppData.package_name, Context.MODE_PRIVATE );
}
//--------------------------------------------------------------------
public static void SetSharedPref( String key, int value ){
  GetSharedPreferences().edit().putInt( key, value ).apply();
}
//--------------------------------------------------------------------
public static void SetSharedPref( String key, boolean value ){
  GetSharedPreferences().edit().putBoolean( key, value ).apply();
}
//--------------------------------------------------------------------
public static int GetSharedPrefInt( String key ){
  return GetSharedPreferences().getInt( key, -1 );
}
//--------------------------------------------------------------------
public static boolean GetSharedPrefBool( String key ){
  return GetSharedPreferences().getBoolean( key, false );
}
//--------------------------------------------------------------------
public static boolean RemoveSharedPref( String key ){
  return GetSharedPreferences().edit().remove(key).commit();
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static Drawable GetDrawable( int id ){

  if( AppData.context == null ){
    return null;
  }//if

  if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
    return AppData.resources.getDrawable( id, AppData.context.getTheme() );
  }else{
    return AppData.resources.getDrawable( id );
  }//else
}
//--------------------------------------------------------------------
public static String GetString( int id ){

  if( AppData.context == null ){
    return "";
  }//if

  return AppData.context.getString( id );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static boolean IsServiceRunning( Class<?> service_class ){

  ActivityManager manager = (ActivityManager)
      AppData.context.getSystemService( Context.ACTIVITY_SERVICE );

  List<ActivityManager.RunningServiceInfo> services =
      manager.getRunningServices( Integer.MAX_VALUE );

  String service_name = service_class.getName();

  for( ActivityManager.RunningServiceInfo service_info : services ){

    if( service_name.equals(
        service_info.service.getClassName() ) ){
      return true;
    }//if
  }//for

  return false;

}
//--------------------------------------------------------------------
public static void StopService( Intent intent ){
  AppData.instance.activity.stopService( intent );
}
// --------------------------------------------------------------------
public static void StartService( Intent intent ){
  AppData.instance.activity.startService( intent );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static boolean IsInternetLive(){

  ConnectivityManager con_mngr =
      (ConnectivityManager) AppData.context.getSystemService(
          Context.CONNECTIVITY_SERVICE );

  NetworkInfo network_info = con_mngr.getActiveNetworkInfo();

  if( network_info != null && network_info.isConnected() ){
    return true;
  }//if

  return false;
}
//--------------------------------------------------------------------

//--------------------------------------------------------------------
public static int GenerateNotificationId(){
  long   time     = new Date().getTime();
  String tmpStr   = String.valueOf( time );
  String last4Str = tmpStr.substring( tmpStr.length() - 5 );

  return Integer.valueOf( last4Str );
}
//--------------------------------------------------------------------
@SuppressLint("AndroidLintSimpleDateFormat")
public static String GetCurrentTimeStamp(){

  final SimpleDateFormat formatter =
      new SimpleDateFormat( "yyyy MMM dd HH:mm:ss" );

  return GetFormattedDate( formatter );
}
//--------------------------------------------------------------------
@SuppressLint("AndroidLintSimpleDateFormat")
public static String GetCurrentTimeStampFile(){

  final SimpleDateFormat formatter =
      new SimpleDateFormat( "yyyy MMM dd HH_mm_ss" );

  return GetFormattedDate( formatter );
}
//--------------------------------------------------------------------
private static String GetFormattedDate( SimpleDateFormat formatter ){

  String formatted_timestamp;

  try{
    formatted_timestamp = formatter.format( new Date() );
  }catch( Exception e ){
    formatted_timestamp = "failed timestamp format";
  }//try


  return formatted_timestamp;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static <C> List<C> asList( SparseArray<C> sparseArray ){
  if( sparseArray == null ) return null;
  List<C> arrayList = new ArrayList<C>( sparseArray.size() );
  for( int i = 0; i < sparseArray.size(); i++ )
    arrayList.add( sparseArray.valueAt( i ) );
  return arrayList;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static void SetAppShortcutIcon(){

  if( AppData.context == null ){
    return;
  }//if


  if( GetSharedPrefBool( "isAppInstalled" ) ){
    return;
  }//if


  Intent shortcut_intent = new Intent( AppData.context, Act.class );
  shortcut_intent.setAction( Intent.ACTION_MAIN );

  Intent.ShortcutIconResource app_shortcut =
      Intent.ShortcutIconResource.fromContext(
          AppData.context, R.mipmap.ic_launcher );

  Intent intent = new Intent();
  intent.setAction( "com.android.launcher.action.INSTALL_SHORTCUT" );
  intent.putExtra( Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent );
  intent.putExtra( Intent.EXTRA_SHORTCUT_NAME, R.string.app_name );
  intent.putExtra( Intent.EXTRA_SHORTCUT_ICON_RESOURCE, app_shortcut );


  AppData.context.sendBroadcast( intent );

  SetSharedPref( "isAppInstalled", true );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------

//Get app folder sample1
/*
AppData           data            = AppData.GetInstance();
PackageManager package_manager = data.getPackageManager();
String         app_root_folder = "";

try{
  PackageInfo package_info =
      package_manager.getPackageInfo( AppData.package_name, 0 );

  app_root_folder = package_info.applicationInfo.dataDir;

}catch( PackageManager.NameNotFoundException e ){
  Log.d( AppData.log_key, "Error Package name not found: " + e.getMessage() );
}try
*/

//Get app folder sample2
//String app_root_folder = AppData.context.getFilesDir().getAbsolutePath();


/*
//HOW TO CALCULATE ELAPSED TIME
//start measuring elapsed time//////////////////////////////////////
Stopwatch stopwatch = Stopwatch.createStarted();
////////////////////////////////////////////////////////////////////

//stop and measure elapsed time/////////////////////////////////////
stopwatch.stop();
long elapsed = stopwatch.elapsed( TimeUnit.MILLISECONDS );
Log.d( AppData.log_key, "AppData.onCreate() " + elapsed );
////////////////////////////////////////////////////////////////////
*/


/*
//RUNI TASKS IN UI THREAD
Handler handler = new Handler( AppData.context.getMainLooper() );

Runnable runnable = new Runnable(){
  @Override
  public void run(){
    //WORK
  }run
};//runnable

handler.post( runnable );
*/


  /*
  //TIMER TASK ALTERNATIVE
  if( scheduler == null ){
    scheduler = Executors.newSingleThreadScheduledExecutor();
  }if

  final Runnable runnable = new Runnable(){
    public void run(){
      RequestGetNotSyncedPoints();
    }run
  };


  if( future != null ){
    future.cancel( true );
  }if

  future = scheduler.scheduleAtFixedRate(
      runnable, 0, AppData.TIME_PERIOD_SYNC_REPEAT,
      java.util.concurrent.TimeUnit.MILLISECONDS );
  */


}
