package com.fantomsoftware.tracker.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.SparseArray;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.Storage;
import com.fantomsoftware.tracker.data.TrackPoint;
import com.fantomsoftware.tracker.utils.Utils;
import com.google.common.util.concurrent.ExecutionError;

import java.util.List;


public class DBTrackPoints{
//--------------------------------------------------------------------
public static String TABLE_NAME = "trackpoints";

private SQLiteDatabase database;
private DBHelper       database_helper;

private String[] db_columns = {
    TrackPoint.COLUMN_ID_LOCAL,
    TrackPoint.COLUMN_ID,
    TrackPoint.COLUMN_ID_USER,
    TrackPoint.COLUMN_TIMESTAMP,
    TrackPoint.COLUMN_ACCURACY,
    TrackPoint.COLUMN_LATITUDE,
    TrackPoint.COLUMN_LONGITUDE,
    TrackPoint.COLUMN_IS_SYNCED};

//--------------------------------------------------------------------


//--------------------------------------------------------------------
public static String GetDBCreateSql(){

  return "create table " + TABLE_NAME + " ("
      + "id integer primary key autoincrement,"
      + "id_srv_db integer,"
      + "id_user integer,"

      + "timestamp long,"
      + "accuracy integer,"

      + "latitude real,"
      + "longitude real,"

      + "is_synced int"
      + ");";
}
//--------------------------------------------------------------------

//--------------------------------------------------------------------
public DBTrackPoints(){
  database_helper = new DBHelper( AppData.context );
}
//--------------------------------------------------------------------
private boolean CreatetableIfNotExist(){

  String sql_query = "select DISTINCT tbl_name from " +
      "sqlite_master where tbl_name = '" + TABLE_NAME + "'";

  Cursor cursor;

  boolean is_table_exist = false;

  try{
    cursor = database.rawQuery( sql_query, null );

    if( cursor != null && cursor.getCount() > 0 ){
      is_table_exist = true;
    }
  }catch( Exception e ){
    Log.d( AppData.log_key, "DBHelper.IsTableExists, failed" );
    return false;
  }

  if( cursor != null ){
    cursor.close();
  }

  if( !is_table_exist ){
    try{
      database.execSQL( DBTrackPoints.GetDBCreateSql() );
    }catch( Exception e ){
      Log.d( AppData.log_key, "DBHelper.onCreate, DB create failed" );
      return false;
    }//try
  }//if

  return true;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void open(){

  try{
    if( database == null || !database.isOpen() ){

      int flags = SQLiteDatabase.CREATE_IF_NECESSARY |
          SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;

      database = SQLiteDatabase.openDatabase(
          Storage.getFullPathDatabase(), null, flags );

      CreatetableIfNotExist();
    }
  }catch( Exception e ){
    Log.d( AppData.log_key, "DBTrackPoints.close() Failed" + e.getMessage() );
  }
}
//--------------------------------------------------------------------
public void close(){
  try{
    database_helper.close();
  }catch( Exception e ){
    Log.d( AppData.log_key, "DBTrackPoints.close() Failed, " + e.getMessage() );
  }
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public int Add( TrackPoint item ){

  Log.d( AppData.log_key, "DBTrackPoints.Add()" );

  ContentValues db_item_values = new ContentValues();

  db_item_values.put( TrackPoint.COLUMN_ID, item.id_srv_db );
  db_item_values.put( TrackPoint.COLUMN_ID_USER, item.id_user );
  db_item_values.put( TrackPoint.COLUMN_TIMESTAMP, item.timestamp );
  db_item_values.put( TrackPoint.COLUMN_ACCURACY, item.accuracy );
  db_item_values.put( TrackPoint.COLUMN_LATITUDE, item.latitude );
  db_item_values.put( TrackPoint.COLUMN_LONGITUDE, item.longitude );
  db_item_values.put( TrackPoint.COLUMN_IS_SYNCED, item.is_synced );

  //open();

  int result_id = (int) database.insert( TABLE_NAME, null, db_item_values );

  //Log.d( AppData.log_key, "DBTrackPoints.Add() END" );

  return result_id;
}
//--------------------------------------------------------------------
public SparseArray<TrackPoint> GetLatest( int count ){

  Log.d( AppData.log_key, "DBTrackPoints.GetLatest()" );
  //Log.d( AppData.log_key, "DBTrackPoints.GetLatest() START" );

  Cursor cursor;

  String point_last_id =
      "" + Utils.GetSharedPrefInt( AppData.SP_POINT_LAST_ID );

  try{
    //open();

    cursor = database.query(
        TABLE_NAME,//String table
        db_columns,//String[] columns
        "id > ?",//String selection
        new String[]{point_last_id},//String[] selectionArgs
        null,//String groupBy
        null,//String having
        TrackPoint.COLUMN_TIMESTAMP + " DESC",//String orderBy
        "" + count//String limit
    );

  }catch( ExecutionError error ){
    return null;
  }//try

  //Log.d( AppData.log_key, "DBTrackPoints.GetLatest() END" );

  return CursorToTrackPoints( cursor );
}
//--------------------------------------------------------------------
public SparseArray<TrackPoint> GetNotSynced( int count ){

  Log.d( AppData.log_key, "DBTrackPoints.GetNotSynced()" );

  Cursor cursor;

  try{
    //open();

    cursor = database.query(
        TABLE_NAME,//String table
        db_columns,//String[] columns
        TrackPoint.COLUMN_IS_SYNCED + " = 0",//String selection
        null,//String[] selectionArgs
        null,//String groupBy
        null,//String having
        TrackPoint.COLUMN_TIMESTAMP + " DESC",//String orderBy
        "" + count//String limit
    );

  }catch( ExecutionError error ){
    return null;
  }//try

  //Log.d( AppData.log_key, "DBTrackPoints.GetNotSynced() END" );

  return CursorToTrackPoints( cursor );
}
//--------------------------------------------------------------------
public boolean SetSyncedState( List<TrackPoint> points ){

  Log.d( AppData.log_key, "DBTrackPoints.SetSyncedState()" );

  try{
    //open();

    String sql = "UPDATE " + TABLE_NAME +
        " SET " + TrackPoint.COLUMN_IS_SYNCED + " = 1" +
        " WHERE " + TrackPoint.COLUMN_ID_LOCAL + " = ?";

    //database.beginTransaction();
    database.beginTransactionNonExclusive();

    SQLiteStatement sql_statement = database.compileStatement( sql );

    for( TrackPoint point : points ){
      sql_statement.bindLong( 1, point.id );
      sql_statement.execute();
    }//for

    database.setTransactionSuccessful();

  }catch( Exception e ){
    Log.d( AppData.log_key, "DBTrackPoints.SetSyncedState: " + e.getMessage() );
    return false;

  }finally{
    database.endTransaction();
  }//try

  //Log.d( AppData.log_key, "DBTrackPoints.SetSyncedState() END" );

  return true;
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private TrackPoint CursorToTrackPoint( Cursor cursor ){

  if( cursor == null ){
    return null;
  }//if

  TrackPoint point = new TrackPoint();

  point.id = cursor.getInt( 0 );
  point.id_srv_db = cursor.getInt( 1 );
  point.id_user = cursor.getInt( 2 );
  point.timestamp = cursor.getLong( 3 );
  point.accuracy = cursor.getInt( 4 );
  point.latitude = cursor.getDouble( 5 );
  point.longitude = cursor.getDouble( 6 );
  point.is_synced = cursor.getInt( 7 );

  point.UpdateDateTime();
  point.UpdatePosition();

  return point;
}
//--------------------------------------------------------------------
private SparseArray<TrackPoint> CursorToTrackPoints( Cursor cursor ){

  if( cursor == null ){
    return null;
  }//if

  SparseArray<TrackPoint> points = new SparseArray<>();

  cursor.moveToFirst();

  while( !cursor.isAfterLast() ){
    TrackPoint point = CursorToTrackPoint( cursor );
    points.put( points.size(), point );
    cursor.moveToNext();
  }//while

  cursor.close();

  return points;
}
//--------------------------------------------------------------------

}//DBTrackPoints
